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
package org.gecko.vaadin.whiteboard.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import static org.gecko.vaadin.whiteboard.Constants.*;
import org.osgi.service.component.annotations.ComponentPropertyType;

@ComponentPropertyType
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface Route {
	
	/**
	 * Prefix for the property name. This value is prepended to each property
	 * name.
	 */
	String PREFIX_ = VAADIN_PROPERTY_PREFIX;

	/**
	 * Service property identifying a filter name.
	 * 
	 * @return The filter name.
	 * @see org.osgi.service.http.whiteboard.HttpWhiteboardConstants#HTTP_WHITEBOARD_FILTER_NAME
	 *      HTTP_WHITEBOARD_FILTER_NAME
	 */
	String value();
	
}
