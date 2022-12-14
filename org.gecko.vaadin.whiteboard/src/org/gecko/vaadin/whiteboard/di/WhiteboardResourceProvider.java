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
package org.gecko.vaadin.whiteboard.di;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import com.vaadin.flow.di.ResourceProvider;

/**
 * Dummy {@link ResourceProvider} implementation
 * @author Mark Hoffmann
 *
 */
public class WhiteboardResourceProvider implements ResourceProvider {

	/* (non-Javadoc)
	 * @see com.vaadin.flow.di.ResourceProvider#getApplicationResource(java.lang.String)
	 */
	@Override
	public URL getApplicationResource(String path) {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.vaadin.flow.di.ResourceProvider#getApplicationResources(java.lang.String)
	 */
	@Override
	public List<URL> getApplicationResources(String path) throws IOException {
		return Collections.emptyList();
	}

	/* (non-Javadoc)
	 * @see com.vaadin.flow.di.ResourceProvider#getClientResource(java.lang.String)
	 */
	@Override
	public URL getClientResource(String path) {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.vaadin.flow.di.ResourceProvider#getClientResourceAsStream(java.lang.String)
	 */
	@Override
	public InputStream getClientResourceAsStream(String path) throws IOException {
		return null;
	}

}
