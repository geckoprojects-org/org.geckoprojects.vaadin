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
package org.gecko.vaadin.emf.databinding.api;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

/**
 * 
 * @author ilenia
 * @since Mar 24, 2023
 */
public class  EMFListBinder<EO extends EObject> {
	
	private EMFBinderFactory binderFactory;
	private EClass eClass;
	private List<EMFBinder<EO>> binders = new ArrayList<>();
	
	public EMFListBinder(EClass eClass, EMFBinderFactory binderFactory) {
		this.eClass = eClass;
		this.binderFactory = binderFactory;
	}
	
	public EMFBinder<EO> addEMFBinder() {
		EMFBinder<EO> binder = binderFactory.createBinder(eClass);
		binders.add(binder);
		return binder;
	}
	
	public void removeEMFBinders() {
		binders.clear();
	}
	
	public EMFListBinder<EO> bind(EList<EO> bean) {
		binders.forEach(b -> bean.add(b.getBean()));
		return this;
	}
	
}
