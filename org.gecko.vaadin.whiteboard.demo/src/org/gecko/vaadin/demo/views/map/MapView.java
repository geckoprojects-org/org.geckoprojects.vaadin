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
package org.gecko.vaadin.demo.views.map;

import org.gecko.vaadin.demo.views.main.MainView;
import org.gecko.vaadin.whiteboard.annotations.VaadinComponent;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "map", layout = MainView.class)
@PageTitle("Map")
@Component(service=MapView.class, scope = ServiceScope.PROTOTYPE)
@VaadinComponent()
public class MapView extends VerticalLayout {

	private static final long serialVersionUID = 1L;
	private LeafletMap map = new LeafletMap();

    @Activate
    public void activate() {
        setSizeFull();
        setPadding(false);
        map.setSizeFull();
        map.setView(55.0, 10.0, 6);
        add(map);
    }
}
