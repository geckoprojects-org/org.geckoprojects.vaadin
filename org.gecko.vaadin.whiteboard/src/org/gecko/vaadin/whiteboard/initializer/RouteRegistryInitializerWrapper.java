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
package org.gecko.vaadin.whiteboard.initializer;

import java.util.List;
import java.util.Set;

import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.VaadinContext;
import com.vaadin.flow.server.startup.RouteRegistryInitializer;
import com.vaadin.flow.server.startup.VaadinInitializerException;

/**
 * Wrapper for the {@link RouteRegistryInitializer}
 * @author Mark Hoffmann
 *
 */
public class RouteRegistryInitializerWrapper implements StartupProcessor {
	
	private final VaadinContext context;
	private final RouteRegistryInitializer initializer;

	public RouteRegistryInitializerWrapper(VaadinContext context) {
		this.context = context;
		this.initializer = new RouteRegistryInitializer();
	}

	@Override
	public List<Class<?>> getAnnotations() {
		return List.of(Route.class, RouteAlias.class);
	}

	@Override
	public void process(Set<Class<?>> classSet) {
		try {
			initializer.initialize(classSet, context);
		} catch (VaadinInitializerException e) {
			throw new IllegalStateException("Error initializing routes", e);
		}
	}

}
