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
package org.gecko.vaadin.whiteboard.di;

import static org.gecko.vaadin.whiteboard.Constants.VAADIN_PWA_NAME;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import com.vaadin.flow.di.Lookup;

/**
 * {@link Lookup} implementation that looks into the service registry, to get services
 * @author Mark Hoffmann
 *
 */
public class WhiteboardLookup implements Lookup {

	private static final Logger logger = Logger.getLogger(WhiteboardLookup.class.getName());
	private static final String PWA_FILTER = "(" + VAADIN_PWA_NAME + "=/s)";
	private final BundleContext context;
	private final Filter pwaFilter;
	private final String pwaFilterString;
	private final Lookup delegate;

	public WhiteboardLookup(BundleContext context, Lookup delegate, Map<String, Object> properties) {
		this.context = context;
		this.delegate = delegate;
		this.pwaFilterString = String.format(PWA_FILTER, properties.getOrDefault(VAADIN_PWA_NAME, "*"));
		try {
			this.pwaFilter = FrameworkUtil.createFilter(pwaFilterString);
		} catch (InvalidSyntaxException e) {
			throw new IllegalStateException("Error creating application filter", e);
		}
	}

	/* (non-Javadoc)
	 * @see com.vaadin.flow.di.Lookup#lookup(java.lang.Class)
	 */
	@Override
	public <T> T lookup(Class<T> serviceClass) {
		
		Collection<ServiceReference<T>> serviceReferences;
		try {
			serviceReferences = context.getServiceReferences(serviceClass, null);
			if (serviceReferences == null || serviceReferences.isEmpty()) {
				logger.warning(String.format("No service found for class '%s'", serviceClass.getName()));
				Optional<T> instance = delegateLookup(serviceClass);
				if (instance.isPresent()) {
					return instance.get();
				}
				return null;
			}
			List<ServiceReference<T>> refs = serviceReferences.stream().filter(pwaFilter::match).collect(Collectors.toList());
			// prefer matching references over non matching
			ServiceReference<T> reference = !refs.isEmpty() ? refs.get(0) : serviceReferences.iterator().next();
			return context.getService(reference);
		} catch (InvalidSyntaxException e) {
			logger.warning(String.format("Invalif filter No service found for class '%s'", serviceClass.getName()));
		}
		Optional<T> instance = delegateLookup(serviceClass);
		if (instance.isPresent()) {
			return instance.get();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.vaadin.flow.di.Lookup#lookupAll(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> Collection<T> lookupAll(Class<T> serviceClass) {
		try {
			Collection<ServiceReference<T>> references = context.getServiceReferences(serviceClass, null);
			return (Collection<T>) references.stream().map((sr)->{
				try {
					return Optional.of(context.getService(sr));
				} catch (Exception e) {
					logger.log(Level.SEVERE, "Cannot get service for service reference " + sr.toString(), e);
					return Optional.empty();
				}
			})
					.filter(Optional::isPresent)
					.map(o->(T)o.get()).
					collect(Collectors.toList());
		} catch (InvalidSyntaxException e) {
			logger.log(Level.SEVERE, String.format("No service references found for class '%s'", serviceClass.getName()), (Throwable)e);
			return Collections.emptySet();
		}
	}

	private <T> Optional<T> delegateLookup(Class<T> serviceClass) {
		T instance = delegate != null ? delegate.lookup(serviceClass) : null;
		return instance != null ? Optional.of(instance) : Optional.empty();
	}

}
