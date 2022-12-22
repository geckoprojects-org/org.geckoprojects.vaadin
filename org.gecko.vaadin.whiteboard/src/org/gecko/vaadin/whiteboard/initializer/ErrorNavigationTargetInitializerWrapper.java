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

import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.server.VaadinContext;
import com.vaadin.flow.server.startup.ErrorNavigationTargetInitializer;

/**
 * Wrpper for {@link ErrorNavigationTargetInitializer}
 * @author Mark Hoffmann
 *
 */
public class ErrorNavigationTargetInitializerWrapper implements StartupProcessor {
	
	private final VaadinContext context;
	private final ErrorNavigationTargetInitializer initializer;
	
	public ErrorNavigationTargetInitializerWrapper(VaadinContext context) {
		this.context = context;
		this.initializer = new ErrorNavigationTargetInitializer();
	}

	@Override
	public List<Class<?>> getAnnotations() {
		return List.of(HasErrorParameter.class);
	}

	@Override
	public void process(Set<Class<?>> classSet) {
		initializer.initialize(classSet, context);
	}

}
