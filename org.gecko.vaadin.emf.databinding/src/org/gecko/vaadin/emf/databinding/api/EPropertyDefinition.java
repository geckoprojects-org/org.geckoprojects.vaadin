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

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.vaadin.flow.data.binder.PropertyDefinition;

/**
 * 
 * @author ilenia
 * @since Mar 24, 2023
 */
public interface EPropertyDefinition<EO extends EObject, V> extends PropertyDefinition<EO, V> {
	
	public EStructuralFeature getFeature();
	
	public EClass getEClass();

}
