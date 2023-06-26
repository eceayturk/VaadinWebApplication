/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.application.views.list;

import com.example.application.data.entity.Company;
import com.example.application.data.entity.Contact;
import com.example.application.data.entity.Status;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;
import java.util.List;

/**
 *
 * @author ebrue
 */
public class ContactFormEdit extends FormLayout{
    public Contact contact;
    Binder<Contact> binder = new BeanValidationBinder<>(Contact.class); 
  TextField firstName = new TextField("First name"); 
  TextField lastName = new TextField("Last name");
  EmailField email = new EmailField("Email");
  TextField identityNumber = new TextField("Identity Number");
  TextField gender = new TextField("Gender");
  ComboBox<Status> status = new ComboBox<>("Status");
  ComboBox<Company> company = new ComboBox<>("Company");
 
  
  

  Button save = new Button("Save");
  Button delete = new Button("Delete");
 

  public ContactFormEdit(List<Company> companies, List<Status> statuses) {
    addClassName("contact-form-edit"); 
    binder.bindInstanceFields(this);

    company.setItems(companies);
    company.setItemLabelGenerator(Company::getName);
    status.setItems(statuses);
    status.setItemLabelGenerator(Status::getName);
    

    add(firstName, 
        lastName,
        email,
        identityNumber,
        gender,
        company,
        status,
        createButtonsLayout());
  }
   public void setContact(Contact contact) {
        this.contact = contact; 
        binder.readBean(contact); 
    }

  public HorizontalLayout createButtonsLayout() {
  save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
  delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
  

  save.addClickShortcut(Key.ENTER);


  save.addClickListener(event -> validateAndSave()); 
  delete.addClickListener(event -> fireEvent(new DeleteEvent(this, contact))); 


  binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid())); 
  return new HorizontalLayout(save, delete);
}

public void validateAndSave() {
  try {
    binder.writeBean(contact); 
    fireEvent(new SaveEvent(this, contact)); 
  } catch (ValidationException e) {
    e.printStackTrace();
  }
  }
  
  public static abstract class ContactFormEvent extends ComponentEvent<ContactFormEdit> {
  private Contact contact;

  protected ContactFormEvent(ContactFormEdit source, Contact contact) { 
    super(source, false);
    this.contact = contact;
  }

  public Contact getContact() {
    return contact;
  }
}

public static class SaveEvent extends ContactFormEvent {
  SaveEvent(ContactFormEdit source, Contact contact) {
    super(source, contact);
  }
}

public static class DeleteEvent extends ContactFormEvent {
  DeleteEvent(ContactFormEdit source, Contact contact) {
    super(source, contact);
  }

}

public static class CloseEvent extends ContactFormEvent {
  CloseEvent(ContactFormEdit source) {
    super(source, null);
  }
}

public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
    ComponentEventListener<T> listener) { 
  return getEventBus().addListener(eventType, listener);
}

}
