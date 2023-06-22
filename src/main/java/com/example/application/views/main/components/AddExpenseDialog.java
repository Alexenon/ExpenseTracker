package com.example.application.views.main.components;

import com.example.application.model.Expense;
import com.example.application.model.ExpenseConvertor;
import com.example.application.model.ExpenseRequest;
import com.example.application.service.CategoryService;
import com.example.application.service.ExpenseService;
import com.example.application.service.TimestampService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Arrays;

public class AddExpenseDialog extends Dialog {

    private final TimestampService timestampService;
    private final CategoryService categoryService;

    private final TextField nameField = new TextField("Expense Name");
    private final TextArea descriptionField = new TextArea("Description");
    private final NumberField amountField = new NumberField("Amount");
    private final ComboBox<String> intervalField = new ComboBox<>("Interval"); // Select -> element
    private final ComboBox<String> categoryField = new ComboBox<>("Category"); // Combobox -> element
    private final DatePicker dateField = new DatePicker("Date");

    private final Button saveButton = new Button("Add");
    private final Button cancelButton = new Button("Cancel");

    public AddExpenseDialog(TimestampService timestampService, CategoryService categoryService) {
        this.timestampService = timestampService;
        this.categoryService = categoryService;

        this.setHeaderTitle("Add New Expense");
        add(createDialogLayout());
        addStyleToElements();

        this.getFooter().add(cancelButton, saveButton);
    }

    private void addStyleToElements() {
        nameField.setRequired(true);
        nameField.setErrorMessage("Please fill this field");
        amountField.setRequired(true);
        amountField.setErrorMessage("Please fill this field");
        intervalField.setRequired(true);
        intervalField.setErrorMessage("Please fill this field");

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

    public void saveExpenseUsingForm(ExpenseService expenseService) {
        saveButton.addClickListener(event -> {
            if (isFilledCorrectly()) {
                ExpenseRequest expenseRequest = new ExpenseRequest();
                expenseRequest.setName(nameField.getValue());
                expenseRequest.setAmount(amountField.getValue());
                expenseRequest.setDate(dateField.getValue());
                expenseRequest.setDescription(descriptionField.getValue());
                expenseRequest.setTimestamp(intervalField.getValue());

                ExpenseConvertor expenseConvertor = new ExpenseConvertor(timestampService, categoryService);
                Expense expense = new ExpenseConvertor().convertToExpense(expenseRequest);
                expenseService.saveExpense(expense);
                showSuccesfullNotification();
                this.close();
            }
        });
    }

    public boolean isFilledCorrectly() {
        return !nameField.isEmpty() &&
                !intervalField.isEmpty() &&
                !amountField.isEmpty() &&
                amountField.getValue() > 0;
    }

    private void showSuccesfullNotification() {
        Notification notification = Notification.show("Expenses submitted!");
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.setPosition(Notification.Position.TOP_CENTER);
        notification.setDuration(5000);
        notification.open();
    }

}
