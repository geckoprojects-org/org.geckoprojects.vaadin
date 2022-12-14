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

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

import com.vaadin.flow.server.webcomponent.WebComponentConfigurationRegistry;

@Component(service = WebComponentConfigurationRegistry.class, scope = ServiceScope.PROTOTYPE)
public class WhiteboardWebcomponentConfigurationRegistry extends WebComponentConfigurationRegistry {

	private static final long serialVersionUID = 1L;

}
