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
package org.gecko.vaadin.whiteboard;

/**
 * Enum for a Vaadin frontned type
 * @author Mark Hoffmann
 *
 */
public enum VaadinFrontend {
	
	BOWER("bower"),
	NPM("npm"),
	WEBCOMPONENTS("webcomponents");
	
	private String name;
	
	private VaadinFrontend(String name) {
		this.name = name;
	}
	
	/**
	 * Returns the resource filter for the frontend name 
	 * @return the resource filter for the frontend name 
	 */
	public String getResourceFilter() {
		if (name == null) {
			return null;
		}
		return String.format("(%s=%s)", Constants.VAADIN_RESOURCE_NAME, name);
	}
	
}
