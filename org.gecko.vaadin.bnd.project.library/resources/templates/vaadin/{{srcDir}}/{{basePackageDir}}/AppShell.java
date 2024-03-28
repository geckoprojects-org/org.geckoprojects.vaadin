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
 *     Stefan Bishof - API and implementation
 *     Tim Ward - implementation
 */
package {{basePackageName}};

import org.gecko.vaadin.whiteboard.annotations.VaadinComponent;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;


@Theme(value = "wbapp")
@PWA(name = "Vaadin Application", shortName = "Vaadin Application", offlineResources = {"images/Logo_CIM.png"})
@Component(service=AppShell.class, scope = ServiceScope.PROTOTYPE)
@VaadinComponent()
public class AppShell implements AppShellConfigurator {

	private static final long serialVersionUID = 1L;
	
}