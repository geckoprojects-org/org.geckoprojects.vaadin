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

import java.net.URL;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;

import org.gecko.vaadin.whiteboard.registry.ServiceObjectRegistry;
import org.gecko.vaadin.whiteboard.registry.ServiceObjectRegistry.ServiceObjectHolder;
import org.gecko.vaadin.whiteboard.spi.WhiteboardApplicationProcessor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.wiring.BundleWiring;

import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.server.ServiceException;
import com.vaadin.flow.server.StaticFileHandler;
import com.vaadin.flow.server.StaticFileServer;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServlet;
import com.vaadin.flow.server.VaadinServletService;

/**
 * Whiteboard servlet implementation
 * @author Mark Hoffmann
 *
 */
public class WhiteboardVaadinServlet extends VaadinServlet {

	private static final long serialVersionUID = -2046657111200016595L;
	private final WhiteboardApplicationProcessor processor;
	private final BundleContext context;
	private final ServiceObjectHolder holder;
	
	public WhiteboardVaadinServlet(WhiteboardApplicationProcessor processor, ServiceObjectRegistry.ServiceObjectHolder holder) {
		this.processor = processor;
		this.holder = holder;
		this.context = holder.frontend.getBundleContext();
	}
	
	/* (non-Javadoc)
	 * @see com.vaadin.flow.server.VaadinServlet#createServletService(com.vaadin.flow.function.DeploymentConfiguration)
	 */
	@Override
	protected VaadinServletService createServletService(DeploymentConfiguration deploymentConfiguration)
			throws ServiceException {
		WhiteboardVaadinService service = new WhiteboardVaadinService(this,
                deploymentConfiguration, processor, holder);
		service.setClassLoader(context.getBundle().adapt(BundleWiring.class).getClassLoader());
        service.init();
        return service;
	}
	
	/* (non-Javadoc)
	 * @see com.vaadin.flow.server.VaadinServlet#init(javax.servlet.ServletConfig)
	 */
	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);
	}
	
	/* (non-Javadoc)
	 * @see com.vaadin.flow.server.VaadinServlet#createStaticFileHandler(com.vaadin.flow.server.VaadinService)
	 */
	@Override
	protected StaticFileHandler createStaticFileHandler(VaadinService vaadinService) {
		return new StaticFileServer(vaadinService) {
			
			private static final long serialVersionUID = 1L;

			@Override
			protected URL getStaticResource(String path) {
				String resourcePath = "META-INF/resources" + path;
				URL url = context.getBundle().getEntry(resourcePath);
				return url;
			}
		};
	}
	
}
