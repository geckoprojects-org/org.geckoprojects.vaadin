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

import org.gecko.vaadin.whiteboard.annotations.StartupProcessorName;
import org.osgi.service.component.annotations.Component;

import com.googlecode.gentyref.GenericTypeReflector;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.AmbiguousRouteConfigurationException;
import com.vaadin.flow.server.InvalidRouteConfigurationException;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.server.startup.AbstractRouteRegistryInitializer;
import com.vaadin.flow.server.startup.ApplicationRouteRegistry;

@StartupProcessorName("RouteRegistryInitializer")
@Component(service = ARRStartupProcessor.class)
public class RouteInitializerComponent extends AbstractRouteRegistryInitializer implements ARRStartupProcessor {

	private static final long serialVersionUID = 2547341303563161817L;

	@Override
	public void process(Set<Class<?>> classSet, ApplicationRouteRegistry routeRegistry) {
		try {
			if (classSet == null) {
				routeRegistry.clean();
				return;
			}

			Set<Class<? extends com.vaadin.flow.component.Component>> routes = validateRouteClasses(
					classSet.stream());
			RouteConfiguration routeConfiguration = RouteConfiguration
					.forRegistry(routeRegistry);
			routeConfiguration.update(
					() -> setAnnotatedRoutes(routeConfiguration, routes));
			routeRegistry.setPwaConfigurationClass(validatePwaClass(
					routes.stream().map(clazz -> (Class<?>) clazz)));
		} catch (InvalidRouteConfigurationException irce) {
			throw new IllegalStateException(
					"Exception while registering Routes on whiteboard startup",
					irce);
		}
	}

	@Override
	public void process(Set<Class<?>> classSet, ApplicationRouteRegistry routeRegistry, boolean initialize) {
		if (!initialize) {
			process(classSet, routeRegistry);
		}
	}

	private void setAnnotatedRoutes(RouteConfiguration routeConfiguration,
			Set<Class<? extends com.vaadin.flow.component.Component>> routes) {
		routeConfiguration.getHandledRegistry().clean();
		for (Class<? extends com.vaadin.flow.component.Component> navigationTarget : routes) {
			try {
				routeConfiguration.setAnnotatedRoute(navigationTarget);
			} catch (AmbiguousRouteConfigurationException exception) {
				if (!handleAmbiguousRoute(routeConfiguration,
						exception.getConfiguredNavigationTarget(),
						navigationTarget)) {
					throw exception;
				}
			}
		}
	}

	private boolean handleAmbiguousRoute(RouteConfiguration routeConfiguration,
			Class<? extends com.vaadin.flow.component.Component> configuredNavigationTarget,
			Class<? extends com.vaadin.flow.component.Component> navigationTarget) {
		if (GenericTypeReflector.isSuperType(navigationTarget,
				configuredNavigationTarget)) {
			return true;
		} else if (GenericTypeReflector.isSuperType(configuredNavigationTarget,
				navigationTarget)) {
			routeConfiguration.removeRoute(configuredNavigationTarget);
			routeConfiguration.setAnnotatedRoute(navigationTarget);
			return true;
		}
		return false;
	}

	@Override
	public List<Class<?>> getAnnotations() {
		List<Class<?>> classes = new ArrayList<Class<?>>(4);
		classes.add(PWA.class);
		classes.add(Route.class);
		classes.add(RouteAlias.class);
		return classes;
	}

	@Override
	public void process(Set<Class<?>> classSet) {
		throw new UnsupportedOperationException("This operation is not supported in this startup processor");
	}

	@Override
	public void process(Set<Class<?>> classSet, boolean initialize) {
		throw new UnsupportedOperationException("This operation is not supported in this startup processor");
	}

}
