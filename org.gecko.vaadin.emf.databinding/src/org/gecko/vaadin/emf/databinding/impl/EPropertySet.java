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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.gecko.vaadin.emf.databinding.api.EMFPropertySetProvider;
import org.gecko.vaadin.emf.databinding.api.EPropertyDefinition;

import com.vaadin.flow.data.binder.PropertyDefinition;
import com.vaadin.flow.data.binder.PropertySet;

/**
 * 
 * @author ilenia
 * @since Mar 24, 2023
 */
public class EPropertySet<T extends EObject> implements PropertySet<T>{
	
	private static final long serialVersionUID = -7258110647272302147L;
	private final Set<EPropertyDefinition<T, ?>> propertyDefinitions;
	
	public EPropertySet(EClass eClass, EMFPropertySetProvider provider) {
		propertyDefinitions = eClass.getEAllStructuralFeatures()
			.stream()
			.map(feature->new EPropertyDefinitionImpl<T, Object>(feature, provider))
			.collect(Collectors.toSet());
	}

	@Override
	public Stream<PropertyDefinition<T, ?>> getProperties() {
		List<PropertyDefinition<T, ?>> defs = new ArrayList<PropertyDefinition<T,?>>(propertyDefinitions);
		return defs.stream();
	}

	@Override
	public Optional<PropertyDefinition<T, ?>> getProperty(String name) {
		Optional<EPropertyDefinition<T,?>> first = propertyDefinitions.stream().filter(d->d.getName().equals(name)).findFirst();
		PropertyDefinition<T, ?> propDef = first.orElse(null);
		return Optional.ofNullable(propDef);
	}

}
