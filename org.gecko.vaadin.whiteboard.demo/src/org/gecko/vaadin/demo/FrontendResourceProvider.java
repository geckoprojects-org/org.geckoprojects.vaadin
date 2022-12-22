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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gecko.vaadin.whiteboard.Constants;
import org.osgi.annotation.bundle.Capability;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

import com.vaadin.flow.di.ResourceProvider;

/**
 * The link to the java script classes from the flow-client, and flow-data JAR. This content is repackaged into this bundle.
 * We create a configurable Http whiteboard resource, that is mounted from the whiteboard configurator in respect to the
 * Http whiteboards that are used. 
 * @author Mark Hoffmann
 */
@Capability(namespace = Constants.VAADIN_CAPABILITY_NAMESPACE, name = Constants.VAADIN_CAPABILITY_FRONTEND, attribute = "pwa=Vaadin23")
@Component(service = ResourceProvider.class, immediate = true, configurationPolicy = ConfigurationPolicy.REQUIRE, name = Constants.CM_RESOURCE)
public class FrontendResourceProvider implements ResourceProvider {

	private static final Logger logger = Logger.getLogger(FrontendResourceProvider.class.getName());
	private BundleContext context;

	@Activate
	public void activate(BundleContext context, Map<String, Object> properties) {
		this.context = context;
	}

	@Override
	public URL getApplicationResource(String path) {
		return context.getBundle().getResource(path);
	}

	@Override
	public List<URL> getApplicationResources(String path) throws IOException {
		Enumeration<URL> resources = context.getBundle().getResources(path);
		List<URL> urls = new ArrayList<>();
		if (resources == null) {
			return urls;
		}
		while (resources.hasMoreElements()) {
			urls.add(resources.nextElement());
		}
		return urls;
	}

	@Override
	public URL getClientResource(String path) {
		return context.getBundle().getEntry(path);
	}

	@Override
	public InputStream getClientResourceAsStream(String path) throws IOException {
		return getClientResource(path).openStream();
	}
	
	private URL toURL(String stringUrl) {
		try {
			return new URL(stringUrl);
		} catch (MalformedURLException e) {
			logger.log(Level.SEVERE, e, ()->"Cannot convert to URI '" + stringUrl + "'");
		}
		return null;
	}

}
