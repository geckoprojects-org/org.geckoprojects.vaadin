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
	@Requirement(namespace = IdentityNamespace.IDENTITY_NAMESPACE, filter = "(osgi.identity=com.vaadin.flow.push)"),
	@Requirement(namespace = IdentityNamespace.IDENTITY_NAMESPACE, filter = "(osgi.identity=com.vaadin.flow.client)"),
	@Requirement(namespace = IdentityNamespace.IDENTITY_NAMESPACE, filter = "(osgi.identity=com.vaadin.flow.server)"),
	@Requirement(namespace = IdentityNamespace.IDENTITY_NAMESPACE, filter = "(osgi.identity=com.vaadin.flow.html.components)"),
	@Requirement(namespace = IdentityNamespace.IDENTITY_NAMESPACE, filter = "(osgi.identity=com.vaadin.flow.component.vaadinmaterialtheme)"),
	@Requirement(namespace = IdentityNamespace.IDENTITY_NAMESPACE, filter = "(osgi.identity=com.vaadin.flow.component.vaadinlumotheme)"),
	@Requirement(namespace = Constants.VAADIN_CAPABILITY_NAMESPACE, filter = "(vaadin.osgi=" + Constants.VAADIN_CAPABILITY_WHITEBOARD + ")")
})
public @interface RequireVaadinFlow {

}
