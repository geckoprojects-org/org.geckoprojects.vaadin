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
package org.gecko.vaadin.demo.views.person;

import java.time.LocalDate;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.gecko.emf.osgi.example.model.basic.BasicPackage;
import org.gecko.emf.osgi.example.model.basic.Contact;
import org.gecko.emf.osgi.example.model.basic.ContactContextType;
import org.gecko.emf.osgi.example.model.basic.ContactType;
import org.gecko.emf.osgi.example.model.basic.Person;
import org.gecko.vaadin.demo.views.main.MainView;
import org.gecko.vaadin.emf.databinding.api.EMFBinder;
import org.gecko.vaadin.emf.databinding.api.EMFBinderFactory;
import org.gecko.vaadin.emf.databinding.api.EMFListBinder;
import org.gecko.vaadin.emf.databinding.api.EMFListBinderFactory;
import org.gecko.vaadin.whiteboard.annotations.VaadinComponent;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "form", layout = MainView.class)
@PageTitle("Person Form")
@Component(service=PersonFormView.class, scope = ServiceScope.PROTOTYPE)
@VaadinComponent()
public class PersonFormView extends VerticalLayout {

    /** serialVersionUID */
	private static final long serialVersionUID = 4235478954209759554L;
	private TextField firstName;
    private TextField lastName;
    private CustomEmailField emailField;
    private DatePicker dateOfBirth;
    private PhoneNumberField phone;
    private TextField occupation;

    private Button cancel;
    private Button save;
    private EMFBinder<EObject> personBinder;
    private EMFListBinder<Contact> contactBinder;
   
	@Reference
	private EMFBinderFactory binderFactory;
	
	@Reference
	private EMFListBinderFactory binderListFactory;
	
	@Reference
	private BasicPackage personPackage;

	@Activate
    public void activate() {
        addClassName("person-form-view");
        firstName = new TextField("First name");
        lastName = new TextField("Last name");
        emailField = new CustomEmailField("Email address");
        dateOfBirth = new DatePicker("Birthday", LocalDate.now());
        phone = new PhoneNumberField("Phone number");
        occupation = new TextField("Occupation");
        cancel = new Button("Cancel");
        save = new Button("Save");

        add(createTitle());
        add(createFormLayout());
        add(createButtonLayout());

        final Person person = personPackage.getBasicFactory().createPerson();
        person.setFirstName("Emil");
        person.setLastName("Tester");
        EClass pClass = personPackage.getPerson();
		personBinder = binderFactory
				.createBinder(pClass)
				.bindRequired(firstName, personPackage.getPerson_FirstName(), "This field is required")
				.bind(lastName, personPackage.getPerson_LastName())
				.bean(person);
		personBinder.addStatusChangeListener(event -> {
			  boolean isValid = event.getBinder().isValid();
			  save.setEnabled(isValid);
			});
		personBinder.validate();
		
		contactBinder = binderListFactory.createListBinder(personPackage.getContact());
		Contact c1 = personPackage.getBasicFactory().createContact();
		c1.setType(ContactType.EMAIL);
		EMFBinder<Contact> c1Binder = contactBinder.addEMFBinder();
		c1Binder.bind(emailField, personPackage.getContact_Value())
		.bind(emailField.getContextComboBox(), personPackage.getContact_Context())
		.bean(c1);
		
		Contact c2 = personPackage.getBasicFactory().createContact();
		c2.setType(ContactType.PHONE);
		EMFBinder<Contact> c2Binder = contactBinder.addEMFBinder();
		c2Binder.bind(phone, personPackage.getContact_Value())
		.bind(phone.getContextComboBox(), personPackage.getContact_Context())
		.bean(c2);
		
		contactBinder.bind(person.getContact());
        cancel.addClickListener(e -> clearForm());
        save.addClickListener(e -> {
            personBinder.setBean(person);
            Notification.show(pClass.getName() + " details stored.");
            clearForm();
        });
    }

    private void clearForm() {
    	Person newPerson = personPackage.getBasicFactory().createPerson();
    	personBinder.setBean(newPerson);
    	contactBinder.removeEMFBinders();
    	
    	Contact c1 = personPackage.getBasicFactory().createContact();
		c1.setType(ContactType.EMAIL);
		EMFBinder<Contact> c1Binder = contactBinder.addEMFBinder();
		c1Binder.bind(emailField, personPackage.getContact_Value())
		.bind(emailField.getContextComboBox(), personPackage.getContact_Context())
		.bean(c1);
		
		Contact c2 = personPackage.getBasicFactory().createContact();
		c2.setType(ContactType.PHONE);
		EMFBinder<Contact> c2Binder = contactBinder.addEMFBinder();
		c2Binder.bind(phone, personPackage.getContact_Value())
		.bind(phone.getContextComboBox(), personPackage.getContact_Context())
		.bean(c2);
		
		contactBinder.bind(newPerson.getContact());    	
		
		occupation.setValue("");
		dateOfBirth.setValue(LocalDate.now());
    }

    private com.vaadin.flow.component.Component createTitle() {
        return new H3("Personal information");
    }

    private com.vaadin.flow.component.Component createFormLayout() {
        FormLayout formLayout = new FormLayout();       
        formLayout.add(firstName, lastName, dateOfBirth, phone, emailField, occupation);
        return formLayout;
    }

    private com.vaadin.flow.component.Component createButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");
        buttonLayout.add(save);
        buttonLayout.add(cancel);
        return buttonLayout;
    }
    
    

    private static class PhoneNumberField extends CustomField<String> {
        /** serialVersionUID */
		private static final long serialVersionUID = 93663560010609290L;
		private ComboBox<ContactContextType> context = new ComboBox<>();
		private ComboBox<String> countryCode = new ComboBox<>();
        private TextField number = new TextField();

        public PhoneNumberField(String label) {
            setLabel(label);
            context.setWidth("120px");
            context.setItems(ContactContextType.VALUES);
            
            countryCode.setWidth("120px");
            countryCode.setPlaceholder("Country");
            countryCode.setPattern("\\+\\d*");
            countryCode.setAllowedCharPattern("[0-9+]");
            countryCode.setItems("+354", "+91", "+62", "+98", "+964", "+353", "+44", "+972", "+39", "+225");
            countryCode.addCustomValueSetListener(e -> countryCode.setValue(countryCode.getValue()));
            number.setPattern("\\d*");
            number.setAllowedCharPattern("[0-9]");
            HorizontalLayout layout = new HorizontalLayout(context, countryCode, number);
            layout.setFlexGrow(1.0, number);
            add(layout);
        }
        
        public ComboBox<ContactContextType> getContextComboBox() {
        	return context;
        }

        @Override
        protected String generateModelValue() {
            if (countryCode.getValue() != null && number.getValue() != null) {
                String s = countryCode.getValue() + " " + number.getValue();
                return s;
            }
            return "";
        }

        @Override
        protected void setPresentationValue(String phoneNumber) {
            String[] parts = phoneNumber != null ? phoneNumber.split(" ", 2) : new String[0];
            if (parts.length == 1) {
                countryCode.clear();
                number.setValue(parts[0]);
            } else if (parts.length == 2) {
                countryCode.setValue(parts[0]);
                number.setValue(parts[1]);
            } else {
                countryCode.clear();
                number.clear();
            }
        }
    }
    
    private static class CustomEmailField extends CustomField<String> {
        /** serialVersionUID */
		private static final long serialVersionUID = 93663560010609290L;
		private ComboBox<ContactContextType> context = new ComboBox<>();
        private EmailField email = new EmailField();

        public CustomEmailField(String label) {
            setLabel(label);
            context.setWidth("120px");
            context.setItems(ContactContextType.VALUES); 
            email.setErrorMessage("Please enter a valid email address");
            HorizontalLayout layout = new HorizontalLayout(context, email);
            layout.setFlexGrow(1.0, email);
            add(layout);
        }
        
        public ComboBox<ContactContextType> getContextComboBox() {
        	return context;
        }

        @Override
        protected String generateModelValue() {
            if (email.getValue() != null) {
                return email.getValue();
            }
            return "";
        }

        @Override
        protected void setPresentationValue(String emailValue) {
        	if(emailValue == null) {
        		email.clear();
        		email.setInvalid(false);
        	} else {
        		email.setValue(emailValue);
        	}
        }
    }

}
