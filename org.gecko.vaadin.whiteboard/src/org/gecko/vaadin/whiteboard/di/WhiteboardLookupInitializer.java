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
import java.util.Map;

import org.osgi.framework.BundleContext;

import com.vaadin.flow.di.Lookup;
import com.vaadin.flow.di.LookupInitializer;
import com.vaadin.flow.server.VaadinContext;

/**
 * @author Mark Hoffmann
 *
 */
public class WhiteboardLookupInitializer extends LookupInitializer {
	
	private final BundleContext bundleContext;
	private final Map<String, Object> properties;

	public WhiteboardLookupInitializer(BundleContext bundleContext, Map<String, Object> properties) {
		this.bundleContext = bundleContext;
		this.properties = properties;
	}
	
	@Override
	protected Lookup createLookup(VaadinContext context, Map<Class<?>, Collection<Class<?>>> services) {
		Lookup delegate = super.createLookup(context, services);
		return new WhiteboardLookup(bundleContext, delegate, properties);
	}
	

}
