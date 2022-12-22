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
package org.gecko.vaadin.whiteboard.registry;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.gecko.vaadin.whiteboard.Constants;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.namespace.HostNamespace;
import org.osgi.framework.namespace.IdentityNamespace;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;

/**
 * A registry that tracks all bundles for a certain capability frontend.
 * It registers the bundle, fragment information in a global white-board global
 * registry, so that every whiteboard instance can get the bundle-context information
 * for the JS frontend data.
 * 
 * @author Mark Hoffmann
 *
 */
@Component(immediate = true)
public class WhiteboardFrontendRegistry implements FrontendRegistry {
	
	private static final Logger logger = Logger.getLogger(WhiteboardFrontendRegistry.class.getName());
	
	class FrontendBundleTracker extends BundleTracker<Bundle> {

		public FrontendBundleTracker(BundleContext context, BundleTrackerCustomizer<Bundle> customizer) {
			super(context, Bundle.ACTIVE | Bundle.RESOLVED, customizer);
		}
		
		/* (non-Javadoc)
		 * @see org.osgi.util.tracker.BundleTracker#addingBundle(org.osgi.framework.Bundle, org.osgi.framework.BundleEvent)
		 */
		@Override
		public Bundle addingBundle(Bundle bundle, BundleEvent event) {
			if ((bundle.getState() & Bundle.ACTIVE) != 0) {
				BundleWiring wiring = bundle.adapt(BundleWiring.class);
				List<BundleCapability> capabilities = wiring.getCapabilities(Constants.VAADIN_CAPABILITY_NAMESPACE);
				if (capabilities != null && !capabilities.isEmpty()) {
					Optional<BundleCapability> capO = capabilities.stream().filter(this::isFrontendCapability).findFirst();
					if (capO.isPresent()) {
						BundleCapability cap = capO.get();
						String name = (String) cap.getAttributes().get("pwa");
						Bundle b = cap.getRevision().getBundle();
						Bundle host = null;
						if (isFragment(cap)) {
							host = getFragmentHost(cap);
						}
						synchronized (frontends) {
							logger.info(String.format("Add Vaadin frontend bundle '%s' and name %s", b.getSymbolicName(), name));
							frontends.put(cap, new FrontendEntry(cap, b, host, name));
						}
					}
				}
			}
			return super.addingBundle(bundle, event);
		}
		
		/* (non-Javadoc)
		 * @see org.osgi.util.tracker.BundleTracker#remove(org.osgi.framework.Bundle)
		 */
		@Override
		public void remove(Bundle bundle) {
			BundleWiring wiring = bundle.adapt(BundleWiring.class);
			List<BundleCapability> capabilities = wiring.getCapabilities(Constants.VAADIN_CAPABILITY_NAMESPACE);
			if (capabilities != null && !capabilities.isEmpty()) {
				Optional<BundleCapability> capO = capabilities.stream().filter(this::isFrontendCapability).findFirst();
				if (capO.isPresent()) {
					BundleCapability cap = capO.get();
					synchronized (frontends) {
						logger.info(String.format("Remove Vaadin frontend bundle '%s'", cap.getRevision().getBundle().getSymbolicName()));
						frontends.remove(cap);
					}
				}
			}
			super.remove(bundle);
		}
		
		private boolean isFrontendCapability(BundleCapability c) {
			return Constants.VAADIN_CAPABILITY_FRONTEND.
					equals(c.getAttributes().get(Constants.VAADIN_CAPABILITY_NAMESPACE));
		}
		
		private boolean isFragment(BundleCapability c) {
			Bundle bundle = c.getRevision().getBundle();
			BundleWiring wiring = bundle.adapt(BundleWiring.class);
			return wiring.getCapabilities(IdentityNamespace.IDENTITY_NAMESPACE)
				.stream()
				.filter((cap)->IdentityNamespace.TYPE_FRAGMENT.equals(cap.getAttributes().get(IdentityNamespace.CAPABILITY_TYPE_ATTRIBUTE)))
				.findFirst().isPresent();
		}
		
		private Bundle getFragmentHost(BundleCapability c) {
			if (isFragment(c)) {
				Bundle bundle = c.getRevision().getBundle();
				BundleWiring wiring = bundle.adapt(BundleWiring.class);
				Optional<BundleWire> hostWire = wiring.getRequiredWires(HostNamespace.HOST_NAMESPACE).stream().findFirst();
				if (hostWire.isPresent()) {
					BundleWiring hw = hostWire.get().getProviderWiring();
					if (hw != null) {
						return hw.getBundle();
					}
				}
			}
			return null;
		}
		
	}
	
	private volatile Map<BundleCapability, FrontendEntry> frontends = new ConcurrentHashMap<>();
	private volatile FrontendBundleTracker frontendTracker;
	
	@Activate
	public void activate(BundleContext context) {
		logger.info("Activate frontend registry");
		frontendTracker = new FrontendBundleTracker(context, null);
		frontendTracker.open();
	}
	
	@Deactivate
	public void deactivate() {
		logger.info("De-activate frontend registry");
		if (frontendTracker != null) {
			frontendTracker.close();
		}
		frontends.clear();
	}
	
	/* (non-Javadoc)
	 * @see org.gecko.vaadin.whiteboard.registry.FrontendRegistry#hasFrontend(org.osgi.framework.Bundle)
	 */
	@Override
	public boolean hasFrontend(Bundle bundle) {
		return getFrontend(bundle) != null;
	}
	
	/* (non-Javadoc)
	 * @see org.gecko.vaadin.whiteboard.registry.FrontendRegistry#hasFrontend(org.osgi.framework.Bundle, java.lang.String)
	 */
	@Override
	public boolean hasFrontend(Bundle bundle, String pwaName) {
		return getFrontend(bundle, pwaName) != null;
	}

	/* (non-Javadoc)
	 * @see org.gecko.vaadin.whiteboard.registry.FrontendRegistry#getFrontend(org.osgi.framework.Bundle)
	 */
	@Override
	public FrontendEntry getFrontend(Bundle bundle) {
		return bundle == null ? null : getFrontend(bundle.getBundleId());
	}
	
	/* (non-Javadoc)
	 * @see org.gecko.vaadin.whiteboard.registry.FrontendRegistry#getFrontend(long)
	 */
	@Override
	public FrontendEntry getFrontend(long bundleId) {
		synchronized (frontends) {
			for (FrontendEntry e : frontends.values()) {
				if (e.isFragment()) {
					if (e.getBundle().getBundleId() == bundleId || e.getFragmentHost().getBundleId() == bundleId) {
						return e;
					}
				} else {
					if (e.getBundle().getBundleId() == bundleId) {
						return e;
					}
				}
			}
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see org.gecko.vaadin.whiteboard.registry.FrontendRegistry#getFrontend(org.osgi.framework.Bundle, java.lang.String)
	 */
	@Override
	public FrontendEntry getFrontend(Bundle bundle, String pwaName) {
		FrontendEntry entry = getFrontend(bundle);
		if (entry != null && entry.getPwaName() != null && entry.getPwaName().equals(pwaName)) {
			return entry;
		}
		return null;
	}

}
