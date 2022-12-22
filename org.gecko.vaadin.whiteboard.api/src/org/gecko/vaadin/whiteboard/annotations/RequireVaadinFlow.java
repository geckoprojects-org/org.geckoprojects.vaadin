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
package org.gecko.vaadin.whiteboard.annotations;

import org.gecko.vaadin.whiteboard.Constants;
import org.osgi.annotation.bundle.Requirement;
import org.osgi.annotation.bundle.Requirements;
import org.osgi.framework.namespace.IdentityNamespace;

/**
 * Requirement annotation that hold all requirements for a working setup.
 * This annotation is used by the {@link VaadinComponent} annotation. So there is no real
 * need to use this annotation in users code directly
 * @author Mark Hoffmann
 *
 */
@Requirements({
	@Requirement(namespace = IdentityNamespace.IDENTITY_NAMESPACE, filter = "(osgi.identity=com.vaadin.flow.data)"),
	@Requirement(namespace = IdentityNamespace.IDENTITY_NAMESPACE, filter = "(osgi.identity=com.vaadin.flow.data.renderer)"),
	@Requirement(namespace = IdentityNamespace.IDENTITY_NAMESPACE, filter = "(osgi.identity=com.vaadin.flow.push)"),
	@Requirement(namespace = IdentityNamespace.IDENTITY_NAMESPACE, filter = "(osgi.identity=com.vaadin.flow.client)"),
	@Requirement(namespace = IdentityNamespace.IDENTITY_NAMESPACE, filter = "(osgi.identity=com.vaadin.flow.server)"),
	@Requirement(namespace = IdentityNamespace.IDENTITY_NAMESPACE, filter = "(osgi.identity=com.vaadin.flow.html.components)"),
	@Requirement(namespace = IdentityNamespace.IDENTITY_NAMESPACE, filter = "(osgi.identity=com.vaadin.flow.theme.material)"),
	@Requirement(namespace = IdentityNamespace.IDENTITY_NAMESPACE, filter = "(osgi.identity=com.vaadin.flow.theme.lumo)"),
	@Requirement(namespace = Constants.VAADIN_CAPABILITY_NAMESPACE, filter = "(vaadin.osgi=" + Constants.VAADIN_CAPABILITY_WHITEBOARD + ")")
})
public @interface RequireVaadinFlow {

}
