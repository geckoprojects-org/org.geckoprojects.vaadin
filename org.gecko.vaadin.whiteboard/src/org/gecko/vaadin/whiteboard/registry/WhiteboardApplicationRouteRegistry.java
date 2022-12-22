/**
 * Copyright (c) 2012 - 2022 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the terms of the 
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.gecko.vaadin.whiteboard.registry;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.gecko.vaadin.whiteboard.registry.FrontendRegistry.FrontendEntry;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceObjects;

import com.vaadin.flow.server.PWA;
import com.vaadin.flow.server.VaadinContext;
import com.vaadin.flow.server.startup.ApplicationRouteRegistry;

/**
 * Whiteboard version of the {@link ApplicationRouteRegistry}, that can deal with services as incoming classes.
 * It also works as {@link ServiceObjectRegistry}
 * 
 * @author Mark Hoffmann
 */
public class WhiteboardApplicationRouteRegistry extends ApplicationRouteRegistry implements ServiceObjectRegistry<Object> {

	private static final Logger logger = Logger.getLogger(WhiteboardApplicationRouteRegistry.class.getName());
	private static final long serialVersionUID = 1L;
	private final FrontendRegistry frontendRegistry;
	private final Map<Long, ServiceObjectHolder> serviceObjectMap = new ConcurrentHashMap<>();
	private final Map<Long, Class<?>> componentMap = new ConcurrentHashMap<>();
	private final AtomicLong changeCount = new AtomicLong(0l);

	public WhiteboardApplicationRouteRegistry(VaadinContext context, FrontendRegistry frontendRegistry) {
		super(context);
		this.frontendRegistry = frontendRegistry;
		updateRegistry(context);
	}

	public void clean() {
		super.clean();
		serviceObjectMap.clear();
		componentMap.clear();
		changeCount.set(0);
	}

	/* (non-Javadoc)
	 * @see org.gecko.vaadin.whiteboard.registry.ServiceObjectRegistry#addServiceObject(org.osgi.framework.ServiceObjects, java.util.Map)
	 */
	@Override
	public ServiceObjectHolder addServiceObject(ServiceObjects<Object> serviceObjects, Map<String, Object> properties) {
		return updateComponentMap(serviceObjects, properties);
	}

	/* (non-Javadoc)
	 * @see org.gecko.vaadin.whiteboard.registry.ServiceObjectRegistry#updateServiceObject(org.osgi.framework.ServiceObjects, java.util.Map)
	 */
	@Override
	public ServiceObjectHolder updateServiceObject(ServiceObjects<Object> serviceObjects, Map<String, Object> properties) {
		return updateComponentMap(serviceObjects, properties);
	}

	/* (non-Javadoc)
	 * @see org.gecko.vaadin.whiteboard.registry.ServiceObjectRegistry#removeServiceObject(java.util.Map)
	 */
	@Override
	public ServiceObjectHolder removeServiceObject(Map<String, Object> properties) {
		return updateComponentMap(null, properties);
	}

	@Override
	public Set<Class<?>> getClasses() {
		synchronized (serviceObjectMap) {
			return serviceObjectMap.values().stream().map(ServiceObjectHolder::getClazz).collect(Collectors.toSet());
		}
	}

	@Override
	public Object createInstance(Class<?> clazz) {
		try {
			ServiceObjectHolder holder = getHolderByClass(clazz);
			if (holder == null) {
				logger.fine("Cannot find service object holder for class " + clazz.getName());
				return null;
			}
			Object service = holder.serviceObjects.getService();
			return service;
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error getting service instance for class: " + clazz.getName(), e);
		} 
		return null;
	}

	@Override
	public void releaseInstance(Object instance) {
		if (instance == null) {
			logger.warning("Cannot release a null instance");
			return;
		}
		Class<?> clazz = instance.getClass();
		try {
			ServiceObjectHolder holder = getHolderByClass(clazz);
			if (holder != null && holder.serviceObjects != null) {
				holder.serviceObjects.ungetService(instance);
			} else {
				logger.warning("Reached state where no holder or service object in holder is available");
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error releasing service instance for class: " + clazz.getName(), e);
		}
	}

	@Override
	public long getVersion() {
		return changeCount.get();
	}

	@Override
	public ApplicationRouteRegistry getRouteRegistry() {
		return this;
	}

	/**
	 * Presets our {@link WhiteboardApplicationRouteRegistry} to be used in Vaadin now
	 * @param context the {@link VaadinContext}
	 */
	private void updateRegistry(VaadinContext context) {
		ApplicationRouteRegistryWrapper attribute;
		synchronized (context) {
			attribute = context
					.getAttribute(ApplicationRouteRegistryWrapper.class);
			
			if (attribute == null || !attribute.getRegistry().equals(this)) {
				if (attribute == null) {
					logger.info("Setting WhiteboardApplicationRouteRegistry to VaadinContext");
				} else {
					logger.info("Replacing existing ApplicationRouteRegistry through WhiteboardApplicationRouteRegistry");
				}
				attribute = new ApplicationRouteRegistryWrapper(this);
				context.setAttribute(attribute);
			}
		}
		
	}

	private synchronized ServiceObjectHolder updateComponentMap(ServiceObjects<Object> serviceObjects, Map<String, Object> properties) {
		Object service = null;
		Long serviceId = (Long) properties.get(Constants.SERVICE_ID);
		Long bundleId = (Long) properties.get(Constants.SERVICE_BUNDLEID);
		FrontendEntry frontend = frontendRegistry.getFrontend(bundleId);
		// Remove service
		if (serviceObjects == null) {
			Class<?> oldValue = componentMap.remove(serviceId);
			ServiceObjectHolder remove = serviceObjectMap.remove(serviceId);
			if (oldValue != null) {
				changeCount.incrementAndGet();
			}
			return remove;
		}
		try {
			Class<?> newClazz;
			String[] classes = (String[]) properties.get(Constants.OBJECTCLASS);
			if (classes != null && classes.length > 0) {
				newClazz = Class.forName(classes[0]);
			} else {
				service = serviceObjects.getService();
				if (service != null) {
					try {
						newClazz = service.getClass();
					} finally {
						serviceObjects.ungetService(service);
					}
				} else {
					throw new IllegalStateException("Cannot determine a class from the service");
				}
			}
			
			String id = newClazz.getName() + serviceId.toString();
			ServiceObjectHolder holder = new ServiceObjectHolder();
			holder.clazz = newClazz;
			holder.id = id;
			holder.pwa = getPwa(newClazz);
			holder.frontend = frontend;
			holder.properties = properties;
			holder.serviceObjects = serviceObjects;
			Class<?> oldValue = componentMap.put(serviceId, newClazz);
			serviceObjectMap.put(serviceId, holder);
			if (!newClazz.equals(oldValue)) {
				changeCount.incrementAndGet();
			}
			return holder;
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error scanning component class", e);
		}
		return null;
	}
	
	private PWA getPwa(Class<?> clazz) {
		PWA pwa = clazz.getAnnotation(PWA.class);
		return pwa;
	}

	private synchronized ServiceObjectHolder getHolderByClass(Class<?> clazz) {
		return serviceObjectMap.values().stream().filter(h->h.clazz == clazz).findFirst().orElse(null);
	}

}
