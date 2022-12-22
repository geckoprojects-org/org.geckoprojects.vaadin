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

import com.vaadin.flow.component.WebComponentExporter;
import com.vaadin.flow.component.WebComponentExporterFactory;
import com.vaadin.flow.server.VaadinContext;
import com.vaadin.flow.server.startup.VaadinInitializerException;
import com.vaadin.flow.server.startup.WebComponentConfigurationRegistryInitializer;

/**
 * Wrapper for {@link WebComponentConfigurationRegistryInitializer}
 * @author Mark Hoffmann
 *
 */
public class WebComponentConfigurationRegistryInitializerWrapper implements StartupProcessor {
	
	private final VaadinContext context;
	private final WebComponentConfigurationRegistryInitializer initializer;

	public WebComponentConfigurationRegistryInitializerWrapper(VaadinContext context) {
		this.context = context;
		this.initializer = new WebComponentConfigurationRegistryInitializer();
	}
	
	@Override
	public List<Class<?>> getAnnotations() {
		return List.of(WebComponentExporter.class, WebComponentExporterFactory.class);
	}

	@Override
	public void process(Set<Class<?>> classSet) {
		try {
			initializer.initialize(classSet, context);
		} catch (VaadinInitializerException e) {
			throw new IllegalStateException("Error initializing web components", e);
		}
	}

}
