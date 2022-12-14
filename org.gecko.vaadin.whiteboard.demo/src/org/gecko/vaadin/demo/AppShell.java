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
package org.gecko.vaadin.demo;

import org.gecko.vaadin.whiteboard.annotations.VaadinComponent;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;

/**
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 */
@Theme(value = "wbapp")
@PWA(name = "My Whiteboard App", shortName = "My Whiteboard App", offlineResources = {"images/Logo_DIMC.png"})
@Component(service=AppShell.class, scope = ServiceScope.PROTOTYPE)
@VaadinComponent()
public class AppShell implements AppShellConfigurator {

	private static final long serialVersionUID = 1L;
	
}
