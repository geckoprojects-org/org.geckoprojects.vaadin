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

import java.util.Set;

import org.gecko.vaadin.whiteboard.registry.ServiceObjectRegistry;

import com.vaadin.flow.server.webcomponent.WebComponentConfigurationRegistry;

/**
 * Processes the classes for Vaadin routes or other annotation. It is the major part to calculate routes for a Vaadin UI
 * @author Mark Hoffmann
 *
 */
public interface WhiteboardApplicationProcessor {
	
	/**
	 * Processes all classes that are cached
	 */
	public void process();
	
	/**
	 * Processes the given class set
	 * @param classSet the class set to process
	 */
	public void process(Set<Class<?>> classSet);
	
	/**
	 * Processor deactivation, cleans up all resources
	 */
	public void deactivate();
	
	/**
	 * Returns the {@link ServiceObjectRegistry}. Must not return <code>null</code>
	 * @return the {@link ServiceObjectRegistry}
	 */
	public ServiceObjectRegistry<Object> getServiceObjectRegistry();
	
	/**
	 * Returns the {@link WebComponentConfigurationRegistry}. Must not be <code>null</code>
	 * @return the {@link WebComponentConfigurationRegistry}
	 */
	public WebComponentConfigurationRegistry getWebComponentRegistry();
	

}
