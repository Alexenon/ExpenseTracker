package com.example.application.views.main.components;

import com.example.application.dtos.Categories;
import com.example.application.dtos.ExpenseRequest;
import com.example.application.dtos.Timestamps;
import com.example.application.entities.Expense;
import com.example.application.services.ExpenseService;
import com.example.application.utils.ExpenseConvertor;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class AddExpenseDialog extends Dialog {

    private static final Logger logger = LoggerFactory.getLogger(AddExpenseDialog.class);

    private final ExpenseService expenseService;
    private final ExpenseConvertor expenseConvertor;
    private final DatePicker.DatePickerI18n singleFormatI18n;

    private final TextField nameField = new TextField("Expense Name");
    private final TextArea descriptionField = new TextArea("Description");
    private final NumberField amountField = new NumberField("Amount");
    private final Select<String> intervalField = new Select<>();
    private final ComboBox<String> categoryField = new ComboBox<>("Category");
    private final DatePicker startDateField = new DatePicker("Start Date");
    private final DatePicker expireDateField = new DatePicker("Expire Date");
    private final Button saveButton = new Button("Save");
    private final Button cancelButton = new Button("Cancel");

    private Binder<ExpenseRequest> binder;

    @Autowired
    public AddExpenseDialog(ExpenseService expenseService,
                            ExpenseConvertor expenseConvertor,
                            DatePicker.DatePickerI18n singleFormatI18n) {
        this.expenseService = expenseService;
        this.expenseConvertor = expenseConvertor;
        this.singleFormatI18n = singleFormatI18n;

        setHeaderTitle("Add New Expense");
        initBinder();
        add(createDialogLayout());
        addStyleToElements();
    }

    private VerticalLayout createDialogLayout() {
        Component[] components = {nameField, descriptionField, amountField, categoryField, startDateField, intervalField, expireDateField};
        VerticalLayout dialogLayout = new VerticalLayout(components);
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "22rem").set("max-width", "100%");
        Arrays.stream(components).forEach(e -> e.getStyle().set("margin-bottom", "1rem"));

        return dialogLayout;
    }

    private void initBinder() {
        binder = new Binder<>(ExpenseRequest.class);
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
                .bind(ExpenseRequest::getCategoryName, ExpenseRequest::setCategoryName);

        binder.forField(intervalField)
                .asRequired("Please fill this field")
                .bind(ExpenseRequest::getTimestampName, ExpenseRequest::setTimestampName);

        binder.forField(startDateField)
                .asRequired("Please fill this field")
                .bind(ExpenseRequest::getStartDate, ExpenseRequest::setStartDate);

        binder.forField(expireDateField)
                .withValidator(
                        expireDate -> expireDate == null || expireDate.isAfter(startDateField.getValue()),
                        "Expire date should be after start date"
                )
                .bind(ExpenseRequest::getExpireDate, ExpenseRequest::setExpireDate);

        binder.bind(descriptionField, ExpenseRequest::getDescription, ExpenseRequest::setDescription);
    }

    private void addStyleToElements() {
        List<String> timestampNames = Arrays.stream(Timestamps.values()).map(Timestamps::toString).toList();
        List<String> categoryNames = Arrays.stream(Categories.values()).map(Categories::toString).toList();

        intervalField.setLabel("Interval");
        intervalField.setItems(timestampNames);
        intervalField.setHelperText("Select how often this expense will be triggered");
        intervalField.addValueChangeListener(e -> expireDateField.setEnabled(!Objects.equals(e.getValue(), "ONCE")));

        categoryField.setItems(categoryNames);
        categoryField.setHelperText("Select the category which fits this expense");
        amountField.setSuffixComponent(new Span("MDL"));

        startDateField.setI18n(singleFormatI18n);
        startDateField.setHelperText("Format: YYYY-MM-DD");
        startDateField.setValue(LocalDate.now(ZoneId.systemDefault()));

        expireDateField.setEnabled(false); // Expire date field initial is disabled
        expireDateField.setI18n(singleFormatI18n);
        expireDateField.setTooltipText("Select expire date");
        expireDateField.setPlaceholder("Optional: Choose expire date");
        expireDateField.setHelperText("Format: YYYY-MM-DD");

        cancelButton.addClickShortcut(Key.ESCAPE);
        cancelButton.addClickListener(e -> {
            logger.info("Exited `Add New Expense` form");
            this.close();
        });

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        saveButton.addClickListener(e -> defaultClickSaveBtnListener());

        this.getFooter().add(cancelButton, saveButton);
    }

    private void defaultClickSaveBtnListener() {
        logger.info("Clicked on Save button inside `Add New Expense` form");
        if (binder.validate().isOk()) {
            logger.info("Saved expense using data provided inside `Add New Expense` form");
            expenseService.saveExpense(getExpenseFromBinder());
            showSuccesfullNotification();
            this.close();
        } else {
            logger.warn("Submiting `Add New Expense` form with validation errors");
            showErrorNotification();
        }
    }

    public void addClickSaveBtnListener(Consumer<?> listener) {
        saveButton.addClickListener(e -> listener.accept(null));
    }

    private Expense getExpenseFromBinder() {
        return expenseConvertor.convertToExpense(binder.getBean());
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

    @Override
    public void open() {
        super.open();
        logger.info("Opened `Add New Expense` form");
    }
}
