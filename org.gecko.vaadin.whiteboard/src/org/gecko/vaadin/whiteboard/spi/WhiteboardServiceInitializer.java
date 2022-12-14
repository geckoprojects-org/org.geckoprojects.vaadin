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

import static org.gecko.vaadin.whiteboard.Constants.CM_INIT;

import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.gecko.vaadin.whiteboard.di.WhiteboardLookup;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

import com.vaadin.flow.di.Lookup;
import com.vaadin.flow.internal.ApplicationClassLoaderAccess;
import com.vaadin.flow.internal.VaadinContextInitializer;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinContext;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.server.VaadinServletContext;

@Component(immediate = true, service = { VaadinServiceInitListener.class,
		HttpSessionListener.class, ServletContextListener.class }, 
	configurationPolicy = ConfigurationPolicy.REQUIRE, 
	name = CM_INIT, 
	property = {
              HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER + "=true",
              HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME + "=test", }
)
public class WhiteboardServiceInitializer implements HttpSessionListener, ServletContextListener, VaadinServiceInitListener {

	private static final long serialVersionUID = 1L;
	private Logger logger = Logger.getLogger(WhiteboardServiceInitializer.class.getName());
	private BundleContext context;

	@Activate
	public void activate(BundleContext context) {
		this.context = context;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		logger.info("Context destroyed");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextInitialized(ServletContextEvent event) {
		logger.info("Context initialized");
		ServletContext servletContext = event.getServletContext();

        VaadinServletContext context = new VaadinServletContext(servletContext);

        // ServletContextListener::contextInitialized should be called once per
        // servlet context if the class instance is used as a
        // ServletContextListener according to the Servlet spec (or HTTP
        // Whiteboard spec) but in our OSGi support OSGiVaadinServlet may call
        // this method directly for OSGiVaadinInitialization class instance. So
        // to avoid executing the logic here several times let's first check
        // whether initialization has been already done.
        if (context.getAttribute(Lookup.class) != null) {
            return;
        }

        ApplicationClassLoaderAccess classLoaderAccess = context
                .getAttribute(ApplicationClassLoaderAccess.class);
        context.getAttribute(VaadinContextInitializer.class,
                () -> (VaadinContextInitializer) this::initContext);

        if (classLoaderAccess != null) {
            initContext(context);
        }
	}
	
    private void initContext(VaadinContext context) {
        context.removeAttribute(VaadinContextInitializer.class);

        VaadinServletContext servletContext = (VaadinServletContext) context;
        Lookup lookup = servletContext.getAttribute(Lookup.class);
        if (lookup == null) {
        	logger.info("Initialize Lookup with whiteboard lookup");
        	servletContext.setAttribute(Lookup.class, new WhiteboardLookup(this.context));
        }
    }

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSessionListener#sessionCreated(javax.servlet.http.HttpSessionEvent)
	 */
	@Override
	public void sessionCreated(HttpSessionEvent arg0) {
		logger.info("Session created");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSessionListener#sessionDestroyed(javax.servlet.http.HttpSessionEvent)
	 */
	@Override
	public void sessionDestroyed(HttpSessionEvent arg0) {
		logger.info("Session destroyed");
	}

	/* (non-Javadoc)
	 * @see com.vaadin.flow.server.VaadinServiceInitListener#serviceInit(com.vaadin.flow.server.ServiceInitEvent)
	 */
	@Override
	public void serviceInit(ServiceInitEvent event) {
		logger.info("Vaadin service init");
	}

}
