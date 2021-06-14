/**
 * Copyright (c) 2012 - 2021 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.gecko.vaadin.whiteboard.demo.frontend;

import java.util.Map;

import org.gecko.vaadin.whiteboard.BundleResourceProvider;
import org.gecko.vaadin.whiteboard.Constants;
import org.osgi.annotation.bundle.Capability;
import org.osgi.framework.BundleContext;

/**
 * The link to the java script classes from the flow-client, and flow-data JAR. This content is repackaged into this bundle.
 * We create a configurable Http whiteboard resource, that is mounted from the whiteboard configurator in respect to the
 * Http whiteboards that are used. 
 * @author Mark Hoffmann
 */
@Capability(namespace = Constants.VAADIN_CAPABILITY_NAMESPACE, name = Constants.VAADIN_CAPABILITY_FRONTEND, attribute = "pwa=test")
//@Component(service = VaadinResourceProvider.class, immediate = true, configurationPolicy = ConfigurationPolicy.REQUIRE, name = Constants.CM_DATA)
public class FrontendResourceProvider extends BundleResourceProvider {
	
//	@Activate
	@Override
	public void activate(BundleContext context, Map<String, Object> properties) {
		super.activate(context, properties);
	}
	
}
