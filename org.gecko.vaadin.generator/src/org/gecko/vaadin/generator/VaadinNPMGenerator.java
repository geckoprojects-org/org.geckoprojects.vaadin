package org.gecko.vaadin.generator;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.vaadin.flow.plugin.base.BuildFrontendUtil;
import com.vaadin.flow.plugin.base.PluginAdapterBase;
import com.vaadin.flow.plugin.base.PluginAdapterBuild;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.frontend.FrontendTools;
import com.vaadin.flow.server.frontend.FrontendUtils;
import com.vaadin.flow.server.frontend.installer.NodeInstaller;
import com.vaadin.flow.server.frontend.scanner.ClassFinder;

import aQute.bnd.build.Container;
import aQute.bnd.header.Attrs;
import aQute.bnd.header.OSGiHeader;
import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.Constants;
import aQute.bnd.service.externalplugin.ExternalPlugin;
import aQute.bnd.service.generate.BuildContext;
import aQute.bnd.service.generate.Generator;

@ExternalPlugin(name = "geckoVaadinNPM", objectClass = Generator.class)
public class VaadinNPMGenerator implements Generator<GeneratorOptions>, PluginAdapterBase, PluginAdapterBuild {

	public static final String INCLUDE_FROM_COMPILE_DEPS_REGEX = ".*(/|\\\\)(portlet-api|javax\\.servlet-api)-.+jar$";

	private GeneratorLogger logger;
	private BuildContext context;


	@Override
	public Optional<String> generate(BuildContext context, GeneratorOptions options) throws Exception {
		this.context = context;
		logger = GeneratorLogger.getLogger(context);
		try {

			logger.info("Generating frontend ...");
			updateHeader(context, Constants.REQUIRE_CAPABILITY, new TreeSet<>());
			updateHeader(context, Constants.PROVIDE_CAPABILITY, new TreeSet<>());

			logger.info(" - Propagate build info ...");
			BuildFrontendUtil.propagateBuildInfo(this);

			logger.info(" - Prepare frontend ...");
			BuildFrontendUtil.prepareFrontend(this);

			logger.info(" - Update build fileFrontend ...");
			BuildFrontendUtil.updateBuildFile(this);

			logger.info(" - Run node updater ...");
			BuildFrontendUtil.runNodeUpdater(this);

			if (generateBundle()) {
				logger.info(" - Run webpack ...");
				BuildFrontendUtil.runWebpack(this);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} finally {
			logger.info("Finished generating frontend");
			logger.close();
		}

		return Optional.empty();
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
     * Copy the `webpack.generated.js` from the specified URL. Default is the
     * template provided by this plugin. Set it to empty string to disable the
     * feature.
     * @Parameter(defaultValue = FrontendUtils.WEBPACK_GENERATED)
     */
	@Override
	public String webpackGeneratedTemplate() {
		return FrontendUtils.WEBPACK_GENERATED;
	}

	/**
	 * Copy the `webpack.generated.js` from the specified URL. Default is the
	 * template provided by this plugin. Set it to empty string to disable the
	 * feature.
	 * @Parameter(defaultValue = FrontendUtils.WEBPACK_GENERATED)
	 */
	@Override
	public String webpackTemplate() {
		return FrontendUtils.WEBPACK_CONFIG;
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
//		return projectBaseDirectory().resolve("generated/" + com.vaadin.flow.server.frontend.FrontendUtils.FRONTEND).toFile();
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

		try {
			logInfo("Getting class finder");
			List<String> cp = new ArrayList<>();
			getJarFiles().stream().map(File::getAbsolutePath).forEach(cp::add);
			ClassFinder f = BuildFrontendUtil.getClassFinder(cp);
			try {
				f.loadClass(Route.class.getName());
			} catch (ClassNotFoundException e) {
				logError("Cannot find Route.class", e);
			}
			return f;
			
		} catch (Exception e) {
			logError("Error getting class finder ", e);
			return null;
		}
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
//			buildpath = context.getProject().getBuildpath();
			String bsn = context.getProject().getBundleName();
			Set<String> ignoreSet = new HashSet<String>();
			ignoreSet.add(bsn);
			ignoreSet.add("org.gecko.vaadin.whiteboard");
			ignoreSet.add("org.gecko.vaadin.whiteboard.api");
			ignoreSet.add("org.gecko.vaadin.whiteboard.push");
			buildpath = context.getProject().getRunbundles();
			
			return buildpath.stream()
					.filter(c->!ignoreSet.contains(c.getBundleSymbolicName()))
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
     * Whether or not we are running in legacy V14 bootstrap mode. 'True' if
     * defined or if it's set to true otherwise <code>null</code>.
     * @Parameter(defaultValue = "${vaadin.useDeprecatedV14Bootstrapping}")
     */
	@Override
	public String getUseDeprecatedV14Bootstrapping() {
		return null;
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
		return true;
	}

    /**
     * Whether or not we are running in productionMode.
     * @Parameter(defaultValue = "${vaadin.productionMode}")
     */
	@Override
	public boolean productionMode() {
		return true;
	}

    /**
     * The folder where `package.json` file is located. Default is project root
     * dir.
     * @Parameter(defaultValue = "${project.basedir}")
     */
	@Override
	public Path projectBaseDirectory() {
		return context.getBase().toPath();
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

		//		return projectBaseDirectory().resolve("generated")
		//				.resolve(com.vaadin.flow.server.Constants.VAADIN_WEBAPP_RESOURCES).toFile();
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

}