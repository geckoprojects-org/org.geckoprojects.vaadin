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

import com.vaadin.flow.server.VaadinContext;
import com.vaadin.flow.server.startup.WebComponentExporterAwareValidator;

/**
 * Wrapper for the {@link WebComponentExporterAwareValidator}
 * @author Mark Hoffmann
 *
 */
public class WebComponentExporterAwareValidatorWrapper implements StartupProcessor {
	
	private final VaadinContext context;
	private final WebComponentExporterAwareValidator validator;

	public WebComponentExporterAwareValidatorWrapper(VaadinContext context) {
		this.context = context;
		this.validator = new WebComponentExporterAwareValidator();
	}

	@Override
	public List<Class<?>> getAnnotations() {
		return validator.getAnnotations();
	}

	@Override
	public void process(Set<Class<?>> classSet) {
		validator.initialize(classSet, context);
	}

}
