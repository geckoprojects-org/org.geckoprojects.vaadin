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

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.ServletException;

import org.gecko.vaadin.whiteboard.Constants;
import org.gecko.vaadin.whiteboard.di.WhiteboardLookup;
import org.gecko.vaadin.whiteboard.di.WhiteboardLookupInitializer;
import org.gecko.vaadin.whiteboard.initializer.WhiteboardApplicationProcessorImpl;
import org.gecko.vaadin.whiteboard.registry.FrontendRegistry;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.servlet.whiteboard.HttpWhiteboardConstants;

import com.vaadin.flow.di.Lookup;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinContext;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.server.VaadinServletContext;

import aQute.bnd.annotation.service.ServiceCapability;

/**
 * Component that handles the Vaadin lifecycle. Depending an the servlet context the initialization is triggerd
 * @author mark
 *
 */
@ServiceCapability(value = WhiteboardApplicationProcessor.class)
@Component(immediate = true, service = { VaadinServiceInitListener.class,
		ServletContextListener.class }, 
configurationPolicy = ConfigurationPolicy.REQUIRE, 
name = CM_INIT, 
property = {
		HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER + "=true",
		HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME + "=test", }
		)
public class WhiteboardServiceInitializer implements ServletContextListener, VaadinServiceInitListener {

	private static final Logger logger = Logger.getLogger(WhiteboardServiceInitializer.class.getName());
	private static final long serialVersionUID = 1L;
	private WhiteboardApplicationProcessorImpl registryProcessor = null;
	private ServiceRegistration<WhiteboardApplicationProcessor> registryProcessorReg;
	@Reference
	private FrontendRegistry frontendRegistry;
	private ComponentContext ctx;

	@Activate
	public void activate(ComponentContext ctx) {
		this.ctx = ctx;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextInitialized(ServletContextEvent event) {
		ServletContext servletContext = event.getServletContext();
		
		String contextName = servletContext.getServletContextName();
		logger.fine(()->"Initializing servlet context for " + contextName);

		VaadinServletContext vaadinContext = new VaadinServletContext(servletContext);

		// ServletContextListener::contextInitialized should be called once per
		// servlet context if the class instance is used as a
		// ServletContextListener according to the Servlet spec (or HTTP
		// Whiteboard spec) but in our OSGi support OSGiVaadinServlet may call
		// this method directly for OSGiVaadinInitialization class instance. So
		// to avoid executing the logic here several times let's first check
		// whether initialization has been already done.
		BundleContext bundleContext = ctx.getBundleContext();
		if (!initializeLookup(vaadinContext, bundleContext)) {
			return;
		}

		// we need the VaadinContext first to initialize the application processor
		logger.fine(()->"Registering WhiteboardApplicationProcessor service " + contextName);
		registryProcessor = new WhiteboardApplicationProcessorImpl(vaadinContext, frontendRegistry);
		registryProcessorReg = bundleContext.registerService(WhiteboardApplicationProcessor.class, registryProcessor, getProperties());
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		String contextName = event.getServletContext().getServletContextName();
		if (registryProcessor != null) {
			registryProcessor.deactivate();
			registryProcessor = null;
			logger.fine(()->"Deactivated WhiteboardRegistryProcessor for " + contextName);
		}
		if (registryProcessorReg != null) {
			registryProcessorReg.unregister();
			logger.fine(()->"Unregistered WhiteboardRegistryProcessor service for " + contextName);
		}
		logger.fine(()->"Servlet context destroyed for " + contextName);
	}

	/* (non-Javadoc)
	 * @see com.vaadin.flow.server.VaadinServiceInitListener#serviceInit(com.vaadin.flow.server.ServiceInitEvent)
	 */
	@Override
	public void serviceInit(ServiceInitEvent event) {
		logger.info("Vaadin service init");
	}

	/**
	 * Removed the default Vaadin initializer and replaces the {@link Lookup} instance by our {@link WhiteboardLookup} implementation
	 * @param context the {@link VaadinContext}
	 */
	private boolean initializeLookup(VaadinContext vaadinContext, BundleContext bundleContext) {
		Map<String, Object> properties = FrameworkUtil.asMap(ctx.getProperties());
		WhiteboardLookupInitializer lookupInitializer = new WhiteboardLookupInitializer(bundleContext, properties);
		try {
			lookupInitializer.initialize(vaadinContext, new HashMap<>(), lookup -> {
				vaadinContext.setAttribute(Lookup.class, lookup);
			});
			return true;
		} catch (ServletException e) {
			logger.log(Level.SEVERE, e, ()-> "Error creating Whiteboard Lookup. Stopping initialization");
			return false;
		}
	}

	private Dictionary<String, ?> getProperties() {
		Dictionary<String, Object> props = new Hashtable<>();
		String applicationName = (String) ctx.getProperties().get(Constants.VAADIN_APPLICATION_NAME);
		if (applicationName != null) {
			props.put(Constants.VAADIN_APPLICATION_NAME, applicationName);
		}
		String pwaName = (String) ctx.getProperties().get(Constants.VAADIN_PWA_NAME);
		if (pwaName != null) {
			props.put(Constants.VAADIN_PWA_NAME, pwaName);
		}
		return props;
	}

}
