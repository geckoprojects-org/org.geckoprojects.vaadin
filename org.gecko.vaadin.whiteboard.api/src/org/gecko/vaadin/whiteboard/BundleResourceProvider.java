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
package org.gecko.vaadin.whiteboard;

import java.net.URL;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * @author mark
 *
 */
public class BundleResourceProvider implements VaadinResourceProvider {
	
	private Bundle bundle;
	private String prefix = Constants.VAADIN_RESOURCE_DEFAULT_PREFIX;

	public void activate(BundleContext context, Map<String, Object> properties) {
		bundle = context.getBundle();
		String p = (String) properties.get(Constants.VAADIN_RESOURCE_PREFIX);
		if (p != null) {
			p = p.trim();
			if (!p.isBlank() && !p.isEmpty()) {
				if (!p.startsWith("/")) {
					p = "/" + p;
				}
				prefix = p;
			}
		}
	}

	@Override
	public URL getResource(String path) {
		if ((path != null) && 
				(bundle != null) && 
				path.startsWith(prefix)) {
			if (path.startsWith("/")) {
				path = path.substring(1);
			}

			return bundle.getEntry(path);
		}
		return null;
	}

}
