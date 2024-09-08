package com.example.application.views.components.complex_components.dialogs;

import com.example.application.data.dtos.ExpenseDTO;
import com.example.application.data.enums.Timestamps;
import com.example.application.data.requests.ExpenseRequest;
import com.example.application.entities.Expense;
import com.example.application.services.CategoryService;
import com.example.application.services.ExpenseService;
import com.example.application.views.components.utils.HasNotifications;
import com.example.application.views.pages.ExpensesView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class EditExpenseDialog extends Dialog implements HasNotifications {

    private static final Logger logger = LoggerFactory.getLogger(EditExpenseDialog.class);

    private final ExpenseDTO expenseDTO;
    private final ExpenseService expenseService;
    private final CategoryService categoryService;
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
    public EditExpenseDialog(ExpenseDTO expenseDTO,
                             ExpenseService expenseService,
                             CategoryService categoryService,
                             DatePicker.DatePickerI18n singleFormatI18n) {
        this.expenseDTO = expenseDTO;
        this.expenseService = expenseService;
        this.categoryService = categoryService;
        this.singleFormatI18n = singleFormatI18n;

        setHeaderTitle("Edit Expense");
        initBinder();
        add(createDialogLayout());
        addStyleToElements();
        fillFieldsWithValues();
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

        intervalField.setLabel("Interval");
        intervalField.setItems(timestampNames);
        intervalField.setHelperText("Select how often this expense will be triggered");
        intervalField.addValueChangeListener(e -> expireDateField.setEnabled(!Objects.equals(e.getValue(), "ONCE")));

        categoryField.setItems(categoryService.getAllCategoryNames());
        categoryField.setHelperText("Select the category which fits this expense");
        amountField.setSuffixComponent(new Span("MDL"));

        startDateField.setI18n(singleFormatI18n);
        startDateField.setHelperText("Format: YYYY-MM-DD");

        expireDateField.setEnabled(false); // Expire date field initial is disabled
        expireDateField.setI18n(singleFormatI18n);
        expireDateField.setTooltipText("Select expire date");
        expireDateField.setPlaceholder("Optional: Choose expire date");
        expireDateField.setHelperText("Format: YYYY-MM-DD");

        cancelButton.addClickShortcut(Key.ESCAPE);
        cancelButton.addClickListener(e -> {
            logger.info("Exited `Edit Expense` form");
            this.close();
        });

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        saveButton.addClickListener(e -> defaultClickSaveBtnListener());

        this.getFooter().add(cancelButton, saveButton);
    }

    private void fillFieldsWithValues() {
        nameField.setValue(expenseDTO.getName());
        descriptionField.setValue(expenseDTO.getDescription());
        amountField.setValue(expenseDTO.getAmount());
        categoryField.setValue(expenseDTO.getCategory());
        startDateField.setValue(expenseDTO.getStartDate());
        intervalField.setValue(expenseDTO.getTimestamp());
        expireDateField.setValue(expenseDTO.getExpireDate());
    }

    private void defaultClickSaveBtnListener() {
        logger.info("Clicked on Save button inside `Edit Expense` form");
        if (binder.validate().isOk()) {
            logger.info("Updated the expense using data provided inside `Edit Expense` form");
            expenseService.saveExpense(getExpenseFromBinder());
            showSuccessfulNotification("Expense submitted successfully!");
            this.close();
        } else {
            logger.warn("Submitted `Edit Expense` form with validation errors");
            showErrorNotification("An error occurred while submitting `Edit Expense` form");
        }
    }

    public void addClickSaveBtnListener(Consumer<ExpensesView> listener) {
        saveButton.addClickListener(e -> listener.accept(null));
    }

    private Expense getExpenseFromBinder() {
        Expense expense = expenseService.convertToExpense(binder.getBean());
        expense.setId(expenseDTO.getId()); // ID is set for replacing existing expense with this one
        return expense;
    }

    @Override
    public void open() {
        super.open();
        logger.info("Opened `Edit Expense` form");
    }
}
