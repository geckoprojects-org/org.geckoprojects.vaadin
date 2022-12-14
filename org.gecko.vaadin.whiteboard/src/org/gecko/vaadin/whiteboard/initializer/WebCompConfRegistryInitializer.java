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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.gecko.vaadin.whiteboard.annotations.StartupProcessorName;
import org.osgi.service.component.annotations.Component;

import com.vaadin.flow.component.WebComponentExporter;
import com.vaadin.flow.component.WebComponentExporterFactory;
import com.vaadin.flow.component.webcomponent.WebComponentConfiguration;
import com.vaadin.flow.internal.CustomElementNameValidator;
import com.vaadin.flow.server.InvalidCustomElementNameException;
import com.vaadin.flow.server.startup.WebComponentConfigurationRegistryInitializer;
import com.vaadin.flow.server.webcomponent.WebComponentConfigurationRegistry;
import com.vaadin.flow.server.webcomponent.WebComponentExporterUtils;

/**
 * TODO Duplicate code from @see {@link WebComponentConfigurationRegistryInitializer}
 * @author Mark Hoffmann
 *
 */
@StartupProcessorName("WebComponentConfigurationRegistry")
@Component
public class WebCompConfRegistryInitializer implements WCCStartupProcessor {

	@Override
	public List<Class<?>> getAnnotations() {
		List<Class<?>> classes = new ArrayList<Class<?>>(4);
		classes.add(WebComponentExporter.class);
		classes.add(WebComponentExporterFactory.class);
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

	@Override
	public void process(Set<Class<?>> classSet, WebComponentConfigurationRegistry registry) {
		process(classSet, registry, false);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void process(Set<Class<?>> classSet, WebComponentConfigurationRegistry registry, boolean initialize) {
		if (registry == null) {
			throw new IllegalStateException("Cannot process web component configuration without registry instance");
		}
		if (classSet == null || classSet.isEmpty()) {
			registry.setConfigurations(Collections.emptySet());
			return;
		}

		try {
			Set<WebComponentExporterFactory> factories = WebComponentExporterUtils
					.getFactories(classSet);
			Set<WebComponentConfiguration<? extends com.vaadin.flow.component.Component>> configurations = constructConfigurations(
					factories);

			validateTagNames(configurations);
			validateDistinctTagNames(configurations);

			registry.setConfigurations(configurations);
		} catch (Exception e) {
			throw new IllegalStateException(
					String.format("%s failed to collect %s implementations!",
							WebComponentConfigurationRegistryInitializer.class
							.getSimpleName(),
							WebComponentExporter.class.getSimpleName()),
					e);
		}
	}



	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Set<WebComponentConfiguration<? extends com.vaadin.flow.component.Component>> constructConfigurations(
			Set<WebComponentExporterFactory> factories) {
		Objects.requireNonNull(factories,
				"Parameter 'exporterClasses' " + "cannot be null!");

		final WebComponentExporter.WebComponentConfigurationFactory factory = new WebComponentExporter.WebComponentConfigurationFactory();

		Stream<WebComponentConfiguration<? extends com.vaadin.flow.component.Component>> stream = factories
				.stream().map(WebComponentExporterFactory::create)
				.map(factory::create);
		return stream.collect(Collectors.toSet());
	}

	/**
	 * Validate that all web component names are valid custom element names.
	 *
	 * @param configurationSet
	 *            set of web components to validate
	 */
	private static void validateTagNames(
			Set<WebComponentConfiguration<? extends com.vaadin.flow.component.Component>> configurationSet) {
		for (WebComponentConfiguration<? extends com.vaadin.flow.component.Component> configuration : configurationSet) {
			if (!CustomElementNameValidator
					.isCustomElementName(configuration.getTag())) {
				throw new InvalidCustomElementNameException(String.format(
						"Tag name '%s' given by '%s' is not a valid custom "
								+ "element name.",
								configuration.getTag(),
								configuration.getExporterClass().getCanonicalName()));
			}
		}
	}

	/**
	 * Validate that we have exactly one {@link WebComponentConfiguration} per
	 * tag name.
	 *
	 * @param configurationSet
	 *            set of web components to validate
	 */
	private static void validateDistinctTagNames(
			Set<WebComponentConfiguration<? extends com.vaadin.flow.component.Component>> configurationSet) {
		long count = configurationSet.stream()
				.map(WebComponentConfiguration::getTag).distinct().count();
		if (configurationSet.size() != count) {
			Map<String, WebComponentConfiguration<? extends com.vaadin.flow.component.Component>> items = new HashMap<>();
			for (WebComponentConfiguration<? extends com.vaadin.flow.component.Component> configuration : configurationSet) {
				String tag = configuration.getTag();
				if (items.containsKey(tag)) {
					String message = String.format(
							"Found two %s classes '%s' and '%s' for the tag "
									+ "name '%s'. Tag must be unique.",
									WebComponentExporter.class.getSimpleName(),
									items.get(tag).getExporterClass()
									.getCanonicalName(),
									configuration.getExporterClass().getCanonicalName(),
									tag);
					throw new IllegalArgumentException(message);
				}
				items.put(tag, configuration);
			}
		}
	}

}
