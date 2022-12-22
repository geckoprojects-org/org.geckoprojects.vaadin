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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.gecko.vaadin.whiteboard.registry.FrontendRegistry;
import org.gecko.vaadin.whiteboard.registry.ServiceObjectRegistry;
import org.gecko.vaadin.whiteboard.registry.WhiteboardApplicationRouteRegistry;
import org.gecko.vaadin.whiteboard.registry.WhiteboardWebComponentConfigurationRegistry;
import org.gecko.vaadin.whiteboard.spi.VaadinHelper;
import org.gecko.vaadin.whiteboard.spi.WhiteboardApplicationProcessor;

import com.vaadin.flow.server.VaadinContext;
import com.vaadin.flow.server.webcomponent.WebComponentConfigurationRegistry;

/**
 * This processor  is responsible to validate Vaadin annotated classes and put them all in the right registries.
 * So, this is the configuration place, that is usually handled by the Vaadin ServletContainerInitializer:
 * @see vaadin-server com.vaadin.flow.server.startup package
 * @author Mark Hoffmann
 *
 */
public class WhiteboardApplicationProcessorImpl implements WhiteboardApplicationProcessor {
	
	private final WhiteboardApplicationRouteRegistry applicationRegistry;
	private final WebComponentConfigurationRegistry webCompConfRegistry;
	private final List<StartupProcessor> initializers = new ArrayList<>(5);

	public WhiteboardApplicationProcessorImpl(VaadinContext context, FrontendRegistry frontendRegistry) {
		/* 
		 * We need the WhiteboardApplicationRouteRegistry and WhiteboardWebComponentConfigurationRegistry
		 * at the first positions, to replace the static default registries
		 * by our whiteboard-registries 
		 */
		applicationRegistry = new WhiteboardApplicationRouteRegistry(context, frontendRegistry);
		webCompConfRegistry = new WhiteboardWebComponentConfigurationRegistry(context);
		initializers.add(new AnnotationValidatorWrapper(context));
		initializers.add(new ErrorNavigationTargetInitializerWrapper(context));
		initializers.add(new RouteRegistryInitializerWrapper(context));
		initializers.add(new WebComponentExporterAwareValidatorWrapper(context));
		initializers.add(new WebComponentConfigurationRegistryInitializerWrapper(context));
	}
	
	/**
	 * Called in deactivation
	 */
	public void deactivate() {
		applicationRegistry.clean();
	}

	@Override
	public void process() {
		Set<Class<?>> classes = applicationRegistry.getClasses();
		process(classes);
	}

	@Override
	public void process(Set<Class<?>> classSet) {
		initializers.forEach((p)->p.process(VaadinHelper.filterClasses(p.getAnnotations(), classSet)));
	}

	@Override
	public ServiceObjectRegistry<Object> getServiceObjectRegistry() {
		return applicationRegistry;
	}

	@Override
	public WebComponentConfigurationRegistry getWebComponentRegistry() {
		return webCompConfRegistry;
	}

}
