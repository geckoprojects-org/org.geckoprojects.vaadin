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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.vaadin.flow.data.binder.Setter;

/**
 * 
 * @author ilenia
 * @since Mar 24, 2023
 */
public class ESetter<V extends EObject, T> implements Setter<V, T> {

	private static final long serialVersionUID = -751124929528581621L;
	private final EStructuralFeature feature;
	
	public static <EO extends EObject, TYPE> ESetter<EO, TYPE> create(EStructuralFeature feature) {
		return new ESetter<EO, TYPE>(feature);
	}

	private ESetter(EStructuralFeature feature) {
		this.feature = feature;
	}

	@Override
	public void accept(V bean, T fieldvalue) {
		bean.eSet(feature, fieldvalue);
	}
}
