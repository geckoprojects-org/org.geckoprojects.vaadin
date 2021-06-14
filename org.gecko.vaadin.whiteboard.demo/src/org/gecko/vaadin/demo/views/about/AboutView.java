package org.gecko.vaadin.demo.views.about;


import org.gecko.vaadin.demo.views.main.MainView;
import org.gecko.vaadin.whiteboard.annotations.VaadinComponent;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "about", layout = MainView.class)
@PageTitle("About")
@Component(service=AboutView.class, scope = ServiceScope.PROTOTYPE)
@VaadinComponent()
public class AboutView extends Div {

	private static final long serialVersionUID = 1L;
	
	@Activate
    public AboutView() {
        addClassName("about-view");
        add(new Text("Content placeholder"));
    }

}
