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
