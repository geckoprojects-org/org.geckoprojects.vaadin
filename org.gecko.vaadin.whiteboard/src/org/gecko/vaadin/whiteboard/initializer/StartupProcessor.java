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

/**
 * Processor that is called on servlet/Whiteboard startup
 * @author Mark Hoffmann
 *
 */
public interface StartupProcessor {
	
	/**
	 * Returns the annotations types, this processor belongs to
	 */
	public List<Class<?>> getAnnotations();
	
	/**
	 * Processes the given class set
	 * @param classSet the classes to be processed
	 */
	public void process(Set<Class<?>> classSet);
	
	/**
	 * Processes the given class set
	 * @param classSet the classes to be processed
	 * @param initialize <code>true</code> on initialization
	 */
	public void process(Set<Class<?>> classSet, boolean initialize);

}
