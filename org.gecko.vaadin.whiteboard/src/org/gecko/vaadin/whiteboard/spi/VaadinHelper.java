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

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.osgi.framework.ServiceReference;

import com.googlecode.gentyref.GenericTypeReflector;
import com.vaadin.flow.internal.AnnotationReader;
import com.vaadin.flow.router.HasErrorParameter;

/**
 * Generic helper class
 * @author Mark Hoffmann
 */
public class VaadinHelper {

	/**
	 * Creates a properties map from the service reference properties
	 * @param reference the service reference
	 * @return a properties map
	 */
	public static Map<String, Object> getServiceProperties(ServiceReference<?> reference) {
		Map<String, Object> props = new HashMap<>();
		if (reference != null) {
			for (String key : reference.getPropertyKeys()) {
				props.put(key,  reference.getProperty(key));
			}
		}
		return props;
	}

	@SuppressWarnings("unchecked")
	public static Set<Class<?>> filterClasses(List<Class<?>> typeAnnotations, Set<Class<?>> classes) {
		Set<Class<?>> result = new HashSet<>();
		if (typeAnnotations == null || typeAnnotations.isEmpty()) {
			return classes;
		} else {

			Predicate<Class<?>> isAnnotation = Class::isAnnotation;

			List<Class<? extends Annotation>> annotations = typeAnnotations.stream()
					.filter(isAnnotation)
					.map(clazz -> (Class<? extends Annotation>) clazz)
					.collect(Collectors.toList());

			List<Class<?>> superTypes = typeAnnotations.stream()
					.filter(isAnnotation.negate()).collect(Collectors.toList());

			Predicate<Class<?>> hasType = clazz -> annotations.stream()
					.anyMatch(annotation -> AnnotationReader
							.getAnnotationFor(clazz, annotation).isPresent())
					|| superTypes.stream()
					.anyMatch(superType -> GenericTypeReflector
							.isSuperType(HasErrorParameter.class,
									clazz));

			result.addAll(classes
					.stream().filter(hasType).collect(Collectors.toList()));

		}
		return result;
	}

	public static String validate(String context) {
		if (context == null) {
			return null;
		}
		if (!context.startsWith("/")) {
			return  "/" + context;
		}
		if (context.endsWith("/")) {
			return context + "*";
		} else if (!context.endsWith("/*")) {
			return context + "/*";
		} else {
			return context;
		}
	}

}
