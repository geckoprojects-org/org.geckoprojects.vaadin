//package org.gecko.vaadin.demo.service;
//
//import java.util.Date;
//
//import org.gecko.vaadin.demo.model.person.Person;
//import org.gecko.vaadin.demo.model.person.PersonFactory;
//import org.osgi.service.component.annotations.Component;
//
//@Component
//public class PersonServiceImpl implements PersonService {
//	
//	@Override
//	public Person createPerson(String name) {
//		String[] splitted = name.split("\\ ");
//		Person p = PersonFactory.eINSTANCE.createPerson();
//		if (splitted.length >= 2) {
//			p.setFirstName(splitted[0]);
//			p.setLastName(splitted[1]);
//			p.setId(name);
//		} else {
//			p.setFirstName(name);
//			p.setId(name);
//		}
//		return p;
//	}
//
//	@Override
//	public Person createPerson(String firstName, String lastName) {
//		Person p = PersonFactory.eINSTANCE.createPerson();
//		p.setFirstName(firstName);
//		p.setLastName(lastName);
//		p.setBirthDate(new Date());
//		p.setId(firstName + " " + lastName);
//		return p;
//	}
//	
//	@Override
//	public Person updatePerson(Person person) {
//		synchronized (person) {
//			String lastName = person.getLastName();
//			lastName = lastName == null ? "von Goethe" : "von " + lastName;
//			person.setLastName(lastName);
//		}
//		return person;
//	}
//
//}
