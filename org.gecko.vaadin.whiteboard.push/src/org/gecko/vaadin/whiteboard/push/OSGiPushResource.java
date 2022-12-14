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
package org.gecko.vaadin.whiteboard.push;

import org.gecko.vaadin.whiteboard.Constants;
import org.osgi.service.component.annotations.*;
import org.osgi.service.http.context.ServletContextHelper;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardResource;

/**
 * Configurable {@link HttpWhiteboardResource}.
 * It creates per configuration an instance with the corresponding {@link ServletContextHelper} select.
 * 
 * This is triggered by the org.gecko.vaadin.whiteboard.spi.VaadinWhiteboardConfigurator
 * 
 * @author Mark Hoffmann
 */
@HttpWhiteboardResource(pattern = "/VAADIN/static/push/*", prefix = "/META-INF/resources/VAADIN/static/push")
@Component(service = OSGiPushResource.class,
	configurationPolicy = ConfigurationPolicy.REQUIRE,
	name = Constants.CM_PUSH
		)
public class OSGiPushResource {
	
}
