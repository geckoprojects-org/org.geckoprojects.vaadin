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

import org.gecko.vaadin.whiteboard.Constants;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.wiring.BundleCapability;

/**
 * Registry interface to check, if there is a bundle with the {@link Constants#VAADIN_CAPABILITY_FRONTEND}
 * available. It will be registered here.
 * 
 * @author Mark Hoffmann
 *
 */
public interface FrontendRegistry {
	
	static class FrontendEntry {
		boolean fragment = false;
		Bundle fragmentHost;
		Bundle bundle;
		String pwaName;
		BundleCapability capability;
		
		public FrontendEntry(BundleCapability cap, Bundle bundle, Bundle fragmentHost) {
			this(cap, bundle, fragmentHost, null);
		}
		public FrontendEntry(BundleCapability cap, Bundle bundle, Bundle fragmentBundle, String pwaName) {
			this.capability = cap;
			this.bundle = bundle;
			this.fragmentHost = fragmentBundle;
			this.fragment = this.fragmentHost != null;
			this.pwaName = pwaName;
		}
		
		/**
		 * @return the capability
		 */
		public BundleCapability getCapability() {
			return capability;
		}
		
		/**
		 * @return the fragment
		 */
		public boolean isFragment() {
			return fragment;
		}
		
		/**
		 * @return the fragmentBundle
		 */
		public Bundle getFragmentHost() {
			return fragmentHost;
		}
		
		/**
		 * @return the bundle
		 */
		public Bundle getBundle() {
			return bundle;
		}
		
		public BundleContext getBundleContext() {
			if (isFragment()) {
				return getFragmentHost().getBundleContext();
			} else {
				return getBundle().getBundleContext();
			}
		}
		
		/**
		 * @return the pwaName
		 */
		public String getPwaName() {
			return pwaName;
		}
	}
	
	/**
	 * Returns <code>true</code>, if there is a frontend registered for the given bundle.
	 * It may be possible the the frontend is packaged within a fragment. In that case the
	 * fragment itself and host is checked
	 * @param bundle the bundle to check for a frontend
	 * @return <code>true</code>, if there is a frontend, otherwise <code>false</code>
	 */
	public boolean hasFrontend(Bundle bundle);
	
	/**
	 * Returns <code>true</code>, if there is a frontend registered for the given bundle and pwaName.
	 * It may be possible the the frontend is packaged within a fragment. In that case the
	 * fragment itself and host is checked
	 * @param bundle the bundle to check for a frontend
	 * @param pwaName the application name
	 * @return <code>true</code>, if there is a frontend, otherwise <code>false</code>
	 */
	public boolean hasFrontend(Bundle bundle, String pwaName);
	
	/**
	 * Returns the {@link FrontendEntry} for the given bundle . If the bundle
	 * is no fragment, it also checks the fragments, because the JS resources can 
	 * be packaged in a fragment.
	 * @param bundle the bundle to get the frontend for
	 * @return the {@link FrontendEntry} or <code>null</code>
	 */
	public FrontendEntry getFrontend(Bundle bundle);
	
	/**
	 * Returns the {@link FrontendEntry} for the given bundle id. If the bundle
	 * is no fragment, it also checks the fragments, because the JS resources can 
	 * be packaged in a fragment.
	 * @param bundle the bundle to get the frontend for
	 * @return the {@link FrontendEntry} or <code>null</code>
	 */
	public FrontendEntry getFrontend(long bundleId);
	
	/**
	 * Returns the {@link FrontendEntry} for the given bundle and pwa name. If the bundle
	 * is no fragment, it also checks the fragments, because the JS resources can be packaged 
	 * in a fragment.
	 * @param bundle the bundle to get the frontend for
	 * @param pwaName the pwa name
	 * @return the {@link FrontendEntry} or <code>null</code>
	 */
	public FrontendEntry getFrontend(Bundle bundle, String pwaName);

}
