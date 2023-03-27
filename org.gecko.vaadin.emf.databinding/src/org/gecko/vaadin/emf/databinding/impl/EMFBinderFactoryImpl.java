/**
 * Copyright (c) 2012 - 2023 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.gecko.vaadin.emf.databinding.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.gecko.vaadin.emf.databinding.api.EMFBinder;
import org.gecko.vaadin.emf.databinding.api.EMFBinderFactory;
import org.gecko.vaadin.emf.databinding.api.EMFPropertySetProvider;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

import com.vaadin.flow.data.binder.PropertySet;

/**
 * 
 * @author ilenia
 * @since Mar 24, 2023
 */
@Component(name = "EMFBinderFactory", service = EMFBinderFactory.class, scope = ServiceScope.PROTOTYPE)
public class EMFBinderFactoryImpl implements EMFBinderFactory {
	
	@Reference
	private EMFPropertySetProvider propertySetProvider;

	@SuppressWarnings("unchecked")
	@Override
	public EMFBinder<? extends EObject> createBinder(EClass eclass) {
		if (eclass == null) {
			throw new IllegalArgumentException("EClass parameter must not be null");
		}
		PropertySet<EObject> propertySet = propertySetProvider.getPropertySet(eclass);
		if (propertySet == null) {
			throw new IllegalStateException("Cannot create a binder for EClass " + eclass.getName());
		}
		return new EMFBinder<EObject>(propertySet);
	}
}
