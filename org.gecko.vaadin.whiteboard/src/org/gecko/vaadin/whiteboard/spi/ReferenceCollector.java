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
package org.gecko.vaadin.whiteboard.spi;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gecko.vaadin.whiteboard.Constants;
import org.gecko.vaadin.whiteboard.spi.ServiceReferenceEvent.Type;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceObjects;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.util.pushstream.PushStream;
import org.osgi.util.pushstream.PushStreamProvider;
import org.osgi.util.pushstream.SimplePushEventSource;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * {@link PushStream} based service tracker customizer
 * @author Juergen Albert
 * @since 03.01.2018
 */
@Component(immediate = true, name = Constants.CM_REFERENCE_COLLECTOR, configurationPolicy = ConfigurationPolicy.REQUIRE, service = ReferenceCollector.class)
public class ReferenceCollector implements ServiceTrackerCustomizer<Object, Object> {

	private static final Logger logger = Logger.getLogger(ReferenceCollector.class.getName());
	private ServiceTracker<Object, Object> serviceTracker;
	private PushStreamProvider provider = new PushStreamProvider();
	@SuppressWarnings("rawtypes")
	private final Map<ServiceReference<?>, ServiceReferenceEvent> contentReferences = new ConcurrentHashMap<>(); 
	@SuppressWarnings("rawtypes")
	private SimplePushEventSource<ServiceReferenceEvent> source; 

	private ReferenceCollectionConfig config;
	private BundleContext context; 
	private VaadinDispatcher dispatcher = null;
	@SuppressWarnings("rawtypes")
	PushStream<ServiceReferenceEvent> pushStream;

	@interface ReferenceCollectionConfig {
		String vaadin_application_name();
		String vaadin_component_filter();
	}

	@Activate
	public void activate(ReferenceCollectionConfig config, BundleContext context) {
		this.context = context;
		this.config = config;
	}

	@Modified
	public void modified(ReferenceCollectionConfig config) {
		this.config = config;
		reconnect();
	}

	@Deactivate
	public void deactivate() {
		close();
	}

	/**
	 * Connects the {@link PushStream} with the JaxRs dispatcher to forward
	 * the services it
	 * @param dispatcher the dispatcher instance
	 * @throws  
	 */
	@SuppressWarnings("unchecked")
	public void connect(VaadinDispatcher dispatcher) {
		if (dispatcher == null) {
			throw new IllegalArgumentException("Dispatcher instance must not be null");
		}
		activate();
		this.dispatcher = dispatcher;
		if(pushStream == null) {
			contentReferences.forEach((sr, sre) -> {
				if(sre.isComponent()) {
					handleComponentReferences(dispatcher, sre);
				} else {
					logger.warning("Cannot handle unknown Vaadin type. Currentyl only vaadin.component is supported");
				}
			});

			pushStream = provider.buildStream(source).withScheduler(Executors.newScheduledThreadPool(1)).build();
			pushStream = pushStream.buffer().distinct();

			final Duration batchDuration = Duration.ofMillis(500);
			pushStream.window(batchDuration, sec -> sec).onError(e -> logger.log(Level.SEVERE, "Error adding new Vaadin component", e)).forEach(sec -> {
				sec.stream().filter(sre -> sre.isComponent()).forEach(sre -> {
					handleComponentReferences(dispatcher, sre);
				});
				dispatcher.batchDispatch();
			});
		}

	}

	public void close() {
		if (pushStream != null) {
			pushStream.close();
		}
		if (serviceTracker != null) {
			serviceTracker.close();
		}
		if (source != null) {
			source.close();
		}
	}

	public void reconnect() {
		if (dispatcher != null) {
			close();
			activate();
			connect(dispatcher);
		} else {
			logger.warning("Cannot reconnect without VaadinDispatcher");
		}
	}

	@Override
	public Object addingService(ServiceReference<Object> reference) {
		ServiceReferenceEvent<Object> event = new ServiceReferenceEvent<>(reference, Type.ADD);
		Object service = null;
		try {
			service = context.getServiceObjects(reference);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Cannot get service objects from reference: " + reference, e);
		}
		logger.log(Level.INFO, "Add reference from bundle '" + event.getRegistrar().getSymbolicName() + "' " + reference);
		source.publish(event);
		contentReferences.put(reference, event);
		return service;
	}

	/* (non-Javadoc)
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#modifiedService(org.osgi.framework.ServiceReference, java.lang.Object)
	 */
	@Override
	public void modifiedService(ServiceReference<Object> reference, Object service) {
		ServiceReferenceEvent<Object> event = new ServiceReferenceEvent<>(reference, Type.MODIFY);
		source.publish(event);
	}

	/* (non-Javadoc)
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#removedService(org.osgi.framework.ServiceReference, java.lang.Object)
	 */
	@Override
	public void removedService(ServiceReference<Object> reference, Object service) {
		ServiceReferenceEvent<Object> event = new ServiceReferenceEvent<>(reference, Type.REMOVE);
		contentReferences.remove(reference);
		source.publish(event);
		context.ungetService(reference);
	}

	/**
	 * Activated at component activation
	 * @param context the component context
	 * @throws InvalidSyntaxException
	 */
	private void activate() {
		Filter filter = getApplicationFilter();
		if (filter == null) {
			throw new IllegalStateException("Vaadin dispatcher must provide an application filter");
		}
		source = provider.buildSimpleEventSource(ServiceReferenceEvent.class).build();
		serviceTracker = new ServiceTracker<>(context, filter, this);
		serviceTracker.open();
	}

	/**
	 * @param dispatcher
	 * @param sre
	 */
	private void handleComponentReferences(final VaadinDispatcher dispatcher, ServiceReferenceEvent<Object> sre) {
		Map<String, Object> properties = VaadinHelper.getServiceProperties(sre.getReference());
		ServiceObjects<Object> so = context.getServiceObjects(sre.getReference());
		switch (sre.getType()) {
		case ADD:
			logger.fine("Handle resource " + sre.getType() + " properties: " + properties);
			dispatcher.addComponent(so, properties);
			break;
		case MODIFY:
			logger.fine("Handle resource " + sre.getType() + " properties: " + properties);
			dispatcher.modifyComponent(so, properties);
			break;
		default:
			dispatcher.removeComponent(properties);
			break;
		};
	}

	private Filter getApplicationFilter() {
		if (config == null) {
			throw new IllegalStateException("There is no ReferenceCollectionFilter available");
		}
		String filterString = config.vaadin_component_filter();
		if (filterString == null) {
			throw new IllegalStateException("Application filter must be set");
		}
		try {
			return FrameworkUtil.createFilter(filterString);
		} catch (InvalidSyntaxException e) {
			throw new IllegalStateException("Cannot create valid filter string");
		}
	}

}
