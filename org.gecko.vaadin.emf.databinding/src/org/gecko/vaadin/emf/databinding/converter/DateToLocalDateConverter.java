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
package org.gecko.vaadin.emf.databinding.converter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

/**
 * A converter that converts from {@link Long} to {@link Date} and back.
 *
 * @author Vaadin Ltd
 * @since 1.0
 */
public class DateToLocalDateConverter implements Converter<LocalDate, Date> {
	
	/** serialVersionUID */
	private static final long serialVersionUID = -5664082759491450445L;
	private final ZoneId defaultZoneId = ZoneId.systemDefault();

    @Override
    public Result<Date> convertToModel(LocalDate value, ValueContext context) {
        if (value == null) {
            return Result.ok(null);
        }
        return Result.ok(Date.from(value.atStartOfDay(defaultZoneId).toInstant()));
    }

    @Override
    public LocalDate convertToPresentation(Date value, ValueContext context) {
        if (value == null) {
            return null;
        }
    	Instant instant = value.toInstant();
    	LocalDate localDate = instant.atZone(defaultZoneId).toLocalDate();
        return localDate;
    }


}
