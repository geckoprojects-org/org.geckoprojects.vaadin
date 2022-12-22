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
package org.gecko.vaadin.whiteboard.registry;

import java.util.logging.Logger;

import com.vaadin.flow.server.VaadinContext;
import com.vaadin.flow.server.webcomponent.WebComponentConfigurationRegistry;

/**
 * Whiteboard implementation of the {@link WebComponentConfigurationRegistry} as a replacement for the static default one
 * 
 * @author Mark Hoffmann
 */
public class WhiteboardWebComponentConfigurationRegistry extends WebComponentConfigurationRegistry {

	private static final Logger logger = Logger.getLogger(WhiteboardWebComponentConfigurationRegistry.class.getName());
	private static final long serialVersionUID = 1L;
	
	public WhiteboardWebComponentConfigurationRegistry(VaadinContext context) {
		updateRegistry(context);
	}
	
	/**
	 * Presets our {@link WhiteboardWebComponentConfigurationRegistry} to be used in Vaadin now
	 * @param context the {@link VaadinContext}
	 */
	private void updateRegistry(VaadinContext context) {
		assert context != null;

        WebComponentConfigurationRegistry attribute = context.getAttribute(WebComponentConfigurationRegistry.class);

        if (attribute == null || !attribute.equals(this)) {
        	if (attribute == null) {
        		logger.info("Setting WhiteboardWebComponentConfigurationRegistry to VaadinContext");
        	} else {
        		logger.info("Replacing existing WebComponentConfigurationRegistry through WhiteboardWebComponentConfigurationRegistry");
        	}
        	context.setAttribute(this);
        }
	}

}
