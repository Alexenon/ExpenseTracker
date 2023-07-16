package com.example.application.views.main.components;

import com.example.application.model.*;
import com.example.application.service.CategoryService;
import com.example.application.service.ExpenseService;
import com.example.application.service.TimestampService;
import com.example.application.views.main.ExpensesView;
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
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class AddExpenseDialog extends Dialog {

    private static final Logger logger = LoggerFactory.getLogger(AddExpenseDialog.class);

    private final ExpenseService expenseService;
    private final TimestampService timestampService;
    private final CategoryService categoryService;
    private final DatePicker.DatePickerI18n singleFormatI18n;

    private final Binder<ExpenseRequest> binder;

    private final TextField nameField = new TextField("Expense Name");
    private final TextArea descriptionField = new TextArea("Description");
    private final NumberField amountField = new NumberField("Amount");
    private final Select<String> intervalField = new Select<>();
    private final ComboBox<String> categoryField = new ComboBox<>("Category");
    private final DatePicker startDateField = new DatePicker("Start Date");
    private final DatePicker expiryField = new DatePicker("Expiry Date");
    private final Button saveButton = new Button("Save");
    private final Button cancelButton = new Button("Cancel");

    @Autowired
    public AddExpenseDialog(ExpenseService expenseService,
                            TimestampService timestampService,
                            CategoryService categoryService,
                            DatePicker.DatePickerI18n singleFormatI18n) {
        this.expenseService = expenseService;
        this.timestampService = timestampService;
        this.categoryService = categoryService;
        this.singleFormatI18n = singleFormatI18n;

        setHeaderTitle("Add New Expense");
        add(createDialogLayout());
        binder = new Binder<>();
        setupBinder();
        addStyleToElements();
    }


    private VerticalLayout createDialogLayout() {
        Component[] components = {nameField, descriptionField, amountField, categoryField, startDateField, intervalField, expiryField};
        VerticalLayout dialogLayout = new VerticalLayout(components);
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "22rem").set("max-width", "100%");
        Arrays.stream(components).forEach(e -> e.getStyle().set("margin-bottom", "1rem"));

        return dialogLayout;
    }

    private void setupBinder() {
        binder.setBean(new ExpenseRequest());
        binder.forField(nameField)
                .asRequired("Please fill this field")
                .withValidator(name -> name.length() >= 3, "Name must contain at least 3 characters")
                .bind(ExpenseRequest::getName, ExpenseRequest::setName);

        binder.forField(amountField)
                .asRequired("Please fill this field")
                .withValidator(amount -> amount >= 0, "Amount should be greater or equal to 0")
                .bind(ExpenseRequest::getAmount, ExpenseRequest::setAmount);

        binder.forField(categoryField)
                .asRequired("Please fill this field")
                .bind(ExpenseRequest::getCategory, ExpenseRequest::setCategory);

        binder.forField(intervalField)
                .asRequired("Please fill this field")
                .bind(ExpenseRequest::getTimestamp, ExpenseRequest::setTimestamp);

        binder.forField(startDateField)
                .asRequired("Please fill this field")
                .bind(ExpenseRequest::getDate, ExpenseRequest::setDate);

        binder.forField(expiryField)
                .withValidator(
                        expireDate -> expireDate == null || expireDate.isAfter(startDateField.getValue()),
                        "Expire date should be after start date"
                )
                .bind(ExpenseRequest::getExpiryDate, ExpenseRequest::setExpiryDate);

        binder.bind(descriptionField, ExpenseRequest::getDescription, ExpenseRequest::setDescription);
    }

    private void addStyleToElements() {
        List<String> timestampNames = timestampService.getAllTimestamps().stream().map(Timestamp::getName).toList();
        List<String> categoryNames = categoryService.getAllCategories().stream().map(Category::getName).toList();

        intervalField.setLabel("Interval");
        intervalField.setItems(timestampNames);
        intervalField.setHelperText("Select how often this expense will be triggered");
        intervalField.addValueChangeListener(e -> expiryField.setEnabled(!Objects.equals(e.getValue(), "ONCE")));

        categoryField.setItems(categoryNames);
        categoryField.setHelperText("Select the category which fits this expense");
        amountField.setSuffixComponent(new Span("MDL"));

        startDateField.setI18n(singleFormatI18n);
        startDateField.setHelperText("Format: YYYY-MM-DD");
        startDateField.setValue(LocalDate.now(ZoneId.systemDefault()));

        expiryField.setEnabled(false); // Expiry date field initial is disabled
        expiryField.setI18n(singleFormatI18n);
        expiryField.setTooltipText("Select expire date");
        expiryField.setPlaceholder("Optional: Choose expire date");
        expiryField.setHelperText("Format: YYYY-MM-DD");

        cancelButton.addClickShortcut(Key.ESCAPE);
        cancelButton.addClickListener(e -> {
            logger.info("Exiting `Add New Expense` form");
            this.close();
        });

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        saveButton.addClickListener(e -> defaultClickSaveBtnListener());

        this.getFooter().add(cancelButton, saveButton);
    }

    private void defaultClickSaveBtnListener() {
        logger.info("Clicking on Save button inside `Add New Expense` form");
        if (binder.validate().isOk()) {
            logger.info("Saving expense using data provided inside `Add New Expense` form");
            ExpenseConvertor convertor = new ExpenseConvertor(timestampService, categoryService);
            ExpenseRequest expenseRequest = binder.getBean();
            Expense expense = convertor.convertToExpense(expenseRequest);
            expenseService.saveExpense(expense);
            showSuccesfullNotification();
            this.close();
        } else {
            logger.warn("Submiting `Add New Expense` form with validation errors");
            showErrorNotification();
        }
    }

    public void addClickSaveBtnListener(Consumer<ExpensesView> listener) {
        logger.info("Added additional listener for Save Button");
        saveButton.addClickListener(e -> listener.accept(null));
    }

    private void showSuccesfullNotification() {
        Notification notification = Notification.show("Expense submitted succesfully!");
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.setPosition(Notification.Position.TOP_CENTER);
        notification.setDuration(5000);
        notification.open();
    }

    private void showErrorNotification() {
        Notification notification = Notification.show("An error occured while submiting Add New Expense form");
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.setPosition(Notification.Position.TOP_CENTER);
        notification.setDuration(5000);
        notification.open();
    }

}
