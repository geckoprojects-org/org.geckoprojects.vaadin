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
package org.gecko.vaadin.whiteboard.spi;

import org.gecko.vaadin.whiteboard.Constants;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;

public class ServiceReferenceEvent<T extends Object> {

	private final ServiceReference<T> reference;
	private final Bundle registrar;
	private final Type type;

	public enum Type {
		ADD,
		MODIFY,
		REMOVE
	}
	
	public ServiceReferenceEvent(ServiceReference<T> reference, Type type) {
		this.reference = reference;
		this.registrar = reference.getBundle();
		this.type = type;
	}
	
	public ServiceReference<T> getReference() {
		return reference;
	}
	
	public Bundle getRegistrar() {
		return registrar;
	}
	
	public Type getType() {
		return type;
	}
	
	public boolean isComponent() {
		Object isComponent = reference.getProperty(Constants.VAADIN_COMPONENT);
		if(isComponent == null) {
			return false;
		}
		return isComponent instanceof Boolean ? (boolean) isComponent : Boolean.parseBoolean(isComponent.toString());
	}

}
