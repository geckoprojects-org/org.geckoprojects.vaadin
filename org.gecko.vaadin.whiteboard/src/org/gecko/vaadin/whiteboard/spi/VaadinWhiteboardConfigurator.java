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

import static org.gecko.vaadin.whiteboard.Constants.CM_VAADIN_APPLICATION;
import static org.gecko.vaadin.whiteboard.Constants.TARGET_FILTER_APPLICATION;
import static org.gecko.vaadin.whiteboard.Constants.TARGET_FILTER_RESOURCE;
import static org.gecko.vaadin.whiteboard.Constants.TARGET_NAME_FRONTEND_RESOURCE;
import static org.gecko.vaadin.whiteboard.Constants.TARGET_NAME_REFERENCE_COLLECTOR;
import static org.gecko.vaadin.whiteboard.Constants.TARGET_NAME_APPLICATION_PROCESSOR;
import static org.gecko.vaadin.whiteboard.Constants.VAADIN_APPLICATION_CONTEXT;
import static org.gecko.vaadin.whiteboard.Constants.VAADIN_APPLICATION_NAME;
import static org.gecko.vaadin.whiteboard.Constants.VAADIN_PWA_NAME;
import static org.gecko.vaadin.whiteboard.Constants.VAADIN_CAPABILITY_NAMESPACE;
import static org.gecko.vaadin.whiteboard.Constants.VAADIN_CAPABILITY_WHITEBOARD;
import static org.gecko.vaadin.whiteboard.Constants.VAADIN_COMPONENT_FILTER;
import static org.gecko.vaadin.whiteboard.Constants.VAADIN_WHITEBOARD_FILTER_PATTERN;
import static org.gecko.vaadin.whiteboard.Constants.VAADIN_WHITEBOARD_TARGET;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gecko.vaadin.whiteboard.Constants;
import org.gecko.vaadin.whiteboard.VaadinFrontend;
import org.osgi.annotation.bundle.Capabilities;
import org.osgi.annotation.bundle.Capability;
import org.osgi.framework.BundleContext;
import org.osgi.namespace.implementation.ImplementationNamespace;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.servlet.whiteboard.HttpWhiteboardConstants;
import org.osgi.service.servlet.whiteboard.annotations.RequireHttpWhiteboard;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * Configuration component that configures the Vaadin setup for a certain application.
 * It starts an reference collector service, that is only tracking components, bound to this application.
 * Additionally a whiteboard configuration is started, 
 * @author Mark Hoffmann
 */
@RequireHttpWhiteboard
@Capabilities({
	@Capability(namespace = VAADIN_CAPABILITY_NAMESPACE, name = VAADIN_CAPABILITY_WHITEBOARD, version = "20.0.0"),
	@Capability(namespace = ImplementationNamespace.IMPLEMENTATION_NAMESPACE, name = VAADIN_CAPABILITY_WHITEBOARD, version = "19.0.0"),
})
@Component(configurationPolicy = ConfigurationPolicy.REQUIRE, name = CM_VAADIN_APPLICATION)
@Designate(ocd = VaadinWhiteboardConfigurator.VaadinApplicationConfig.class)
public class VaadinWhiteboardConfigurator {

	private static final Logger logger = Logger.getLogger(VaadinWhiteboardConfigurator.class.getName());
	@Reference
	private ConfigurationAdmin configAdmin;
	private String pwaName;
	private String applicationName;
	private String contextPath;
	private String httpWhiteboardTarget;
	private String applicationFilter;
	private VaadinFrontend frontend = VaadinFrontend.NPM;
	private AtomicLong changeCount = new AtomicLong(0l);
	private Configuration refCollectorConfig;
	private Configuration whiteboardConfig;
	private Configuration resourceConfig;
	private Configuration contextConfig;
	private Configuration initConfig;
	private Configuration pushConfig;
	
	@ObjectClassDefinition
	public @interface VaadinApplicationConfig {
		String vaadin_pwa();
		String vaadin_application_name();
		String vaadin_application_description();
		String vaadin_application_context() default "/*";
		String vaadin_whiteboard_target() default "default";
		VaadinFrontend vaadin_application_frontend() default VaadinFrontend.VITE;
	}

	@Activate
	public void activate(VaadinApplicationConfig config, BundleContext context, ComponentContext ctx) throws ConfigurationException {
		configure(config);
		updateReferenceCollector();
		// register resource provider under this whiteboard/servlet context
		updateResource();
		// register Flow Push resource under this whiteboard/servlet context
		updatePush();
		// create servlet context for context path
		updateContext();
		// register session and context listeners under that servlet context
		updateInitialization();
		// initialize the whiteboard 
		updateWhiteboard();
		logger.info("Activating Vaadin setup for application " + applicationName);
	}

	@Modified
	public void modified(VaadinApplicationConfig config, BundleContext context) throws ConfigurationException {
		long oldCount = changeCount.get();
		configure(config);
		if (oldCount < changeCount.get()) {
			updateReferenceCollector();
			updateResource();
			updatePush();
			updateContext();
			updateInitialization();
			updateWhiteboard();
			logger.info("Modified Vaadin setup for application " + applicationName);
		}
	}

	@Deactivate
	public void deactivate(VaadinApplicationConfig config, BundleContext context) {
		logger.info("Deactivating Vaadin setup for application " + applicationName);
		if (initConfig != null) {
			try {
				initConfig.delete();
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Cannot remove whiteboard initializer for application: " + applicationName, e);
			}
		}
		if (resourceConfig != null) {
			try {
				resourceConfig.delete();
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Cannot remove whiteboard resource provider for application: " + applicationName, e);
			}
		}
		if (pushConfig != null) {
			try {
				pushConfig.delete();
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Cannot remove whiteboard push resource for application: " + applicationName, e);
			}
		}
		if (contextConfig != null) {
			try {
				contextConfig.delete();
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Cannot remove whiteboard context for application: " + applicationName, e);
			}
		}
		if (whiteboardConfig != null) {
			try {
				whiteboardConfig.delete();
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Cannot remove whiteboard configuration for application: " + applicationName, e);
			}
		}
		if (refCollectorConfig != null) {
			try {
				refCollectorConfig.delete();
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Cannot remove reference collector configuration for application: " + applicationName, e);
			}
		}
	}

	private void updateReferenceCollector() {
		if (refCollectorConfig == null) {
			try {
				refCollectorConfig = configAdmin.getFactoryConfiguration(Constants.CM_REFERENCE_COLLECTOR, applicationName);
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Cannot create reference collector configuration for application: " + applicationName, e);
				return;
			}
		}
		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(VAADIN_PWA_NAME, pwaName);
		properties.put(VAADIN_APPLICATION_NAME, applicationName);
		properties.put(VAADIN_COMPONENT_FILTER, applicationFilter);
		try {
			refCollectorConfig.updateIfDifferent(properties);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Cannot update reference collector configuration for application: " + applicationName, e);
		}
	}

	private void updateWhiteboard() {
		if (whiteboardConfig == null) {
			try {
				whiteboardConfig = configAdmin.getFactoryConfiguration(Constants.CM_WHITEBOARD, applicationName);
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Cannot create whiteboard configuration for application: " + applicationName, e);
				return;
			}
		}
		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(VAADIN_PWA_NAME, pwaName);
		properties.put(VAADIN_APPLICATION_NAME, applicationName);
		properties.put(VAADIN_APPLICATION_CONTEXT, contextPath);
		if (httpWhiteboardTarget != null) {
			properties.put(VAADIN_WHITEBOARD_TARGET, httpWhiteboardTarget);
		}
		properties.put(TARGET_NAME_FRONTEND_RESOURCE, String.format(TARGET_FILTER_RESOURCE, frontend.getResourceFilter()));
		properties.put(TARGET_NAME_REFERENCE_COLLECTOR, String.format(TARGET_FILTER_APPLICATION, applicationName));
		properties.put(TARGET_NAME_APPLICATION_PROCESSOR, String.format(TARGET_FILTER_APPLICATION, applicationName));
		try {
			whiteboardConfig.updateIfDifferent(properties);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Cannot update whiteboards configuration for application: " + applicationName, e);
		}
	}
	
	private void updateContext() {
		if (contextConfig == null) {
			try {
				contextConfig = configAdmin.getFactoryConfiguration(Constants.CM_CONTEXT, applicationName);
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Cannot create whiteboard context for application: " + applicationName, e);
				return;
			}
		}
		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(VAADIN_PWA_NAME, pwaName);
		properties.put(VAADIN_APPLICATION_NAME, applicationName);
		properties.put(VAADIN_APPLICATION_CONTEXT, contextPath);
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, applicationName);
		
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, contextPath);
		try {
			contextConfig.updateIfDifferent(properties);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Cannot update whiteboards context for application: " + applicationName, e);
		}
	}
	
	private void updateResource() {
		if (pushConfig == null) {
			try {
				resourceConfig = configAdmin.getFactoryConfiguration(Constants.CM_RESOURCE, applicationName, "?");
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Cannot create whiteboard resource provider for application: " + applicationName, e);
				return;
			}
		}
		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(VAADIN_PWA_NAME, pwaName);
		properties.put(VAADIN_APPLICATION_NAME, applicationName);
		try {
			resourceConfig.updateIfDifferent(properties);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Cannot update whiteboards resource provider for application: " + applicationName, e);
		}
	}
	
	private void updatePush() {
		if (pushConfig == null) {
			try {
				pushConfig = configAdmin.getFactoryConfiguration(Constants.CM_PUSH, applicationName);
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Cannot create whiteboard push resource for application: " + applicationName, e);
				return;
			}
		}
		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(VAADIN_PWA_NAME, pwaName);
		properties.put(VAADIN_APPLICATION_NAME, applicationName);
		properties.put(VAADIN_APPLICATION_CONTEXT, contextPath);
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, String.format("(%s=%s)", HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, applicationName));
		try {
			pushConfig.updateIfDifferent(properties);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Cannot update whiteboards push resource for application: " + applicationName, e);
		}
	}
	
	private void updateInitialization() {
		if (initConfig == null) {
			try {
				initConfig = configAdmin.getFactoryConfiguration(Constants.CM_INIT, applicationName);
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Cannot create whiteboard initializer for application: " + applicationName, e);
				return;
			}
		}
		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(VAADIN_PWA_NAME, pwaName);
		properties.put(VAADIN_APPLICATION_NAME, applicationName);
		properties.put(VAADIN_APPLICATION_CONTEXT, contextPath);
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, true);
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, String.format("(%s=%s)", HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, applicationName));
		try {
			initConfig.updateIfDifferent(properties);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Cannot update whiteboards initializer for application: " + applicationName, e);
		}
	}

	private void configure(VaadinApplicationConfig config) throws ConfigurationException {
		boolean changed = false;
		String v = config.vaadin_application_name();
		if (!v.equals(applicationName)) {
			applicationName = v;
			changed = true;
		}
		v = config.vaadin_pwa();
		if (!v.equals(pwaName)) {
			pwaName = v;
			changed = true;
		}
		v = config.vaadin_application_context();
		if (v == null) {
			throw new ConfigurationException(VAADIN_APPLICATION_CONTEXT, "An application context path must be set");
		} 
		if (!v.equals(contextPath)) {
			contextPath = v;
			changed = true;
		}
		String filterString = String.format(VAADIN_WHITEBOARD_FILTER_PATTERN, applicationName);
		if (applicationFilter == null || !applicationFilter.equals(filterString)) {
			applicationFilter = filterString;
			changed = true;
		}
		VaadinFrontend f = config.vaadin_application_frontend();
		if (!this.frontend.equals(f)) {
			frontend = f;
			changed = true;
		}
		v = config.vaadin_whiteboard_target();
		if (httpWhiteboardTarget != null && 
				!httpWhiteboardTarget.equals(v) || 
				v !=null && 
						httpWhiteboardTarget == null) {
			httpWhiteboardTarget = v;
			changed = true;
		}
		if (changed) {
			changeCount.incrementAndGet();
		}
	}

}
