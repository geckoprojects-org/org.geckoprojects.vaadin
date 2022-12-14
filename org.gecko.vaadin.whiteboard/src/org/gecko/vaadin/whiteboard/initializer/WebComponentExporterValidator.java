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
import java.util.Optional;
import java.util.Set;

import org.gecko.vaadin.whiteboard.annotations.StartupProcessorName;
import org.osgi.service.component.annotations.Component;

import com.googlecode.gentyref.GenericTypeReflector;
import com.vaadin.flow.component.WebComponentExporter;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.startup.AbstractAnnotationValidator;
import com.vaadin.flow.theme.Theme;

@Component(service = StartupProcessor.class)
@StartupProcessorName("WebComponentExporterValidator")
public class WebComponentExporterValidator extends AbstractAnnotationValidator implements StartupProcessor {

	private static final long serialVersionUID = -2359525384778837688L;

	@Override
	public void process(Set<Class<?>> classSet) {
		process(classSet, false);
	}

	@Override
	public void process(Set<Class<?>> classSet, boolean initialize) {
		validateClasses(classSet);
	}
	
	@Override
    protected Optional<String> handleNonRouterLayout(Class<?> clazz) {
        if (WebComponentExporter.class
                .isAssignableFrom(GenericTypeReflector.erase(clazz))) {
            return Optional.empty();
        }
        return Optional.of(String.format(
                "Class '%s' contains '%s', but it is not a router "
                        + "layout/top level route/web component.",
                clazz.getName(), getClassAnnotations(clazz)));
    }

	@Override
	public List<Class<?>> getAnnotations() {
		List<Class<?>> classes = new ArrayList<Class<?>>(4);
		classes.add(Theme.class);
		classes.add(Push.class);
		return classes;
	}

}
