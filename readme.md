[![CI Build](https://github.com/geckoprojects-org/org.gecko.vaadin/actions/workflows/build.yml/badge.svg)](https://github.com/geckoprojects-org/org.gecko.vaadin/actions/workflows/build.yml)[![License](https://github.com/geckoprojects-org/org.gecko.vaadin/actions/workflows/license.yml/badge.svg)](https://github.com/geckoprojects-org/org.gecko.vaadin/actions/workflows/license.yml )[![Sonar](https://github.com/geckoprojects-org/org.gecko.vaadin/actions/workflows/sonar.yml/badge.svg)](https://github.com/geckoprojects-org/org.gecko.vaadin/actions/workflows/sonar.yml )[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=geckoprojects-org_org.gecko.vaadin&metric=bugs)](https://sonarcloud.io/dashboard?id=geckoprojects-org_org.gecko.vaadin)[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=geckoprojects-org_org.gecko.vaadin&metric=code_smells)](https://sonarcloud.io/dashboard?id=geckoprojects-org_org.gecko.vaadin)[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=geckoprojects-org_org.gecko.vaadin&metric=coverage)](https://sonarcloud.io/dashboard?id=geckoprojects-org_org.gecko.vaadin)[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=geckoprojects-org_org.gecko.vaadin&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=geckoprojects-org_org.gecko.vaadin)[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=geckoprojects-org_org.gecko.vaadin&metric=alert_status)](https://sonarcloud.io/dashboard?id=geckoprojects-org_org.gecko.vaadin)[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=geckoprojects-org_org.gecko.vaadin&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=geckoprojects-org_org.gecko.vaadin)[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=geckoprojects-org_org.gecko.vaadin&metric=security_rating)](https://sonarcloud.io/dashboard?id=geckoprojects-org_org.gecko.vaadin)[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=geckoprojects-org_org.gecko.vaadin&metric=sqale_index)](https://sonarcloud.io/dashboard?id=geckoprojects-org_org.gecko.vaadin)[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=geckoprojects-org_org.gecko.vaadin&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=geckoprojects-org_org.gecko.vaadin)

# Vaadin OSGi Whiteboard

This project contains an OSGi whiteboard pattern based approach to use OSGi Components as Vaadin UI components.

This version is for Vaadin 23.

Please look at the demo projects. 

This setup currently requires bndtools 6.4.0 or larger. 

To build the project call `gradlew clean build`

With that also the frontend components are build for the demo fragment.

You can find the artifacts on Maven Central or/and the Snapshot Repositories:

```
<dependency>
  <groupId>org.geckoprojects.vaadin</groupId>
  <artifactId>org.gecko.vaadin.whiteboard</artifactId>
  <version>2.0.0-SNAPSHOT</version>
</dependency>
<dependency>
  <groupId>org.geckoprojects.vaadin</groupId>
  <artifactId>org.gecko.vaadin.whiteboard.api</artifactId>
  <version>2.0.0-SNAPSHOT</version>
</dependency>
<dependency>
  <groupId>org.geckoprojects.vaadin</groupId>
  <artifactId>org.gecko.vaadin.generator</artifactId>
  <version>2.0.0-SNAPSHOT</version>
</dependency>
```



## The Demo Project

There are two projects for the demo.

One contains the Vaadin UI programming.

The second *org.gecko.vaadin.whiteboard.demo.frontend* is an OSGi fragment with the generated JavaScript Frontend. This setup is not mendatory, but we use the possibility of bndtools to resolve dependencies, to get our bundles we need. This is our base for starting the frontend build. Additionally all mandatory bundles are automatically added.

Starting requirements:

```
-runrequires: \
	bnd.identity;id='org.gecko.vaadin.whiteboard.api',\
	bnd.identity;id='org.gecko.vaadin.whiteboard.demo'
```

 After resolving the *org.gecko.vaadin.whiteboard.demo.frontend/bnd.bnd* file we get the *-runbundles* section:

```
-runbundles: \
	com.vaadin.external.gentyref;version='[1.2.0,1.2.1)',\
	com.vaadin.external.gwt;version='[2.8.2,2.8.3)',\
	jakarta.annotation-api;version='[1.3.5,1.3.6)',\
	org.apache.commons.commons-fileupload;version='[1.4.0,1.4.1)',\
	org.gecko.vaadin.whiteboard;version=snapshot,\
	org.gecko.vaadin.whiteboard.api;version=snapshot,\
	org.gecko.vaadin.whiteboard.demo;version=snapshot,\
	org.osgi.util.function;version='[1.1.0,1.1.1)',\
	net.bytebuddy.byte-buddy;version='[1.12.19,1.12.20)',\
	org.apache.aries.spifly.dynamic.framework.extension;version='[1.3.5,1.3.6)',\
	org.apache.commons.commons-compress;version='[1.22.0,1.22.1)',\
	org.apache.felix.http.jetty;version='[4.2.2,4.2.3)',\
	org.apache.felix.http.servlet-api;version='[1.2.0,1.2.1)',\
	org.apache.felix.scr;version='[2.2.4,2.2.5)',\
	org.jsoup;version='[1.15.3,1.15.4)',\
	org.osgi.service.cm;version='[1.6.0,1.6.1)',\
	org.osgi.service.component;version='[1.5.0,1.5.1)',\
	org.osgi.service.http.whiteboard;version='[1.1.0,1.1.1)',\
	org.osgi.util.converter;version='[1.0.1,1.0.2)',\
	org.osgi.util.promise;version='[1.2.0,1.2.1)',\
	org.osgi.util.pushstream;version='[1.0.2,1.0.3)',\
	slf4j.api;version='[1.7.36,1.7.37)',\
	slf4j.simple;version='[1.7.36,1.7.37)',\
	org.apache.felix.cm.json;version='[1.0.8,1.0.9)',\
	org.apache.felix.configurator;version='[1.0.16,1.0.17)',\
	org.apache.sling.commons.johnzon;version='[1.2.14,1.2.15)',\
	com.fasterxml.jackson.core.jackson-annotations;version='[2.14.1,2.14.2)',\
	com.fasterxml.jackson.core.jackson-core;version='[2.14.1,2.14.2)',\
	com.fasterxml.jackson.core.jackson-databind;version='[2.14.1,2.14.2)',\
	com.helger.commons.ph-commons;version='[10.2.2,10.2.3)',\
	com.helger.ph-css;version='[6.5.0,6.5.1)',\
	com.nimbusds.nimbus-jose-jwt;version='[9.23.0,9.23.1)',\
	com.vaadin.flow.client;version='[23.3.0,23.3.1)',\
	com.vaadin.flow.component.applayout;version='[23.3.0,23.3.1)',\
	com.vaadin.flow.component.avatar;version='[23.3.0,23.3.1)',\
	com.vaadin.flow.component.button;version='[23.3.0,23.3.1)',\
	com.vaadin.flow.component.icon;version='[23.3.0,23.3.1)',\
	com.vaadin.flow.component.notification;version='[23.3.0,23.3.1)',\
	com.vaadin.flow.component.orderedlayout;version='[23.3.0,23.3.1)',\
	com.vaadin.flow.component.shared;version='[23.3.0,23.3.1)',\
	com.vaadin.flow.component.tabs;version='[23.3.0,23.3.1)',\
	com.vaadin.flow.component.textfield;version='[23.3.0,23.3.1)',\
	com.vaadin.flow.data;version='[23.3.0,23.3.1)',\
	com.vaadin.flow.html.components;version='[23.3.0,23.3.1)',\
	com.vaadin.flow.push;version='[23.3.0,23.3.1)',\
	com.vaadin.flow.server;version='[23.3.0,23.3.1)',\
	com.vaadin.flow.theme.lumo;version='[23.3.0,23.3.1)',\
	com.vaadin.flow.theme.material;version='[23.3.0,23.3.1)',\
	com.vaadin.license-checker;version='[1.11.2,1.11.3)',\
	com.vaadin.open;version='[8.4.0,8.4.1)',\
	jakarta.validation.jakarta.validation-api;version='[2.0.2,2.0.3)',\
	jcp.annotations;version=snapshot,\
	org.apache.commons.commons-io;version='[2.11.0,2.11.1)',\
	com.vaadin.flow.data.renderer;version='[23.3.0,23.3.1)'
```

This is now our base for the frontend build that is executed by

```
-generate:\
	test.txt;\
	generate=geckoVaadinNPM;\
	output=META-INF/VAADIN;\
	cleanup=ALL
```

Bndtools does the rest for us.

You can test the application by launching the *launch_whiteboard_demo_osgi.bndrun* in the project *org.gecko.vaadin.whiteboard.demo* .

It is no magic, it uses the OSGi Http Whiteboard to be run an a corresponding configuration that is located in the projects *config* folder

The application runs under http://localhost:8080/twentythree/ 



## Links

* [Documentation](https://github.com/geckoprojects-org/org.gecko.vaadin)
* [Source Code](https://github.com/geckoprojects-org/org.gecko.vaadin) (clone with `scm:git:git@github.com:geckoprojects-org/org.gecko.vaadin.git`)


## Developers

* **Mark Hoffmann** (m.hoffmann) / [m.hoffmann@datainmotion.com](mailto:m.hoffmann@datainmotion.comm) @ [Data In Motion](https://www.datainmotion.de) - *developer*, *architect*
* **Ilenia Salvadori** (i.salvadori) / [i.salvadori@datainmotion.co](mailto:i.salvadori@datainmotion.com) @ [Data In Motion](https://www.datainmotion.de) - *developer*

## Licenses

**Eclipse Public License 2.0**

## Copyright

Data In Motion Consuling GmbH - All rights reserved

---
Data In Motion Consuling GmbH - [info@data-in-motion.biz](mailto:info@data-in-motion.biz)
