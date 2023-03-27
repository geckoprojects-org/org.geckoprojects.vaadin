/**
 * Copyright (c) 2012 - 2018 Data In Motion and others.
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

import java.time.LocalDate;
import java.util.Date;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.gecko.vaadin.emf.databinding.converter.DateToLocalDateConverter;
import org.gecko.vaadin.emf.databinding.impl.ESetter;
import org.gecko.vaadin.emf.databinding.impl.EValueProvider;

import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.PropertySet;
import com.vaadin.flow.data.converter.Converter;

public class EMFBinder<T extends EObject> extends Binder<T>{	

	/** serialVersionUID */
	private static final long serialVersionUID = 7542931227027560024L;

	public EMFBinder(PropertySet<T> propertySet) {
		super(propertySet);
	}
	
	public <FIELDVALUE> Binding<T, FIELDVALUE> doBind(HasValue<?, FIELDVALUE> field, EStructuralFeature feature) {
		return forField(field).bind(EValueProvider.create(feature), ESetter.create(feature));
	}
	
	@SuppressWarnings("unchecked")
	public <FIELDVALUE> EMFBinder<T> bind(HasValue<?, FIELDVALUE> field, EStructuralFeature feature) {
		EClassifier eType = feature.getEType();
		if (eType.equals(EcorePackage.Literals.EDATE)) {
			Converter<LocalDate, Date> converter = new DateToLocalDateConverter();		
			forField(field).withConverter((Converter<FIELDVALUE, Date>) converter).bind(EValueProvider.create(feature), ESetter.create(feature));
		} else {
			doBind(field, feature);
		}
		return this;
	}
	
	public <FIELDVALUE> EMFBinder<T> bindRequired(HasValue<?, FIELDVALUE> field, EStructuralFeature feature, String message) {
		forField(field).asRequired(message).bind(EValueProvider.create(feature), ESetter.create(feature));
		return this;
	}
	
	public EMFBinder<T> bean(T object) {
		setBean(object);
		return this;
	}
}
