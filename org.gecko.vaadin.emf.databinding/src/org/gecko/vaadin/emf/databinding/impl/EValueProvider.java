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

import com.vaadin.flow.function.ValueProvider;

/**
 * 
 * @author ilenia
 * @since Mar 24, 2023
 */
public class EValueProvider<V extends EObject, T> implements ValueProvider<V, T> {

	private static final long serialVersionUID = -5025789778555018021L;
	private final EStructuralFeature feature;
	
	@SuppressWarnings("unchecked")
	public static <EO extends EObject, TYPE> EValueProvider<EO, TYPE> create(EStructuralFeature feature) {
		Class<TYPE> typeClass = (Class<TYPE>) feature.getEType().getInstanceClass();
		return new EValueProvider<EO, TYPE>(feature, typeClass);
	}
	
	public static <EO extends EObject, TYPE> EValueProvider<EO, TYPE> create(EStructuralFeature feature, Class<TYPE> typeClass) {
		return new EValueProvider<EO, TYPE>(feature, typeClass);
	}

	private EValueProvider(EStructuralFeature feature, Class<T> valueType) {
		this.feature = feature;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T apply(V source) {
		return (T) source.eGet(feature);
	}
}
