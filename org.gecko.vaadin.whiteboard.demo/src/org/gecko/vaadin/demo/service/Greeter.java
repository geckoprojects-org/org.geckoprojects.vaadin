package org.gecko.vaadin.demo.service;

import org.osgi.service.component.annotations.Component;

@Component
public class Greeter implements GreeterService {

	@Override
	public String greet(String name) {
		return "Hello " + name;
	}

}
