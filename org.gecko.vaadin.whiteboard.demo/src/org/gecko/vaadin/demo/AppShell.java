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
