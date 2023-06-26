package com.example.application.views.main.components;

import com.example.application.model.Expense;
import com.example.application.model.ExpenseConvertor;
import com.example.application.model.ExpenseRequest;
import com.example.application.service.CategoryService;
import com.example.application.service.ExpenseService;
import com.example.application.service.TimestampService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
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
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.shared.HasValidationProperties;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AddExpenseDialog extends Dialog {

    private static final Logger logger = LoggerFactory.getLogger(AddExpenseDialog.class);

    private final TextField nameField = new TextField("Expense Name");
    private final TextArea descriptionField = new TextArea("Description");
    private final NumberField amountField = new NumberField("Amount");
    private final Select<String> intervalField = new Select<>();
    private final ComboBox<String> categoryField = new ComboBox<>("Category");
    private final DatePicker dateField = new DatePicker("Date");

    private final Button saveButton = new Button("Add");
    private final Button cancelButton = new Button("Cancel");

    private final Component[] requiredComponents = {nameField, amountField, categoryField, intervalField};

    public AddExpenseDialog() {
        this.setHeaderTitle("Add New Expense");
        add(createDialogLayout());
        addStyleToElements();

        this.getFooter().add(cancelButton, saveButton);
    }

    private void addStyleToElements() {
        Arrays.stream(requiredComponents).forEach(component -> {
            ((HasValue<?, ?>) component).setRequiredIndicatorVisible(true);
            ((HasValidationProperties) component).setErrorMessage("Please fill this field");
        });

        intervalField.setLabel("Interval");
        intervalField.setItems("ONCE", "DAILY", "WEEKLY", "MONTHLY", "YEARLY");
        intervalField.setHelperText("Select the interval this expense will be triggered");
        categoryField.setItems("Others", "Services", "Food and Drinks", "Distractions");
        categoryField.setHelperText("Select the category which fits this expense");
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
        Component[] components = {nameField, descriptionField, amountField, categoryField, dateField, intervalField};
        VerticalLayout dialogLayout = new VerticalLayout(components);
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "22rem").set("max-width", "100%");
        Arrays.stream(components).forEach(e -> e.getStyle().set("margin-bottom", "1rem"));

        return dialogLayout;
    }

    public void saveExpenseUsingForm(ExpenseService expenseService, TimestampService timestampService, CategoryService categoryService) {
        logger.info("Clicking on " + saveButton.getText() + " button");
        saveButton.addClickListener(event -> {
            if (isFilledCorrectly()) {
                logger.info("Saving expense using data provided inside `Add New Expense` form");
                ExpenseRequest expenseRequest = new ExpenseRequest();
                expenseRequest.setName(nameField.getValue());
                expenseRequest.setAmount(amountField.getValue());
                expenseRequest.setDate(dateField.getValue());
                expenseRequest.setDescription(descriptionField.getValue());
                expenseRequest.setTimestamp(intervalField.getValue());

                ExpenseConvertor expenseConvertor = new ExpenseConvertor(timestampService, categoryService);
                Expense expense = expenseConvertor.convertToExpense(expenseRequest);
                final Expense saveExpense = expenseService.saveExpense(expense);
                System.out.println(saveExpense);
                showSuccesfullNotification();
                this.close();
            } else {
                logger.warn("Trying to submit form without all required fields to be filled");
            }
        });
    }

    private List<Component> getListOfEmptyComponents() {
        return Arrays.stream(requiredComponents)
                .filter(component -> ((HasValue<?, ?>) component).isEmpty())
                .collect(Collectors.toList());
    }

    public boolean isFilledCorrectly() {
        return getListOfEmptyComponents().isEmpty();
    }

    private void showSuccesfullNotification() {
        Notification notification = Notification.show("Expenses submitted!");
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.setPosition(Notification.Position.TOP_CENTER);
        notification.setDuration(5000);
        notification.open();
    }

}
