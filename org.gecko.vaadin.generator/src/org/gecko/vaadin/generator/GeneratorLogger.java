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
package org.gecko.vaadin.generator;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;

import aQute.bnd.service.generate.BuildContext;

/**
 * @author mark
 *
 */
public class GeneratorLogger implements AutoCloseable {
	
	private PrintStream logWriter;
	
	private enum Level {
		INFO,
		DEBUG,
		ERROR,
		WARN;
		
		public String getLevelString() {
			return "[" + toString() + "] ";
		}
	}
	
	public void info(String message) {
		log(Level.INFO, message, null);
	}
	
	public void debug(String message) {
		debug(message, null);
	}
	
	public void debug(String message, Throwable cause) {
		log(Level.DEBUG, message, cause);
	}
	
	public void warn(String message) {
		warn(message, null);
	}
	
	public void warn(String message, Throwable cause) {
		log(Level.WARN, message, cause);
	}

	public void error(String message) {
		error(message, null);
	}
	
	public void error(String message, Throwable cause) {
		log(Level.ERROR, message, cause);
	}
	
	private void log(Level level, String message, Throwable cause) {
		if (logWriter != null) {
			logWriter.println(level.getLevelString() + message);
			if (cause != null) {
				cause.printStackTrace(logWriter);
			}
			logWriter.flush();
		} else {
			System.err.println(level.getLevelString() + message);
			cause.printStackTrace();
		}
	}
	
	private GeneratorLogger(BuildContext context) throws IOException {
		File logFile = new File(context.getBase(), "npm-generate.log");
		Files.deleteIfExists(logFile.toPath());
		logFile.createNewFile();
		logWriter = new PrintStream(logFile);
	}
	
	public static GeneratorLogger getLogger(BuildContext context) throws IOException {
		return new GeneratorLogger(context);
	}

	@Override
	public void close() throws Exception {
		if (logWriter != null) {
			logWriter.close();
		}
	}

}
