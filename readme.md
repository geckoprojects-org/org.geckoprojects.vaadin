[![CI Build](https://github.com/geckoprojects-org/org.geckoprojects.vaadin/actions/workflows/build.yml/badge.svg)](https://github.com/geckoprojects-org/org.geckoprojects.vaadin/actions/workflows/build.yml)[![License](https://github.com/geckoprojects-org/org.geckoprojects.vaadin/actions/workflows/license.yml/badge.svg)](https://github.com/geckoprojects-org/org.geckoprojects.vaadin/actions/workflows/license.yml )[![Sonar](https://github.com/geckoprojects-org/org.geckoprojects.vaadin/actions/workflows/sonar.yml/badge.svg)](https://github.com/geckoprojects-org/org.geckoprojects.vaadin/actions/workflows/sonar.yml )[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=geckoprojects-org_org.geckoprojects.vaadin&metric=bugs)](https://sonarcloud.io/dashboard?id=geckoprojects-org_org.geckoprojects.vaadin)[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=geckoprojects-org_org.geckoprojects.vaadin&metric=code_smells)](https://sonarcloud.io/dashboard?id=geckoprojects-org_org.geckoprojects.vaadin)[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=geckoprojects-org_org.geckoprojects.vaadin&metric=coverage)](https://sonarcloud.io/dashboard?id=geckoprojects-org_org.geckoprojects.vaadin)[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=geckoprojects-org_org.geckoprojects.vaadin&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=geckoprojects-org_org.geckoprojects.vaadin)[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=geckoprojects-org_org.geckoprojects.vaadin&metric=alert_status)](https://sonarcloud.io/dashboard?id=geckoprojects-org_org.geckoprojects.vaadin)[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=geckoprojects-org_org.geckoprojects.vaadin&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=geckoprojects-org_org.geckoprojects.vaadin)[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=geckoprojects-org_org.geckoprojects.vaadin&metric=security_rating)](https://sonarcloud.io/dashboard?id=geckoprojects-org_org.geckoprojects.vaadin)[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=geckoprojects-org_org.geckoprojects.vaadin&metric=sqale_index)](https://sonarcloud.io/dashboard?id=geckoprojects-org_org.geckoprojects.vaadin)[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=geckoprojects-org_org.geckoprojects.vaadin&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=geckoprojects-org_org.geckoprojects.vaadin)

# Vaadin OSGi Whiteboard
This project uses [bndtools](https://bndtools.org/) to build the OSGi compliant artifacts. It is the same tool that Vaadin uses internally to make their JAR's OSGi compatible.

We use the gradle support for bndtools.

## Demo Application

There is a demo application that can be tested. It illustrates the use of Vaadin Components in OSGi together with the Whiteboard:

**org.gecko.vaadin.whiteboard.demo**

There is a bundle fragment, for the frontend:

**org.gecko.vaadin.whiteboard.demo.frontend**

It contains the configuration for the Vaadin frontend generator. It is contained in the **bnd.bnd** file. The generator is executed at each full build or on any modification in the **test.txt** file.

we want to improve this process to run it as a bnd-exporter instead of an generator. this is also because the generation process takes a while.

## Try it out

You need to build the whole project using the gradle wrapper:

`./gradlew clean build`

The Vaadin frontend is also generated within this step.

If you want to run the application using gradle, just call after the build:

`./gradlew run.launch_whiteboard_demo_osgi `

Then goto [http://localhost:8080/twenty](http://localhost:8080/twenty) in your browser and you will see the demo application.

To create an executable jar in gradle call:

`./gradlew export.launch_whiteboard_demo_osgi`

The executable jar can be found in **org.gecko.vaadin.whiteboard.demo/generated/distributions/executable**. You can run the jar as usual:

`java -jar launch_whiteboard_demo_osgi.jar`

As a bndtools user, you can run the **launch_whiteboard_demo_osgi.bndrun** in the  **org.gecko.vaadin.whiteboard.demo** project.

## Artifacts

Currently the SNAPSHOT artifacts are available here: 

[https://oss.sonatype.org/content/repositories/snapshots/](https://oss.sonatype.org/content/repositories/snapshots/)

The generator needs this dependency:
```
<dependency>
  <groupId>org.geckoprojects.vaadin</groupId>
  <artifactId>org.gecko.vaadin.generator</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</dependency>
```

To use the whiteboard you will need:

```
<dependency>
  <groupId>org.geckoprojects.vaadin</groupId>
  <artifactId>org.gecko.vaadin.whiteboard</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</dependency>
<dependency>
  <groupId>org.geckoprojects.vaadin</groupId>
  <artifactId>org.gecko.vaadin.whiteboard.push</artifactId>
  <version>7.0.0-SNAPSHOT</version>
</dependency>
```

## Links

* [Documentation](https://github.com/geckoprojects-org/org.geckoprojects.vaadin)
* [Source Code](https://github.com/geckoprojects-org/org.geckoprojects.vaadin) (clone with `scm:git:git@github.com:geckoprojects-org/org.geckoprojects.vaadin.git`)
* [bndtools](https://bndtools.org/)

## Developers

* **Mark Hoffmann** (mhoffmann) / [m.hoffmann@data-in-motion.biz](mailto:m.hoffmann@data-in-motion.biz) @ [Data In Motion](https://www.datainmotion.de) - *developer*, *architect*
* **Juergen Albert** (jalbert) / [j.albert@data-in-motion.biz](mailto:j.albert@data-in-motion.biz) @ [Data In Motion](https://www.datainmotion.de) - *architect*, *developer*


## Licenses

**Eclipse Public License 2.0**

## Copyright

Data In Motion Consuling GmbH - All rights reserved

---
Data In Motion Consuling GmbH - [info@data-in-motion.biz](mailto:info@data-in-motion.biz)
