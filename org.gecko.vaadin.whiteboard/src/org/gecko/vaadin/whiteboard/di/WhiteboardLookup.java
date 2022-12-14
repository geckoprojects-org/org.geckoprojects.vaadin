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

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import com.vaadin.flow.di.Lookup;
import com.vaadin.flow.di.ResourceProvider;

public class WhiteboardLookup implements Lookup {
	
	private static final Logger logger = Logger.getLogger(WhiteboardLookup.class.getName());
	private final BundleContext context;

	public WhiteboardLookup(BundleContext context) {
		this.context = context;
	}

	/* (non-Javadoc)
	 * @see com.vaadin.flow.di.Lookup#lookup(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T lookup(Class<T> serviceClass) {
		if (ResourceProvider.class.isAssignableFrom(serviceClass)) {
			return (T) new WhiteboardResourceProvider();
		}
		ServiceReference<T> serviceReference = context.getServiceReference(serviceClass);
		if (serviceReference == null) {
			logger.warning(String.format("No service found for class '%s'", serviceClass.getName()));
			return null;
		}
		return context.getService(serviceReference);
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

}
