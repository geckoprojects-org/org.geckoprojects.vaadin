/**
 * Copyright (c) 2012 - 2023 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.gecko.vaadin.whiteboard.di;

import org.gecko.vaadin.whiteboard.registry.ServiceObjectRegistry;

import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.di.InstantiatorFactory;
import com.vaadin.flow.server.VaadinService;

/**
 * 
 * @author ilenia
 * @since Mar 9, 2023
 */
public class OSGiServiceInstantiatorFactory implements InstantiatorFactory {
	
	public Instantiator createInstantiator(VaadinService service, ServiceObjectRegistry<Object> serviceObjectRegistry) {
		return new OSGiServiceInstantiator(service, serviceObjectRegistry);
	}
	
	/* 
	 * (non-Javadoc)
	 * @see com.vaadin.flow.di.InstantiatorFactory#createInstantitor(com.vaadin.flow.server.VaadinService)
	 */
	@Override
	public Instantiator createInstantitor(VaadinService service) {
		return new OSGiServiceInstantiator(service);
	}

}
