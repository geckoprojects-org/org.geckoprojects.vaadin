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
package org.gecko.vaadin.whiteboard.spi;

import java.util.Set;

import org.gecko.vaadin.whiteboard.registry.ServiceObjectRegistry;

import com.vaadin.flow.server.webcomponent.WebComponentConfigurationRegistry;

public interface VaadinWhiteboardRegistryProcessor {
	
	public void process();
	
	public void process(boolean initialize);
	
	public void process(Set<Class<?>> classSet);
	
	public void process(Set<Class<?>> classSet, boolean initialize);
	
	public ServiceObjectRegistry<Object> getServiceObjectRegistry();
	
	public WebComponentConfigurationRegistry getWebComponentRegistry();

}
