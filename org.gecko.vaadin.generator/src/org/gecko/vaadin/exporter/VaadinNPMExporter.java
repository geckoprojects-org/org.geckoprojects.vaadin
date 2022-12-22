/**
 * Copyright (c) 2012 - 2022 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the terms of the 
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.gecko.vaadin.exporter;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.gecko.vaadin.generator.VaadinNPMGenerator;

import aQute.bnd.build.Project;
import aQute.bnd.osgi.Resource;
import aQute.bnd.service.export.Exporter;

public class VaadinNPMExporter extends VaadinNPMGenerator implements Exporter {
	
	public static final String VAADIN_EXPORTER_TYPE = "vaadin.exporter";
	public static final Set<String> types = new HashSet<>();
	
	static {
		types.add(VAADIN_EXPORTER_TYPE);
	}

	@Override
	public String[] getTypes() {
		return types.toArray(new String[1]);
	}

	@Override
	public Entry<String, Resource> export(String type, Project project, Map<String, String> options) throws Exception {
		if(!types.contains(type)) {
			return null;
		}
		doGenerate(project);
		return null;
	}

}
