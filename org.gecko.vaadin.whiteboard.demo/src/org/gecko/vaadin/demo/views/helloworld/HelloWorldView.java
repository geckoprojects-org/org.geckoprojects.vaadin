package org.gecko.vaadin.demo.views.helloworld;


import org.gecko.vaadin.demo.service.GreeterService;
import org.gecko.vaadin.demo.views.main.MainView;
import org.gecko.vaadin.whiteboard.annotations.VaadinComponent;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

@Route(value = "hello", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@PageTitle("Hello World")
@Component(service=HelloWorldView.class, scope = ServiceScope.PROTOTYPE)
@VaadinComponent()
public class HelloWorldView extends HorizontalLayout {

	private static final long serialVersionUID = 1L;
	private TextField name;
    private Button sayHello;
    @Reference
    private GreeterService service;

    @Activate
    public HelloWorldView() {
        addClassName("hello-world-view");
        name = new TextField("Your name");
        sayHello = new Button("Say hello");
        add(name, sayHello);
        setVerticalComponentAlignment(Alignment.END, name, sayHello);
        sayHello.addClickListener(e -> {
        	String greet = service.greet(name.getValue());
            Notification.show(greet);
        });
    }

}
