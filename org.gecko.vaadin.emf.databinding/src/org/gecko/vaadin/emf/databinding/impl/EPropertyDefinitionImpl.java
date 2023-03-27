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

import java.util.Optional;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.gecko.vaadin.emf.databinding.api.EMFPropertySetProvider;
import org.gecko.vaadin.emf.databinding.api.EPropertyDefinition;

import com.vaadin.flow.data.binder.PropertyDefinition;
import com.vaadin.flow.data.binder.PropertySet;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.function.ValueProvider;

/**
 * 
 * @author ilenia
 * @since Mar 24, 2023
 */
public class EPropertyDefinitionImpl<EO extends EObject, V> implements EPropertyDefinition<EO, V> {

	private static final long serialVersionUID = -8079168794629390091L;
	private final EStructuralFeature feature;
	private final Class<V> valueTypeClass;
	private final EMFPropertySetProvider provider;

	@SuppressWarnings("unchecked")
	public EPropertyDefinitionImpl(EStructuralFeature feature, EMFPropertySetProvider provider) {
		this.feature = feature;
		this.provider = provider;
		this.valueTypeClass = (Class<V>) feature.getEType().getInstanceClass();
	}
	
	@Override
	public ValueProvider<EO, V> getGetter() {
		return EValueProvider.create(feature, valueTypeClass);
	}

	@Override
	public Optional<Setter<EO, V>> getSetter() {
		if (!feature.isChangeable()) {
			return Optional.empty();
		}
		return Optional.of(ESetter.create(feature));
	}

	@Override
	public Class<V> getType() {
		return valueTypeClass;
	}

	@Override
	public Class<?> getPropertyHolderType() {
		return getEClass().getInstanceClass();
	}

	@Override
	public String getName() {
		return feature.getName();
	}

	@Override
	public String getCaption() {
		return getName();
	}

	@SuppressWarnings("unchecked")
	@Override
	public PropertySet<EO> getPropertySet() {
		if (feature instanceof EReference) {
			EReference ref = (EReference) feature;
			EClass type = ref.getEReferenceType();
			return (PropertySet<EO>) provider.getPropertySet(type);
		}
		return null;
	}

	@Override
	public PropertyDefinition<EO, ?> getParent() {
		return null;
	}

	@Override
	public EStructuralFeature getFeature() {
		return feature;
	}

	@Override
	public EClass getEClass() {
		return feature.getEContainingClass();
	}


}
