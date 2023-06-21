package com.example.application.views.main.components;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

import java.time.LocalDate;
import java.util.Arrays;

public class AddExpenseDialog extends Dialog {

    private final TextField nameField = new TextField("Expense Name");
    private final TextArea descriptionField = new TextArea("Description");
    private final NumberField amountField = new NumberField("Amount");
    private final ComboBox<String> intervalField = new ComboBox<>("Interval");
    private final DatePicker dateField = new DatePicker("Date");

    private final Button saveButton = new Button("Add");
    private final Button cancelButton = new Button("Cancel");

    public AddExpenseDialog() {
        this.setHeaderTitle("Add New Expense");
        add(createDialogLayout());
        addStyleToElements();

        this.getFooter().add(cancelButton, saveButton);
    }

    private void addStyleToElements() {
        nameField.setRequired(true);
        amountField.setRequired(true);
        intervalField.setRequired(true);

        intervalField.setAllowCustomValue(true);
        intervalField.setItems("ONCE", "DAILY", "WEEKLY", "MONTHLY", "YEARLY");
        intervalField.setHelperText("Select the interval this expense will be triggered");
        amountField.setSuffixComponent(new Span("MDL"));

        DatePicker.DatePickerI18n singleFormatI18n = new DatePicker.DatePickerI18n();
        singleFormatI18n.setDateFormat("yyyy-MM-dd");
        dateField.setHelperText("Format: YYYY-MM-DD");
        dateField.setI18n(singleFormatI18n);
        dateField.setValue(LocalDate.now());

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancelButton.addClickShortcut(Key.ESCAPE);
        cancelButton.addClickListener(e -> this.close());
    }

    private VerticalLayout createDialogLayout() {
        Component[] components = {nameField, descriptionField, amountField, dateField, intervalField};
        VerticalLayout dialogLayout = new VerticalLayout(components);
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "22rem").set("max-width", "100%");
        Arrays.stream(components).forEach(e -> e.getStyle().set("margin-bottom", "1rem"));

        return dialogLayout;
    }

    public void handleSaveButtonClick(ComponentEventListener<ClickEvent<Button>> clickListener) {
        saveButton.addClickListener(event -> {
            clickListener.onComponentEvent(event);
            this.close();
        });
    }

    public boolean isFilledCorrectly() {
        return !nameField.isEmpty() &&
                !intervalField.isEmpty() &&
                !amountField.isEmpty() &&
                amountField.getValue() > 0;
    }
    
    public String getNameValue() {
        return nameField.getValue();
    }

    public double getAmountValue() {
        return amountField.getValue();
    }

    public LocalDate getDateValue() {
        return dateField.getValue();
    }
    
    public String getDescriptionValue() {
        return descriptionField.getValue();
    }
    
    public String getIntervalValue() {
        return intervalField.getValue();
    }

}
