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
package org.gecko.vaadin.whiteboard.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gecko.vaadin.whiteboard.di.OSGiInstantiator;
import org.gecko.vaadin.whiteboard.registry.ServiceObjectRegistry;
import org.gecko.vaadin.whiteboard.registry.ServiceObjectRegistry.ServiceObjectHolder;
import org.gecko.vaadin.whiteboard.spi.VaadinWhiteboardRegistryProcessor;

import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.server.PwaRegistry;
import com.vaadin.flow.server.RouteRegistry;
import com.vaadin.flow.server.ServiceException;
import com.vaadin.flow.server.VaadinServlet;
import com.vaadin.flow.server.VaadinServletService;

public class WhiteboardVaadinService extends VaadinServletService {

	private static final long serialVersionUID = 3598055305160023933L;
	private static final Logger logger = Logger.getLogger(WhiteboardVaadinService.class.getName());
	private final ServiceObjectRegistry<Object> serviceObjectRegistry;
	private final ServiceObjectHolder holder;

	public WhiteboardVaadinService(VaadinServlet servlet,
			DeploymentConfiguration deploymentConfiguration, 
			VaadinWhiteboardRegistryProcessor processor, ServiceObjectRegistry.ServiceObjectHolder holder) {
		super(servlet, deploymentConfiguration);
		this.holder = holder;
		this.serviceObjectRegistry = processor.getServiceObjectRegistry();
	}

	/* (non-Javadoc)
	 * @see com.vaadin.flow.server.VaadinServletService#getPwaRegistry()
	 */
	@Override
	protected PwaRegistry getPwaRegistry() {
		if (holder.isPWA()) {
			try {
				PwaRegistry pwaRegistry = new PwaRegistry(holder.pwa, getServlet().getServletContext());
				return pwaRegistry;
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Error initializing PWA regsitry", e);
			}
		}
		return super.getPwaRegistry();
	}

	@Override
	protected RouteRegistry getRouteRegistry() {
		return serviceObjectRegistry.getRouteRegistry();
	}

	@Override
	protected Instantiator createInstantiator() throws ServiceException {
		return loadInstantiators().orElseGet(() -> {
			OSGiInstantiator defaultInstantiator = new OSGiInstantiator(
					this, serviceObjectRegistry);
			defaultInstantiator.init(this);
			return defaultInstantiator;
		});
	}


	//	/**
	//	 * By-passing the default mechanism and look also into our frontend provider, if no data was found using the ordniary way
	//	 */
	//	@Override
	//	public InputStream getResourceAsStream(String path, WebBrowser browser,
	//			AbstractTheme theme) {
	//		InputStream is = super.getResourceAsStream(path, browser, theme);
	//		if (is == null) {
	//			logger.fine("Cannot get resource stream using the old way for path: " + path + ", using frontned provider instead");
	//			//			String resolved = resolveResource(path, browser);
	//			String resolved = getThemedOrRawPath(path, browser, theme);
	//			URL url = frontendProvider.getResource(resolved);
	//			if (url != null) {
	//				try {
	//					is = url.openConnection().getInputStream();
	//				} catch (IOException e) {
	//					logger.log(Level.SEVERE, "Cannot open stream for path " + resolved, e);
	//				}
	//			}
	//		}
	//		logger.finer("Return resource stream " + is + " for path " + path);
	//		return is;
	//	}
	/**
	 * By-passing the default mechanism and look also into our frontend provider, if no data was found using the ordniary way
	 */
	@Override
	public InputStream getResourceAsStream(String path) {
		System.out.println("resource " + path);
		InputStream is = super.getResourceAsStream(path);
		//		if (is == null) {
		//			logger.info("Cannot get resource stream using the old way for path: " + path + ", using frontend provider instead");
		//			URL url = frontendProvider.getResource(path);
		//			if (url != null) {
		//				try {
		//					is = url.openConnection().getInputStream();
		//				} catch (IOException e) {
		//					logger.log(Level.SEVERE, "Cannot open stream for path " + path, e);
		//				}
		//			}
		//		}
		logger.severe("Return resource stream " + is + " for path " + path);
		return is;
	}

	//	@Override
	//	public URL getResource(String path, WebBrowser browser, AbstractTheme theme) {
	//		String resolved = getThemedOrRawPath(path, browser, theme);
	//		URL url = frontendProvider.getResource(resolved);
	//		if (url == null) {
	//			logger.fine("Cannot get resource using frontend provider" + path + ", using old way instead");
	//			url = super.getResource(path, browser, theme);
	//		}
	//		logger.finer("Return resource URL" + (url == null ? null : url.toString()));
	//		return url;
	//	}
	@Override
	public URL getResource(String path) {
		logger.severe("GET RESOURCE" + path);
		//		URL url = frontendProvider.getResource(path);
		//		if (url == null) {
		//			logger.info("Cannot get resource using frontend provider" + path + ", using old way instead");
		//			url = super.getResource(path);
		//		}
		//		logger.info("Return resource URL" + (url == null ? null : url.toString()));
		//		return url;
		return null;
	}


}
