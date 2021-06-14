# org.gecko.vaadin.generator

## Links

* [Documentation](https://gitlab.com/gecko.io/geckoVaadin)
* [Source Code](https://gitlab.com/gecko.io/geckoEMF) (clone with `scm:git:git@gitlab.com:gecko.io/geckoEMF.git`)

## Coordinates

### Maven

```xml
<dependency>
    <groupId>org.geckoprojects.vaadin</groupId>
    <artifactId>org.gecko.vaadin.generator</artifactId>
    <version>0.0.1</version>
</dependency>
```

### OSGi

```
Bundle Symbolic Name: org.gecko.vaadin.generator
Version             : 0.0.1
```

### Feature-Coordinate

```
"bundles": [
   {
    "id": "org.geckoprojects.vaadin:org.gecko.vaadin.generator:0.0.0"
   }
]
```
## Usage

You can run the Vaadin Generator in any BND project. Preferable you enable the generator in a project, where you Vaadin PWA is located. You can also use a bundle fragment that has the PWA project a its host.

All web- resources are generated into the **META-INF/VAADIN** folder. Thats why you have to package it to the bundle or fragment. Depending on you Vaddin project, it may contain a few MegaBytes of JavaScript. Thats why you may use a fragment instead of the bundle.
  

```
-includeresource: \
	META-INF/VAADIN=META-INF/VAADIN

-pluginpath:${workspace}/org.gecko.vaadin.generator/generated/org.gecko.vaadin.generator.jar

-generate:\
	test.txt;\
	generate=geckoVaadinNPM;\
	output=META-INF/VAADIN
```

You have to define the plugin path, where the generator JAR is located. In this case we use the generator from the project.

The generate section can be copied and modified upon your needs. In this case the **test.txt** is watched for changes. If it has changed, the generator will run. Please refer to the generator documentation a bndtools. 

All the other properties are more static and can always be reused.

The generator will then introspect the **bnd.bnd** file. So the generator will use the **-runbundle** section to generate the frontend stuff. So, you can use the **resolve** mechanism here, placing you requirements and let bndtools resolve the bundles.

If your Vaadin project is *org.gecko.vaadin.test*, you can use the following reruirements:

```
-runrequires: \
	bnd.identity;id='org.gecko.vaadin.whiteboard',\
	bnd.identity;id='org.gecko.vaadin.whiteboard.push',\
	bnd.identity;id='org.gecko.vaadin.test'
```

You should end up with a **-runbundle** section like this:

```
	biz.aQute.bndlib;version='[5.3.0,5.3.1)',\
	com.fasterxml.jackson.core.jackson-annotations;version='[2.12.0,2.12.1)',\
	com.fasterxml.jackson.core.jackson-core;version='[2.12.0,2.12.1)',\
	com.fasterxml.jackson.core.jackson-databind;version='[2.12.0,2.12.1)',\
	com.helger.ph-commons;version='[9.5.3,9.5.4)',\
	com.helger.ph-css;version='[6.2.3,6.2.4)',\
	com.vaadin.external.gentyref;version='[1.2.0,1.2.1)',\
	com.vaadin.external.gwt;version='[2.8.2,2.8.3)',\
	com.vaadin.flow.client;version='[7.0.0,7.0.1)',\
	com.vaadin.flow.component.applayout;version='[20.0.0,20.0.1)',\
	com.vaadin.flow.component.avatar;version='[20.0.0,20.0.1)',\
	com.vaadin.flow.component.button;version='[20.0.0,20.0.1)',\
	com.vaadin.flow.component.notification;version='[20.0.0,20.0.1)',\
	com.vaadin.flow.component.orderedlayout;version='[20.0.0,20.0.1)',\
	com.vaadin.flow.component.tabs;version='[20.0.0,20.0.1)',\
	com.vaadin.flow.component.textfield;version='[20.0.0,20.0.1)',\
	com.vaadin.flow.data;version='[7.0.0,7.0.1)',\
	com.vaadin.flow.html.components;version='[7.0.0,7.0.1)',\
	com.vaadin.flow.push;version='[7.0.0,7.0.1)',\
	com.vaadin.flow.server;version='[7.0.0,7.0.1)',\
	com.vaadin.flow.theme.lumo;version='[7.0.0,7.0.1)',\
	com.vaadin.flow.theme.material;version='[7.0.0,7.0.1)',\
	jakarta.annotation-api;version='[1.3.5,1.3.6)',\
	net.bytebuddy.byte-buddy;version='[1.10.18,1.10.19)',\
	org.apache.aries.spifly.dynamic.bundle;version='[1.3.2,1.3.3)',\
	org.apache.commons.commons-compress;version='[1.20.0,1.20.1)',\
	org.apache.commons.commons-fileupload;version='[1.4.0,1.4.1)',\
	org.apache.commons.io;version='[2.5.0,2.5.1)',\
	org.apache.commons.logging;version='[1.2.0,1.2.1)',\
	org.apache.felix.configadmin;version='[1.9.18,1.9.19)',\
	org.apache.felix.configurator;version='[1.0.8,1.0.9)',\
	org.apache.felix.http.bridge;version='[4.1.2,4.1.3)',\
	org.apache.felix.http.servlet-api;version='[1.1.2,1.1.3)',\
	org.apache.felix.scr;version='[2.1.24,2.1.25)',\
	org.gecko.vaadin.whiteboard;version=snapshot,\
	org.gecko.vaadin.whiteboard.test;version=snapshot,\
	org.gecko.vaadin.whiteboard.push;version=snapshot,\
	org.jsoup;version='[1.12.1,1.12.2)',\
	org.objectweb.asm;version='[9.0.0,9.0.1)',\
	org.objectweb.asm.commons;version='[9.0.0,9.0.1)',\
	org.objectweb.asm.tree;version='[9.0.0,9.0.1)',\
	org.objectweb.asm.tree.analysis;version='[9.0.0,9.0.1)',\
	org.objectweb.asm.util;version='[9.0.0,9.0.1)',\
	org.osgi.util.pushstream;version='[1.0.0,1.0.1)',\
	slf4j.api;version='[1.7.25,1.7.26)'
```

 

## Components

### com.vaadin.flow.server.startup.DefaultApplicationConfigurationFactory - *state = enabled, activation = delayed*

#### Description

#### Services - *scope = singleton*

|Interface name |
|--- |
|com.vaadin.flow.server.startup.ApplicationConfigurationFactory |

#### Properties

|Name |Type |Value |
|--- |--- |--- |
|service.ranking |Integer |-2147483648 |

#### Configuration - *policy = optional*

##### Pid: `com.vaadin.flow.server.startup.DefaultApplicationConfigurationFactory`

No information available.

#### Reference bindings

No bindings.

#### OSGi-Configurator


```
/*
 * Component: com.vaadin.flow.server.startup.DefaultApplicationConfigurationFactory
 * policy:    optional
 */
"com.vaadin.flow.server.startup.DefaultApplicationConfigurationFactory":{
        //# Component properties
        /*
         * Type = Integer
         * Default = -2147483648
         */
         // "service.ranking": null,


        //# Reference bindings
        // none

        //# ObjectClassDefinition - Attributes
        // (No PidOcd available.)
}
```

## Developers

* **Juergen Albert** (jalbert) / [j.albert@data-in-motion.biz](mailto:j.albert@data-in-motion.biz) @ [Data In Motion](https://www.datainmotion.de) - *architect*, *developer*
* **Mark Hoffmann** (mhoffmann) / [m.hoffmann@data-in-motion.biz](mailto:m.hoffmann@data-in-motion.biz) @ [Data In Motion](https://www.datainmotion.de) - *developer*, *architect*

## Licenses

**Eclipse Public License 1.0**

## Copyright

Data In Motion Consuling GmbH - All rights reserved

---
Data In Motion Consuling GmbH - [info@data-in-motion.biz](mailto:info@data-in-motion.biz)