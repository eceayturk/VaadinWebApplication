package com.example.application.views.list;



import com.example.application.data.entity.Contact;
import com.example.application.data.service.CrmService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;

@Route(value="", layout = MainLayout.class)
@PageTitle("Contacts | INFO SYSTEM")
public class ListView extends VerticalLayout {
    Grid<Contact> grid = new Grid<>(Contact.class);
    TextField filterText = new TextField();
    ContactForm form;
    ContactFormEdit dia;
    
    
    
    CrmService service;
    Dialog dialog;
    Button save = new Button("Save");
    Button delete = new Button("Delete");
    
    
    Binder<Contact> binder = new BeanValidationBinder<>(Contact.class); 
     
    
  

    public ListView(CrmService service) {
        this.service = service;
        addClassName("list-view");
        setSizeFull();
        configureGrid();
        configureForm();
        configureDialog();
        

        add(getToolbar(), getContent());
        form.setVisible(false);
        updateList();
       
    }

    private HorizontalLayout getContent() {
        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.addClassNames("content");
        content.setSizeFull();
        return content;
    }

    private void configureForm() {
    form = new ContactForm(service.findAllCompanies(), service.findAllStatuses());
    form.setWidth("25em");
    form.addListener(ContactForm.SaveEvent.class, this::saveContact);
    form.addListener(ContactForm.DeleteEvent.class, this::deleteContact); 
  
    form.addListener(ContactForm.CloseEvent.class, e -> closeEditor()); 
    }
    
    private void configureDialog(){
        dialog = new Dialog();
        dia = new ContactFormEdit(service.findAllCompanies(),service.findAllStatuses());
        dialog.add(dia.firstName,
                dia.lastName,
                dia.email,
                dia.identityNumber,
                dia.gender,
                dia.company,
                dia.status,
                dia.createButtonsLayout());
        dialog.setWidth("25em");
        dia.addListener(ContactFormEdit.SaveEvent.class, this::saveDia);
        dia.addListener(ContactFormEdit.DeleteEvent.class, this::deleteDia);
        
        Button closeButton = new Button(new Icon("lumo", "cross"), (e) -> dialog.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        dialog.add(closeButton);

    }
    

    private void configureGrid() {
        grid.addClassNames("contact-grid");
        grid.setSizeFull();
        grid.setColumns("firstName", "lastName", "email");
        grid.addColumn("identityNumber");
        grid.addColumn("gender");
        grid.addComponentColumn(item -> {
	Icon icon;
	if(item.getGender().equals("Female")){
		icon = VaadinIcon.FEMALE.create();
		icon.setColor("pink");
	} else {
		icon = VaadinIcon.MALE.create();
		icon.setColor("blue");
	}
	return icon;
});
        
        grid.addColumn(contact -> contact.getStatus().getName()).setHeader("Status");
        grid.addColumn(contact -> contact.getCompany().getName()).setHeader("Company");
     
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(event ->
            editContact(event.getValue())); 
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Filter by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        Button addContactButton = new Button("Add contact");
        addContactButton.addClickListener(click -> addContact()); 

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addContactButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    public void editContact(Contact contact) { 
        if (contact == null) {
            closeEditor();
        } else {
            form.setContact(contact);
            dia.setContact(contact);
            addClassName("editing");
            dialog.open();
            form.setVisible(false);
            
        }
    }
    
    private void saveContact(ContactForm.SaveEvent event) {
    service.saveContact(event.getContact());
    closeEditor();
    updateList();

    
}
    private void saveDia(ContactFormEdit.SaveEvent event){
         service.saveContact(event.getContact());
         updateList();
         dialog.close();
    }
    private void deleteContact(ContactForm.DeleteEvent event) {
        service.deleteContact(event.getContact());
        closeEditor();
        updateList();  
    }
    private void deleteDia(ContactFormEdit.DeleteEvent event){
         service.deleteContact(event.getContact());
         updateList();
         dialog.close();
    }
    private void closeEditor() {
        form.setContact(null);
        form.setVisible(false);
        removeClassName("editing");
    }
    private void addContact() { 
        grid.asSingleSelect().clear();
        editContact(new Contact());
        form.setVisible(true);
        dialog.close();
    }
    private void updateList() {
        grid.setItems(service.findAllContacts(filterText.getValue()));
    }
}
