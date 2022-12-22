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
package org.gecko.vaadin.whiteboard.registry;

import java.util.Map;
import java.util.Set;

import org.gecko.vaadin.whiteboard.registry.FrontendRegistry.FrontendEntry;
import org.osgi.framework.ServiceObjects;

import com.vaadin.flow.server.PWA;
import com.vaadin.flow.server.startup.ApplicationRouteRegistry;

public interface ServiceObjectRegistry<T extends Object> {
	
	static class ServiceObjectHolder {
		public Map<String, Object> properties;
		public ServiceObjects<Object> serviceObjects;
		public Class<?> clazz;
		public String id;
		public PWA pwa;
		public FrontendEntry frontend;
		public Class<?> getClazz() {
			return clazz;
		}
		public boolean isPWA() {
			return pwa != null;
		}
	}
	
	public ServiceObjectHolder addServiceObject(ServiceObjects<T> serviceObjects, Map<String, Object> properties);
	
	public ServiceObjectHolder updateServiceObject(ServiceObjects<T > serviceObjects, Map<String, Object> properties);
	
	public ServiceObjectHolder removeServiceObject(Map<String, Object> properties);
	
	public Set<Class<?>> getClasses();
	
	public Object createInstance(Class<?> clazz);
	
	public void releaseInstance(Object instance);
	
	public long getVersion();
	
	public ApplicationRouteRegistry getRouteRegistry();

}
