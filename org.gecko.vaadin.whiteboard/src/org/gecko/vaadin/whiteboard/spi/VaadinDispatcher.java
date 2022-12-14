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

import java.util.Map;

import org.osgi.framework.ServiceObjects;

public interface VaadinDispatcher {
	
	public void addComponent(ServiceObjects<Object> serviceObjects, Map<String, Object> properties);
	
	public void modifyComponent(ServiceObjects<Object> serviceObjects, Map<String, Object> properties);
	
	public void removeComponent(Map<String, Object> properties);
	
	public void batchDispatch();
	
}
