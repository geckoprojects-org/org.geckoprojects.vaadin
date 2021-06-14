package org.gecko.vaadin.generator;

import java.io.File;
import java.util.Optional;

import aQute.bnd.service.generate.Options;

public interface GeneratorOptions extends Options {
    	Optional<File> output();
    }