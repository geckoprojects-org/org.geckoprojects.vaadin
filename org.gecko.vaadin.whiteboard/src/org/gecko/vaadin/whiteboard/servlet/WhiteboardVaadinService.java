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
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gecko.vaadin.whiteboard.di.OSGiServiceInstantiator;
import org.gecko.vaadin.whiteboard.registry.ServiceObjectRegistry;
import org.gecko.vaadin.whiteboard.registry.ServiceObjectRegistry.ServiceObjectHolder;
import org.gecko.vaadin.whiteboard.spi.WhiteboardApplicationProcessor;

import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.server.PwaRegistry;
import com.vaadin.flow.server.RequestHandler;
import com.vaadin.flow.server.RouteRegistry;
import com.vaadin.flow.server.ServiceException;
import com.vaadin.flow.server.VaadinServlet;
import com.vaadin.flow.server.VaadinServletService;
import com.vaadin.flow.server.communication.IndexHtmlRequestHandler;

public class WhiteboardVaadinService extends VaadinServletService {

	private static final long serialVersionUID = 3598055305160023933L;
	private static final Logger logger = Logger.getLogger(WhiteboardVaadinService.class.getName());
	private final ServiceObjectRegistry<Object> serviceObjectRegistry;
	private final ServiceObjectHolder holder;

	public WhiteboardVaadinService(VaadinServlet servlet,
			DeploymentConfiguration deploymentConfiguration, 
			WhiteboardApplicationProcessor processor, ServiceObjectRegistry.ServiceObjectHolder holder) {
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
				logger.log(Level.SEVERE, "Error initializing PWA registry", e);
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
			OSGiServiceInstantiator defaultInstantiator = new OSGiServiceInstantiator(
					this, serviceObjectRegistry);
			defaultInstantiator.init(this);
			return defaultInstantiator;
		});
	}
	
//	@Override
//	protected List<RequestHandler> createRequestHandlers() throws ServiceException {
//		List<RequestHandler> handlers = super.createRequestHandlers();
//		RequestHandler htmlHandler = handlers.stream().filter(IndexHtmlRequestHandler.class::isInstance).findFirst().orElse(null);
//		
//	}

	/**
	 * By-passing the default mechanism and look also into our frontend provider, if no data was found using the ordinary way
	 */
	@Override
	public InputStream getResourceAsStream(String path) {
		InputStream is = super.getResourceAsStream(path);
		logger.severe("Get resource stream was called!!! Path: " + path);
		return is;
	}

	@Override
	public URL getResource(String path) {
		logger.severe("Get resource was called!!! Path: " + path);
		return null;
	}


}
