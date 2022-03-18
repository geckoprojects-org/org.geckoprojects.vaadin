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

import com.vaadin.flow.server.webcomponent.WebComponentConfigurationRegistry;

public interface WCCStartupProcessor extends StartupProcessor{
	
	public void process(Set<Class<?>> classSet, WebComponentConfigurationRegistry registry);
	
	public void process(Set<Class<?>> classSet, WebComponentConfigurationRegistry registry, boolean initialize);

}
