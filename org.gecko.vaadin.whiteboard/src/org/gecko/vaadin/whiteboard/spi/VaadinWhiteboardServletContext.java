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

import java.net.URL;
import java.util.Set;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.http.context.ServletContextHelper;

import static org.gecko.vaadin.whiteboard.Constants.CM_CONTEXT;

/**
 * Configurable {@link ServletContextHelper} that is registered under the corresponding context root from the Vaadin
 * configuration.
 * 
 * @author Mark Hoffmann
 */
@Component(
        service = ServletContextHelper.class,
        scope = ServiceScope.BUNDLE,
        configurationPolicy = ConfigurationPolicy.REQUIRE,
        name = CM_CONTEXT
)
public class VaadinWhiteboardServletContext extends ServletContextHelper {
	
	private static final String META_INF_RESOURCES = "/META-INF/resources";
	private ServletContextHelper delegatee;
	 
    @Activate
    private void activate(final ComponentContext ctx) {
        delegatee = new ServletContextHelper(ctx.getUsingBundle()) {};
    }
 
    /* (non-Javadoc)
     * @see org.osgi.service.http.context.ServletContextHelper#getResource(java.lang.String)
     */
    @Override
    public URL getResource(String name) {
    	URL resourceUrl = delegatee.getResource(name);
    	if (resourceUrl == null) {
            // this is a workaround specific for getting the correct icon.png in OSGi
            String cpPath = name.startsWith("/") ? META_INF_RESOURCES + name
                    : META_INF_RESOURCES + "/" + name;
            resourceUrl = delegatee.getResource(cpPath);
        }
        return resourceUrl;
    }
 
    /* (non-Javadoc)
     * @see org.osgi.service.http.context.ServletContextHelper#getMimeType(java.lang.String)
     */
    @Override
    public String getMimeType(String name) {
        return delegatee.getMimeType(name);
    }
 
    /* (non-Javadoc)
     * @see org.osgi.service.http.context.ServletContextHelper#getResourcePaths(java.lang.String)
     */
    @Override
    public Set<String> getResourcePaths(String path) {
        return delegatee.getResourcePaths(path);
    }
 
    /* (non-Javadoc)
     * @see org.osgi.service.http.context.ServletContextHelper#getRealPath(java.lang.String)
     */
    @Override
    public String getRealPath(String path) {
        return delegatee.getRealPath(path);
    }

}
