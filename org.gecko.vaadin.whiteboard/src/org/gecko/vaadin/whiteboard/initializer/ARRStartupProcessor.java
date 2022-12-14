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

import com.vaadin.flow.server.startup.ApplicationRouteRegistry;

/**
 * {@link ApplicationRouteRegistry} startup processor. A startup processor that needs
 * an {@link ApplicationRouteRegistry}
 * @author Mark Hoffmann
 *
 */
public interface ARRStartupProcessor extends StartupProcessor{
	
	/**
	 * Returns the annotations types, this processor belongs to
	 */
	public List<Class<?>> getAnnotations();
	
	/**
	 * Processes the given classes for the annotations
	 * @param classSet the set of classes
	 * @param routeRegistry the rout registry
	 */
	public void process(Set<Class<?>> classSet, ApplicationRouteRegistry routeRegistry);
	
	/**
	 * Processes the given classes for the annotations
	 * @param classSet the set of classes
	 * @param routeRegistry the rout registry
	 * @param initialize <code>true</code> for instialization
	 */
	public void process(Set<Class<?>> classSet, ApplicationRouteRegistry routeRegistry, boolean initialize);

}
