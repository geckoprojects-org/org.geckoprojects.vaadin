/**
 * Copyright (c) 2012 - 2021 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.gecko.vaadin.whiteboard.di;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.gecko.vaadin.whiteboard.registry.ServiceObjectRegistry;
import org.osgi.framework.ServiceObjects;

import com.vaadin.flow.di.DefaultInstantiator;
import com.vaadin.flow.server.SessionDestroyEvent;
import com.vaadin.flow.server.SessionDestroyListener;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinSession;

/**
 * OSGi instantiator, that creates instances out of {@link ServiceObjects}.
 * It registers a {@link SessionDestroyListener} to the {@link VaadinService} to get
 * notified, when to release the service instances.
 * The instance are stored in the {@link VaadinSession}, an released as soon as 
 * there is an getOrCreate request, with an already existing service instance.
 * @author Mark Hoffmann
 *
 */
public class OSGiInstantiator extends DefaultInstantiator implements SessionDestroyListener {

	private static final long serialVersionUID = 1646678691680462434L;
	private static final Logger logger = Logger.getLogger(OSGiInstantiator.class.getName());
	private static final String INSTANCE_LIST = "vaadin.osgi.instantiator.objects";
	private static final String INSTANCE_CLASS = "vaadin.osgi.object.%s";
	private final ServiceObjectRegistry<Object> serviceObjectRegistry;

	public OSGiInstantiator(VaadinService service, ServiceObjectRegistry<Object> serviceObjectRegistry) {
		super(service);
		service.addSessionDestroyListener(this);
		this.serviceObjectRegistry = serviceObjectRegistry;
	}
	
	/* (non-Javadoc)
	 * Creates instances out of OSGi service objects
	 * @see com.vaadin.flow.di.DefaultInstantiator#getOrCreate(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getOrCreate(Class<T> type) {
		VaadinSession session = VaadinSession.getCurrent();
		if (session == null) {
			logger.severe("Cannot create a instance object that is not disposable because of a null VaadinSession");
			return null;
		}
//		session.getSession().setMaxInactiveInterval(5);
		releaseServiceInstance(session, type.getName());
		
		/*
		 * Create a new instance and keep that in the session
		 */
		Object o = serviceObjectRegistry.createInstance(type);
		if (o == null) {
			logger.finer("Get or created instance of type " + type.getSimpleName() + " for session " + session);
			return super.getOrCreate(type);
		} else { 
			registerServiceInstance(session, o, type.getName());
			return (T) o;
		}
	}

	/**
	 *Cleans service objects up on session destroy
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void sessionDestroy(SessionDestroyEvent event) {
		VaadinSession session = event.getSession();
		List<String> instances;
		synchronized (session) {
			List<String> clazzes = (List<String>) session.getAttribute(INSTANCE_LIST);
			if (clazzes == null) {
				return;
			}
			session.setAttribute(INSTANCE_LIST, null);
			instances = Collections.unmodifiableList(clazzes);
			clazzes.clear();
		}
		instances.stream().forEach(clazz->releaseServiceInstance(session, clazz));
		logger.info("Released service object " + instances.size() + " instances for session " + session);
		instances.clear();
	}
	
	/**
	 * Check the session for old service objects instances and release them, if they exist.
	 * Cleanup the session object afterwards.
	 * @param session the current {@link VaadinSession}
	 * @param clazzName the class name of the instance to be released
	 */
	@SuppressWarnings("unchecked")
	private void releaseServiceInstance(VaadinSession session, String clazzName) {
		String attributeKey = String.format(INSTANCE_CLASS, clazzName);
		Object oldObject = session.getAttribute(attributeKey);
		List<String> instances = (List<String>) session.getAttribute(INSTANCE_LIST);
		if (oldObject != null) {
			logger.info("Released OSGi service object instance of type " + clazzName + " for session " + session);
			serviceObjectRegistry.releaseInstance(oldObject);
			synchronized (session) {
				session.setAttribute(attributeKey, null);
				instances.remove(clazzName);
			}
		}
	}
	
	/**
	 * Register the given service instance in the session using the given class name
	 * @param session the current {@link VaadinSession}
	 * @param instance the service instance to be registered
	 * @param clazzName the class name of the service instance type
	 */
	@SuppressWarnings("unchecked")
	private void registerServiceInstance(VaadinSession session, Object instance, String clazzName) {
		if (session == null || instance == null || clazzName == null) {
			logger.warning("There is nothing to register; session: " + session + ", instance: " + instance + ", class-name: " + clazzName);
			return;
		}
		String attributeKey = String.format(INSTANCE_CLASS, clazzName);
		List<String> instances = (List<String>) session.getAttribute(INSTANCE_LIST);
		logger.info("Get or created OSGi service object instance of type " + clazzName + " for session " + session);
		if (instances == null) {
			instances = new LinkedList<String>();
			session.setAttribute(INSTANCE_LIST, instances);
		}
		session.setAttribute(attributeKey, instance);
		instances.add(clazzName);
		logger.fine("Service objects instance cache contains " + instances.size() + " elements");
	}

}
