# Vaadin OSGi

## Old Startup

Vaadin-Server - start Bundle Tracker via Activator in server.osgi.ServletContainerInitializerExtender

* Find javax.servlet.ServletContextInitializer from vaadin-server bundle. 
* They look for certain annotations and parse the classes for this annotations like @Route
* Mostly located in server.startup:
    * Create interface
    * Make Service Components out of it, create interface
    * class com.vaadin.flow.server.startup.AnnotationValidator
    * class com.vaadin.flow.server.startup.DevModeInitializer
    * class com.vaadin.flow.server.startup.ErrorNavigationTargetInitializer
    * class com.vaadin.flow.server.startup.RouteRegistryInitializer
    * class com.vaadin.flow.server.startup.ServletVerifier
    * class com.vaadin.flow.server.startup.WebComponentConfigurationRegistryInitializer
    * class com.vaadin.flow.server.startup.WebComponentExporterAwareValidator
* Scanning of classes in VaadinBundleTracker happens like this:
    * Check  Manifest for Vaadin-Extender
    * Get Bundle Wiring
    * List all resource Strings from that bundle
    * Replace .class with empty
    * ignore all classes starting with com.vaadin.server or module-info
    * Load classes for these String resources with the bundle class loader
    * Put the loaded class an the bundle id in a map in OSGiAccess
    * Take the previously scanned initializer
    * go over all classes an reuse ServletContextInitializer (ClassLoaderAwareServletContainerInitializer) and call process from that.
    * This builds up the ApplicationRouteRegistry which is static and gets the faked ServletContext

The VaadinBundleTracker checks the vaadin-server bundle for activation, which is always the case as long as the bundle tracker was started from the vaadin-server BundleActivator#start method.

OSGi Access initializes a special OSGiServletContext, but it doesnt set e.g. the BundleContext as context parameter. This would enable the servlet to easy go into OSGi an load/register services. 

In OSGi a special servlet context is used that. The inner OSGiServletContext class is abstract and therefore a n instance is created by bytebuddy. This context instance is a placeholder for accessing the registries by context.

## The Vaadin Whiteboard

To run the whiteboard, you need the bundle `org.gecko.vaadin.whiteboard`. It currently mounts only on '/*'. It also contains all API from the bundle `org.gecko.vaadin.whiteboard.api`. This is just for development purposes to not need all implementation details, when creating UI service components.

In addition to the whiteboard bundle, you will always need a bundle that provided the capability *vaadin.frontend* in namespace *vaadin.osgi*. We ship the Bower frontend in the bundle `org.gecko.vaadin.whiteboard.bower`, that is also needed by the whiteboard.

The whiteboard itself collects all services with the service property `vaadin.component=true` using the ReferenceCollector. The services currently needs to be annotated with the usualy Vaadin annotations. It is recommended to initialize the UI in the constructor or the @Activate callback

### Configurator
A whiteboard instance is started using a configuration. The `VaadinWhiteboardConfigurator` collects the base configuration and starts a `ReferenceCollector` instance for a given Vaadin application filter for the Vaadin components. So you only get components, that are applicable for you Vaadin whiteboard. The configurator als launches the `VaadinWhiteboardComponent` with some configuration information.

### WhiteboardApplicationRouteRegistry

This is an extended route registry, that collects all OSGi `ServiceObjects` in addition to the ordinary routes. For that, this registry is also registered as `ServiceObjectRegistry` service.

We will need this route registry and service objects registry later when creating the instance for the routes in the Vaadin Servlet/Service

### VaadinWhiteboardRegistryProcessor

This contains the special `WhiteboardApplicationRouteRegistry` and all `ServletContainerInitializer` that are usually started when installing the Vaadin-Servlet in the package `com.vaadin.flow.server.startup` using *one* static instance of the route registry.

### Whiteboard Component

The `VaadinWhiteboardComponent` needs the dedicated `ReferenceCollector` instance from the configurator and a fresh instance of the `VaadinWhiteboardRegistryProcessor`.

The service objects are come from the `ReferenceCollector` that is connected to the `VaadinWhiteboardComponent`. It uses the `ReferenceCollector` as dispatcher to the `ServiceObjectsRegistry`. So all components go through the `ReferenceCollector` via `VaadinWhiteboardComponent` into the `ServiceObjectsRegistry`. 

When dispatching, the collected OSGi `ServiceReferences` and `ServiceObject` classes are scanned for Vaadin Annotations using the `VaadinWhiteboardRegistryProcessor`. the build up the route registry for the Vaadin application.

The whiteboard component registers a special `WhiteboardVaadinServlet`, that uses a special `WhiteboardVaadinService`, which creates an special `OSGiInstantiator`. Latter creates instance out of the ServiceObjectsRegistry` that is provided to the servlet. 

The `WhiteboardVaadinService`, `WhiteboardVaadinServlet` and `OSGiInstantiator` inherit from the default Vaadin setup and should also be capable to handle ordinary Vaadin stuff.

### Bower mode

The OSGi implementation provided by Vaadin natively, expects the have all bower JavaScript components in the bundle that registers the VaadinServlet, because their look-up goes against the servlet context.

In return the non-OSGi implementation starts a FrontendVaadinServlet, to serve the bower JavaScript resources for all the frontend components.

We bundled all these JavaScript stuff in a separate bundle **org.gecko.vaadin.whiteboard.bower**, that provides the corresponding files under the same pattern, like the FrontendVaadinServlet. We use a special 
OSGi requirement *vaadin.frontend* to bind

## Limitations

This is a very basic initial setup. Limitations are:

* `WhiteboardVaadinServlet` is not yet PROTOTYPE scoped. We needs the correct setup for the service, instantiator for that.
* ~~`OSGiInstantiator` can release service instances, but there is currently no release process implemented. that means that service instances are created  out of `ServiceObjects` but not released. We may need to verify if we can assign the current `VaadinSession` to an instance and then use the `SessionDestroyListener` in Vaadin to release instances for a session.~~
* The `OSGiInstantiator` uses the session instance of the request to check for existing instances of an service. A reload in the browser may force, the same session, but in that case the old instance is thrown away and a new instance is created. If an existing session invalidates, we only get informed via session destroy listener, that then finally releases all service instances of that session instance. This may result in invalid references
* ~~The bower_components are packaged in the bundle that starts the Servlet. In our case the whiteboard bundle. This is suboptimal. We need a way to use e.g. fragments instead.~~
* It only works with bower_components, means with Vaadin 14 in compatibility mode. NPM or webcomponents have to be implemented as well.
* tha latest release of Vaadin is not supported.

## Example Project org.gecko.vaadin.whiteboard.component

An example is in `org.gecko.vaadin.whiteboard.component`. You will find a corresponding configuration there:

```
{
  ":configurator:resource-version": 1,
  
  "VaadinApplication~VaadinApp": 
	{
		"vaadin.application.description": "OSGi Vaadin Application",
		"vaadin.application.name": "VaadinApp",
		"vaadin.application.context": "vaadin"
	}
}
```

You can use the launcher to run the Vaadin Application on port 8080. Please try http://localhost:8080 to see the result.

To get all the dependencies you need just annotate a class with `@RequireVaadinFlow`. To create a service based Vaadin component, annotate the corresponding OSGi Component with `@VaadinComponent`.

You can use the Vaadin annotations like `@Route`, `@Theme` as usual. It might be appropriate to initialize the UI related stuff in the `@Activate` callback of the component.

