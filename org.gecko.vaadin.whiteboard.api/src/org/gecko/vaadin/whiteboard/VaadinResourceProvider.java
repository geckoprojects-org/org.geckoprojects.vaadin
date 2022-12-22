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

import java.net.URL;

/**
 * Provider for static resources
 * @author Mark Hoffmann
 *
 */
public interface VaadinResourceProvider {
	
	public static final String RESOURCE_PREFIX_PROPERTY = "vaadin.resource.prefix"; 
	public static final String RESOURCE_DEFAULT_PREFIX = "/VAADIN-INF/resources"; 
	
	/**
	 * Returns the resource for the given path
	 * @param path the path within the bundle
	 * @return the URL or <code>null</code> if nothing was found
	 */
	URL getResource(String path);

}
