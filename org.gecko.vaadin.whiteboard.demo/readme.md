# Vaadin Whiteboard Demo

Gecko.io Vaadin Whiteboard Demo Application

## Links

* [Documentation](https://gitlab.com/gecko.io/geckoVaadin)
* [Source Code](https://gitlab.com/gecko.io/geckoEMF) (clone with `scm:git:git@gitlab.com:gecko.io/geckoEMF.git`)

## Coordinates

### Maven

```xml
<dependency>
    <groupId>org.geckoprojects.vaadin</groupId>
    <artifactId>org.gecko.vaadin.whiteboard.component</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### OSGi

```
Bundle Symbolic Name: org.gecko.vaadin.whiteboard.component
Version             : 1.0.0.SNAPSHOT
```

### Feature-Coordinate

```
"bundles": [
   {
    "id": "org.geckoprojects.vaadin:org.gecko.vaadin.whiteboard.component:1.0.0-SNAPSHOT"
   }
]
```

## Components

### org.gecko.vaadin.component.service.Greeter - *state = enabled, activation = delayed*

#### Description

#### Services - *scope = singleton*

|Interface name |
|--- |
|org.gecko.vaadin.component.service.GreeterService |

#### Properties

No properties.

#### Configuration - *policy = optional*

##### Pid: `org.gecko.vaadin.component.service.Greeter`

No information available.

#### Reference bindings

No bindings.

#### OSGi-Configurator


```
/*
 * Component: org.gecko.vaadin.component.service.Greeter
 * policy:    optional
 */
"org.gecko.vaadin.component.service.Greeter":{
        //# Component properties
        // none

        //# Reference bindings
        // none

        //# ObjectClassDefinition - Attributes
        // (No PidOcd available.)
}
```

---

### org.gecko.vaadin.component.service.RegisterServlet - *state = enabled, activation = immediate*

#### Description

#### Services

No services.

#### Properties

No properties.

#### Configuration - *policy = optional*

##### Pid: `org.gecko.vaadin.component.service.RegisterServlet`

No information available.

#### Reference bindings

No bindings.

#### OSGi-Configurator


```
/*
 * Component: org.gecko.vaadin.component.service.RegisterServlet
 * policy:    optional
 */
"org.gecko.vaadin.component.service.RegisterServlet":{
        //# Component properties
        // none

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