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
package org.gecko.vaadin.whiteboard.initializer;

import java.util.Set;

import org.gecko.vaadin.whiteboard.registry.ServiceObjectRegistry;
import org.gecko.vaadin.whiteboard.spi.VaadinHelper;
import org.gecko.vaadin.whiteboard.spi.VaadinWhiteboardRegistryProcessor;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceScope;
import org.osgi.service.component.annotations.ServiceScope;

import com.vaadin.flow.server.webcomponent.WebComponentConfigurationRegistry;

/**
 * This component is responsible to validate Vaadin annotated classes and put them all in the right registries.
 * So, this is the configuration place, that is usually handled by the Vaadin ServletContainerInitializer:
 * @see vaadin-server com.vaadin.flow.server.startup package
 * @author Mark Hoffmann
 *
 */
@Component(service = VaadinWhiteboardRegistryProcessor.class, scope = ServiceScope.PROTOTYPE)
public class WhiteboardRegistryProcessor implements VaadinWhiteboardRegistryProcessor {

	@Reference(name="routeRegistry", scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ServiceObjectRegistry<Object> serviceObjectRegistry;
	@Reference(name="webCompConfigurationRegistry", scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private WebComponentConfigurationRegistry webCompConfRegistry;

	@Reference(target = "(vaadin.startup.processor.name=AnnotationValidator)")
	private StartupProcessor annotationValidator;
	@Reference(target = "(vaadin.startup.processor.name=ErrorNavigationTarget)")
	private ARRStartupProcessor errorNavigation;
	@Reference(target = "(vaadin.startup.processor.name=RouteRegistryInitializer)")
	private ARRStartupProcessor routeInitializer;
	@Reference(target = "(vaadin.startup.processor.name=WebComponentExporterValidator)")
	private StartupProcessor wceInitializer;
	@Reference(target = "(vaadin.startup.processor.name=WebComponentConfigurationRegistry)")
	private WCCStartupProcessor wccrInitializer;

	@Override
	public void process() {
		process(false);
	}

	@Override
	public void process(boolean initialize) {
		Set<Class<?>> classes = serviceObjectRegistry.getClasses();
		process(classes, initialize);
	}

	@Override
	public void process(Set<Class<?>> classSet) {
		internalProcess(annotationValidator, classSet, false);
		internalProcess(routeInitializer, classSet, false);
		internalProcess(errorNavigation, classSet, false);
		internalProcess(wceInitializer, classSet, false);
		internalProcess(wccrInitializer, classSet, false);
	}

	@Override
	public void process(Set<Class<?>> classSet, boolean initialize) {
		internalProcess(annotationValidator, classSet, initialize);
		internalProcess(routeInitializer, classSet, initialize);
		internalProcess(errorNavigation, classSet, initialize);
		internalProcess(wceInitializer, classSet, initialize);
		internalProcess(wccrInitializer, classSet, initialize);
	}

	private synchronized void internalProcess(StartupProcessor processor, Set<Class<?>> classSet, boolean initialize) {
		if (processor instanceof ARRStartupProcessor) {
			((ARRStartupProcessor)processor).process(VaadinHelper.filterClasses(processor.getAnnotations(), classSet), serviceObjectRegistry.getRouteRegistry(), initialize);
		} else if (processor instanceof WCCStartupProcessor) {
			((WCCStartupProcessor)processor).process(VaadinHelper.filterClasses(processor.getAnnotations(), classSet), webCompConfRegistry, initialize);
		} else {
			processor.process(VaadinHelper.filterClasses(processor.getAnnotations(), classSet), initialize);
		}
	}

	@Override
	public ServiceObjectRegistry<Object> getServiceObjectRegistry() {
		return serviceObjectRegistry;
	}

	@Override
	public WebComponentConfigurationRegistry getWebComponentRegistry() {
		return webCompConfRegistry;
	}

}
