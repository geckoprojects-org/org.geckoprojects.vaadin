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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.reflections.util.ConfigurationBuilder;

import com.vaadin.flow.plugin.base.BuildFrontendUtil;
import com.vaadin.flow.plugin.base.PluginAdapterBase;
import com.vaadin.flow.plugin.base.PluginAdapterBuild;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.frontend.FrontendTools;
import com.vaadin.flow.server.frontend.FrontendToolsSettings;
import com.vaadin.flow.server.frontend.FrontendUtils;
import com.vaadin.flow.server.frontend.installer.NodeInstaller;
import com.vaadin.flow.server.frontend.scanner.ClassFinder;
import com.vaadin.flow.utils.FlowFileUtils;

import aQute.bnd.build.Container;
import aQute.bnd.build.Project;
import aQute.bnd.header.Attrs;
import aQute.bnd.header.OSGiHeader;
import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.Constants;
import aQute.bnd.service.externalplugin.ExternalPlugin;
import aQute.bnd.service.generate.BuildContext;
import aQute.bnd.service.generate.Generator;

/**
 * Bnd generator as external plugin that is capable to generate Vaadins Frontend JS code
 * @author Mark Hoffmann
 *
 */
@SuppressWarnings("restriction")
@ExternalPlugin(name = "geckoVaadinNPM", objectClass = Generator.class)
public class VaadinNPMGenerator implements Generator<GeneratorOptions>, PluginAdapterBase, PluginAdapterBuild {

	private static final String[] CLEANUP_RESOURCES = new String[] {"vite.config.ts", "vite.generated.ts", "types.d.ts", "webpack.config.js", "webpack.generated.js", "tsconfig.json", "pnpm-lock.yaml", "pnpmfile.js", "package.json", ".npmrc", "generated/index.html", "generated/index.ts", "generated/sw.ts", "generated/plugins/", "generated/flow-frontend/", "generated/frontend/", "node_modules/", "frontend/"};
	public static final String INCLUDE_FROM_COMPILE_DEPS_REGEX = ".*(/|\\\\)(portlet-api|javax\\.servlet-api)-.+jar$";
	public static List<String> SCAN_EXCLUDES_PATH = List.of("org/apache", "com/google", "org.zeroturnaround", "biz.aQute", "javassist", "net.bytebuddy", "org.slf4j");

	private GeneratorLogger logger;
	private Project project;
	private File basePath;
	private ClassFinder classFinder;


	@Override
	public Optional<String> generate(BuildContext context, GeneratorOptions options) throws Exception {
		project = context.getProject();
		basePath = context.getBase();
		logger = getLogger();
		logger.info("Generating frontend ...");
		updateHeader(context, Constants.REQUIRE_CAPABILITY, new TreeSet<>());
		updateHeader(context, Constants.PROVIDE_CAPABILITY, new TreeSet<>());
		
		doGenerate(project);
		
		boolean cleanup = "ALL".equals(context.getProperty("cleanup", "NONE"));
		if (cleanup) {
			logger.info("Doing cleanup ...");
			for(String r : CLEANUP_RESOURCES) {
				File fr = project.getFile(r);
				if (fr.isDirectory() && fr.exists()) {
					Files.walk(fr.toPath())
				      .sorted(Comparator.reverseOrder())
				      .map(Path::toFile)
				      .forEach(File::delete);
					if ( fr.exists()) {
						fr.delete();
					}
					logger.info("Removed folder " + r);
					continue;
				}
				if (fr.exists()) {
					fr.delete();
					logger.info("Removed file " + r);
				}
			}
		}
		logger.info("Finished cleanup");
		logger.close();
		return Optional.empty();
	}
	
	protected void doGenerate(Project project) throws Exception {
		this.project = project;
		basePath = project.getBase();
		logger = getLogger();
		try {

			logger.info(" - Propagate build info ...");
			BuildFrontendUtil.propagateBuildInfo(this);

			logger.info(" - Prepare frontend ...");
			BuildFrontendUtil.prepareFrontend(this);

			logger.info(" - Update build fileFrontend ...");
			BuildFrontendUtil.updateBuildFile(this);

			logger.info(" - Run node updater ...");
			BuildFrontendUtil.runNodeUpdater(this);

			if (generateBundle()) {
				logger.info(" - Run frontend build ...");
				FrontendToolsSettings settings = getFrontendToolsSettings(this);
		        FrontendTools tools = new FrontendTools(settings);
		        tools.validateNodeAndNpmVersion();
		        BuildFrontendUtil.runVite(this, tools);
			}
			logger.info("Finished generating frontend");
		} catch (Exception e) {
			logger.error("Error generating Vaadin Frontend " + e.getMessage(), e);
		}
	}
	
	private GeneratorLogger getLogger() throws IOException {
		return logger != null ? logger : GeneratorLogger.getLogger(basePath, project.getLogger());
	}

	/**
	 * Updates specified header, sorting and removing duplicates. Destroys contents
	 * of set parameter.
	 *
	 * @param context
	 * @param name     header name
	 * @param set      values to add to header; contents are not preserved.
	 */
	private void updateHeader(BuildContext context, String name, TreeSet<String> set) {
		if (!set.isEmpty()) {
			String value = context.getProperty(name);
			if (value != null) {
				Parameters p = OSGiHeader.parseHeader(value);
				for (Map.Entry<String, Attrs> entry : p.entrySet()) {
					StringBuilder sb = new StringBuilder(entry.getKey());
					if (entry.getValue() != null) {
						sb.append(";");
						entry.getValue().append(sb);
					}
					set.add(sb.toString());
				}
			}
			String header = String.join(",", set);
			context.setProperty(name, header);
		}
	}

	/**
     * Defines the project frontend directory from where resources should be
     * copied from for use with webpack.
     * @Parameter(defaultValue = "${project.basedir}/"
     *       + Constants.LOCAL_FRONTEND_RESOURCES_PATH)
     */
	@Override
	public File frontendResourcesDirectory() {
		return projectBaseDirectory().resolve("META-INF/resources/frontend").toFile();
	}

	/**
     * Whether to generate a bundle from the project frontend sources or not.
     */
	@Override
	public boolean generateBundle() {
		return true;
	}

	/**
     * Whether to generate embeddable web components from WebComponentExporter
     * inheritors.
     */
	@Override
	public boolean generateEmbeddableWebComponents() {
		return true;
	}

	/**
     * Whether to use byte code scanner strategy to discover frontend
     * components.
     * @Parameter(defaultValue = "true")
     */
	@Override
	public boolean optimizeBundle() {
		return true;
	}

	/**
     * Whether to run <code>npm install</code> after updating dependencies.
     */
	@Override
	public boolean runNpmInstall() {
		return true;
	}


	/**
     * Application properties file in Spring project.
     * @Parameter(defaultValue = "${project.basedir}/src/main/resources/application.properties")
     */
	@Override
	public File applicationProperties() {
		return projectBaseDirectory().resolve("res/application.properties").toFile();
	}

	/**
     * Whether or not insert the initial Uidl object in the bootstrap index.html
     * @Parameter(defaultValue = "${vaadin."
     *       + Constants.SERVLET_PARAMETER_INITIAL_UIDL + "}")
     */
	@Override
	public boolean eagerServerLoad() {
		return false;
	}

	/**
     * A directory with project's frontend source files.
     * @Parameter(defaultValue = "${project.basedir}/" + FRONTEND)
     */
	@Override
	public File frontendDirectory() {
		return projectBaseDirectory().resolve(com.vaadin.flow.server.frontend.FrontendUtils.FRONTEND).toFile();
	}

	/**
    * The folder where flow will put generated files that will be used by
    * webpack.
    * @Parameter(defaultValue = "${project.build.directory}/" + FRONTEND)
    */
	@Override
	public File generatedFolder() {
		return projectBaseDirectory().resolve(buildFolder() + "/").resolve(com.vaadin.flow.server.frontend.FrontendUtils.FRONTEND).toFile();
	}

	/**
     * The folder where flow will put TS API files for client projects.
     * @Parameter(defaultValue = "${project.basedir}/" + FRONTEND + "/generated")
     */
	@Override
	public File generatedTsFolder() {
		return projectBaseDirectory().resolve(com.vaadin.flow.server.frontend.FrontendUtils.FRONTEND)
				.resolve("generated").toFile();
	}


    /**
     * Generates a List of ClasspathElements (Run and CompileTime) from a
     * Bnd project.
     *
     * @param project
     *            a given MavenProject
     * @return List of ClasspathElements
     */
	@Override
	public ClassFinder getClassFinder() {
		if (classFinder != null) {
			return classFinder;
		}
		try {
			logInfo("Getting class finder");
			List<String> cp = getJarFiles().stream().map(File::getAbsolutePath).collect(Collectors.toList());
			ClassFinder f = doGetClassFinder(cp);
			try {
				f.loadClass(Route.class.getName());
				classFinder = f;
			} catch (ClassNotFoundException e) {
				logError("Cannot load Route.class with class finder", e);
			}
			return f;
			
		} catch (Exception e) {
			logError("Error getting class finder ", e);
			return null;
		}
	}
	
	private ClassFinder doGetClassFinder(List<String> classpathElements) {
    	URL[] urls = classpathElements.stream().distinct().map(File::new)
                .map(FlowFileUtils::convertToUrl).toArray(URL[]::new);
    	ClassLoader cl = new URLClassLoader(urls, getClass().getClassLoader());
    	ClassLoader ctxClassLoader = new URLClassLoader(urls,
                Thread.currentThread().getContextClassLoader());
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder()
                .addClassLoaders(cl, ctxClassLoader).setExpandSuperTypes(false)
                .addUrls(urls);
        Predicate<String> filter = resourceName -> !SCAN_EXCLUDES_PATH.contains(resourceName)
        		&& resourceName.endsWith(".class")
                && !resourceName.endsWith("module-info.class");
        configurationBuilder.setInputsFilter(filter);
		return new GeneratorClassFinder(configurationBuilder, cl);
	}

	/**
     * The Jar Files that would be searched.
     *
     * @return {@link Set} of {@link File}
     */
	@Override
	public Set<File> getJarFiles() {
		Collection<Container> buildpath;
		try {
			String bsn = project.getName();
			Set<String> filterSet = new HashSet<String>();
			filterSet.add(bsn + ".jar");
			filterSet.add("org.gecko.vaadin.whiteboard.api.jar");
			filterSet.add("org.gecko.vaadin.whiteboard.jar");
			buildpath = project.getRunbundles();
			return buildpath.stream()
					.filter(c->!filterSet.contains(c.getBundleSymbolicName()))
					.map(Container::getFile)
					.collect(Collectors.toSet());
		} catch (Exception e) {
			logError("Cannot get JAR files, returning empty set", e);
			return Collections.emptySet();
		}
	}

    /**
     * Indicates that it is a Jar Project.
     *
     * @return boolean - indicates that it is a Jar Project
     */
	@Override
	public boolean isJarProject() {
		return true;
	}



    /**
     * Checks the debug Mode.
     *
     * @return boolean
     */
	@Override
	public boolean isDebugEnabled() {
		return true;
	}

    /**
     * Java source folders for scanning.
     * @Parameter(defaultValue = "${project.basedir}/src/main/java")
     */
	@Override
	public File javaSourceFolder() {
		return projectBaseDirectory().resolve("src").toFile();
	}

    /**
     * Delegates a debug-Message to a logger.
     *
     * @param debugMessage
     *            to be logged.
     */
	@Override
	public void logDebug(CharSequence debugMessage) {
		logger.debug(debugMessage.toString());
	}

    /**
     * Delegates a info-Message to a logger.
     *
     * @param infoMessage
     *            to be logged.
     */
	@Override
	public void logInfo(CharSequence infoMessage) {
		logger.info(infoMessage.toString());
	}

    /**
     * delegates a warning-Message to a logger.
     *
     * @param warningMessage
     *            to be logged.
     */
	@Override
	public void logWarn(CharSequence warningMessage) {
		logWarn(warningMessage);
	}

    /**
     * Delegates a warning-Message to a logger.
     *
     * @param warningMessage
     *            to be logged.
     * @param throwable
     *            to be logged.
     */
	@Override
	public void logWarn(CharSequence warningMessage, Throwable throwable) {
		logger.warn(warningMessage.toString(), throwable);

	}

    /**
     * Delegates a error-Message to a logger.
     *
     * @param warning
     *            to be logged.
     * @param e
     *            to be logged.
     */
	@Override
	public void logError(CharSequence errorMessage, Throwable throwable) {
		logger.error(errorMessage.toString(), throwable);

	}

    /**
     * Download node.js from this URL. Handy in heavily firewalled corporate
     * environments where the node.js download can be provided from an intranet
     * mirror. Defaults to null which will cause the downloader to use
     * {@link NodeInstaller#DEFAULT_NODEJS_DOWNLOAD_ROOT}.
     * <p>
     * </p>
     * Example: <code>"https://nodejs.org/dist/"</code>.
     * @Parameter(property = "node.download.root", defaultValue = NodeInstaller.DEFAULT_NODEJS_DOWNLOAD_ROOT)
     */
	@Override
	public URI nodeDownloadRoot() throws URISyntaxException {
		return URI.create(NodeInstaller.DEFAULT_NODEJS_DOWNLOAD_ROOT);
	}

    /**
     * The node.js version to be used when node.js is installed automatically by
     * Vaadin, for example `"v16.0.0"`. Defaults to null which uses the
     * Vaadin-default node version - see {@link FrontendTools} for details.
     * @Parameter(property = "node.version", defaultValue = FrontendTools.DEFAULT_NODE_VERSION)
     */
	@Override
	public String nodeVersion() {
		return FrontendTools.DEFAULT_NODE_VERSION;
	}

    /**
     * The folder where `package.json` file is located. Default is project root
     * dir.
     * @Parameter(defaultValue = "${project.basedir}")
     */
	@Override
	public File npmFolder() {
		return projectBaseDirectory().toFile();
	}

    /**
     * Default generated path of the OpenAPI json.
     * @Parameter(defaultValue = "${project.build.directory}/generated-resources/openapi.json")
     */
	@Override
	public File openApiJsonFile() {
		return projectBaseDirectory().resolve(buildFolder() + "/").resolve("/generated-resources/openapi.json").toFile();
	}

    /**
     * Instructs to use pnpm for installing npm frontend resources.
     * @Parameter(property = Constants.SERVLET_PARAMETER_ENABLE_PNPM, defaultValue = Constants.ENABLE_PNPM_DEFAULT_STRING)
     */
	@Override
	public boolean pnpmEnable() {
		return false;
	}


    /**
     * The folder where `package.json` file is located. Default is project root
     * dir.
     * @Parameter(defaultValue = "${project.basedir}")
     */
	@Override
	public Path projectBaseDirectory() {
		return basePath == null ? null : basePath.toPath();
	}

    /**
     * Whether vaadin home node executable usage is forced. If it's set to
     * {@code true} then vaadin home 'node' is checked and installed if it's
     * absent. Then it will be used instead of globally 'node' or locally
     * installed installed 'node'.
     * @Parameter(property = Constants.REQUIRE_HOME_NODE_EXECUTABLE, defaultValue = "false")
     */
	@Override
	public boolean requireHomeNodeExec() {
		return false;
	}

    /**
     * Defines the output directory for generated non-served resources, such as
     * the token file.
     *
     * @return {@link File}
     */
	@Override
	public File servletResourceOutputDirectory() {
		return projectBaseDirectory().resolve(com.vaadin.flow.server.Constants.VAADIN_SERVLET_RESOURCES).toFile();
	}

    /**
     * The folder where webpack should output index.js and other generated
     * files.
     * @Parameter(defaultValue = "${project.build.outputDirectory}/"
     *        + VAADIN_WEBAPP_RESOURCES)
     */
	@Override
	public File webpackOutputDirectory() {
		return projectBaseDirectory().resolve(com.vaadin.flow.server.Constants.VAADIN_WEBAPP_RESOURCES).toFile();
	}

    /**
     * Build directory for the project.
     * @Parameter(property = "build.folder", defaultValue = "${project.build.directory}")
     */
	@Override
	public String buildFolder() {
		return "generated";
	}

	@Override
	public File javaResourceFolder() {
		return projectBaseDirectory().resolve("resources").toFile();
	}

	@Override
	public boolean nodeAutoUpdate() {
		return false;
	}

	@Override
	public boolean useGlobalPnpm() {
		return false;
	}

	@Override
	public List<String> postinstallPackages() {
		return Collections.emptyList();
	}
	
	/* 
	 * (non-Javadoc)
	 * @see com.vaadin.flow.plugin.base.PluginAdapterBase#isFrontendHotdeploy()
	 */
	@Override
	public boolean isFrontendHotdeploy() {
		return false;
	}
	
    private static FrontendToolsSettings getFrontendToolsSettings(
            PluginAdapterBase adapter) throws URISyntaxException {
        FrontendToolsSettings settings = new FrontendToolsSettings(
                adapter.npmFolder().getAbsolutePath(),
                () -> FrontendUtils.getVaadinHomeDirectory().getAbsolutePath());
        settings.setNodeDownloadRoot(adapter.nodeDownloadRoot());
        settings.setNodeVersion(adapter.nodeVersion());
        settings.setAutoUpdate(adapter.nodeAutoUpdate());
        settings.setUseGlobalPnpm(adapter.useGlobalPnpm());
        settings.setForceAlternativeNode(adapter.requireHomeNodeExec());

        return settings;
    }

	

}