package com.example.application.views.components.custom.dialogs;

import com.example.application.data.dtos.expense.CategoryDTO;
import com.example.application.data.requests.expenses.CreateExpenseRequest;
import com.example.application.entities.expenses.ExpenseTimestamp;
import com.example.application.entities.expenses.Tag;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.views.components.core.TagInput;
import com.example.application.views.components.utils.HasNotifications;
import com.example.application.views.pages.expenses.ExpensesView;
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
import com.vaadin.flow.data.validator.DoubleRangeValidator;
import com.vaadin.flow.data.validator.StringLengthValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MONTHS;

public class AddExpenseDialog extends Dialog implements HasNotifications {

	private static final Logger logger = LoggerFactory.getLogger(AddExpenseDialog.class);

	private final InstrumentsFacadeService instrumentsFacadeService;

	private final TextField nameField = new TextField("Expense Name");
	private final TextArea descriptionField = new TextArea("Description");
	private final NumberField amountField = new NumberField("Amount");
	private final Select<ExpenseTimestamp> timestampField = new Select<>();
	private final ComboBox<String> categoryField = new ComboBox<>("Category");
	private final TagInput tagsField = new TagInput();
	private final DatePicker startDateField = new DatePicker("Start Date");
	private final DatePicker expireDateField = new DatePicker("Expire Date");
	private final Button saveButton = new Button("Save");
	private final Button cancelButton = new Button("Cancel");

	private final DatePicker.DatePickerI18n singleFormatI18n;

	private Binder<CreateExpenseRequest> binder;

	@Autowired
	public AddExpenseDialog(InstrumentsFacadeService instrumentsFacadeService,
							DatePicker.DatePickerI18n singleFormatI18n)
	{
		this.instrumentsFacadeService = instrumentsFacadeService;
		this.singleFormatI18n = singleFormatI18n;

		setHeaderTitle("Add New Expense");
		initFields();
		initBinder();
		add(createDialogLayout());
	}

	private VerticalLayout createDialogLayout() {

		Component[] components = {nameField, descriptionField, amountField, categoryField, tagsField, timestampField, startDateField, expireDateField};
		VerticalLayout dialogLayout = new VerticalLayout(components);
		dialogLayout.setPadding(false);
		dialogLayout.setSpacing(false);
		dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
		dialogLayout.getStyle()
				.set("width", "22rem")
				.set("max-width", "100%");
		Stream.of(components).forEach(e -> e.getStyle().set("margin-bottom", "1rem"));

		return dialogLayout;
	}

	private void initFields() {
		nameField.getElement().setAttribute("autocomplete", "off");

		timestampField.setLabel("Interval");
		timestampField.setItems(ExpenseTimestamp.values());
		timestampField.setHelperText("Select how often this expense will be triggered");
		timestampField.addValueChangeListener(timestamp -> {
			boolean timestampIsOnce = timestamp.getValue().equals(ExpenseTimestamp.ONCE);

			if (timestampIsOnce)
				expireDateField.setValue(null);

			expireDateField.setVisible(!timestampIsOnce);
		});

		categoryField.setItems(getUserCategories());
		categoryField.setHelperText("Select the category which fits this expense");

		tagsField.setItems(getUserTags());
		tagsField.setHelperText("Add tags related with this expenses in case there are");
		amountField.setSuffixComponent(new Span("MDL"));

		startDateField.setI18n(singleFormatI18n);
		startDateField.setHelperText("Format: YYYY-MM-DD");
		startDateField.setValue(LocalDate.now());

		expireDateField.setVisible(false); // Expire date field initial is disabled
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

	private void initBinder() {
		binder = new Binder<>(CreateExpenseRequest.class);
		binder.setBean(defaultRequest());
		binder.forField(nameField)
				.asRequired("Please fill this field")
				.withValidator(new StringLengthValidator("Name should be between 3 and 50 characters", 3, 50))
				.bind(CreateExpenseRequest::getName, CreateExpenseRequest::setName);

		binder.forField(descriptionField)
				.withValidator(new StringLengthValidator("Description can be up to 250 characters", 0, 250))
				.bind(CreateExpenseRequest::getDescription, CreateExpenseRequest::setDescription);

		binder.forField(amountField)
				.asRequired("Please fill this field")
				.withValidator(new DoubleRangeValidator("Invalid decimal value", 0.0, Double.MAX_VALUE))
				.withValidator(amount -> amount != null && amount > 0, "Amount should be greater than 0")
				.bind(CreateExpenseRequest::getAmount, CreateExpenseRequest::setAmount);

		binder.forField(categoryField)
				.asRequired("Please fill this field")
				.bind(CreateExpenseRequest::getCategory, CreateExpenseRequest::setCategory);

		binder.forField(tagsField)
				.withValidator(tags -> tags.stream()
								.allMatch(tag -> tag != null && tag.length() >= 4 && tag.length() <= 20),
						"All tags should be between 4 and 20 characters long"
				).bind(CreateExpenseRequest::getTags, CreateExpenseRequest::setTags);

		binder.forField(timestampField)
				.asRequired("Please fill this field")
				.bind(CreateExpenseRequest::getTimestamp, CreateExpenseRequest::setTimestamp);

		binder.forField(startDateField)
				.asRequired("Please fill this field")
				.bind(CreateExpenseRequest::getStartDate, CreateExpenseRequest::setStartDate);

		binder.forField(expireDateField)
				.withValidator(
						expireDate -> expireDate == null || startDateField.getValue() == null
									  || expireDate.isAfter(startDateField.getValue()),
						"Expire date should be after start date")
				.withValidator(
						expireDate -> {
							if (expireDate == null || startDateField.getValue() == null) return true;

							return !timestampField.getValue().equals(ExpenseTimestamp.WEEKLY)
								   || DAYS.between(startDateField.getValue(), expireDate) >= 7;
						}, "Should pass at least 7 days to end subscription"
				)
				.withValidator(
						expireDate -> {
							if (expireDate == null || startDateField.getValue() == null)
								return true;

							return !timestampField.getValue().equals(ExpenseTimestamp.MONTHLY)
								   || MONTHS.between(startDateField.getValue(), expireDate) >= 1;
						}, "Should pass at least 1 month to end subscription"
				)
				.bind(CreateExpenseRequest::getExpireDate, CreateExpenseRequest::setExpireDate);

		binder.bind(descriptionField, CreateExpenseRequest::getDescription, CreateExpenseRequest::setDescription);
	}

	private void defaultClickSaveBtnListener() {
		logger.info("Clicked on Save button inside `Add New Expense` form");
		if (binder.validate().isOk()) {
			logger.info("Saved expense using data provided inside `Add New Expense` form");
			instrumentsFacadeService.createExpense(binder.getBean());
			showSuccessfulNotification("Expense submitted successfully!");
			this.close();
		} else {
			logger.warn("Submitting `Add New Expense` form with validation errors");
			showErrorNotification("An error occurred while submitting Add New Expense form");
		}
	}

	public void addSaveBtnClickListener(Consumer<ExpensesView> listener) {
		saveButton.addClickListener(e -> listener.accept(null));
	}

	private List<String> getUserCategories() {
		return instrumentsFacadeService.findUserCategories()
				.stream()
				.map(CategoryDTO::getName)
				.toList();
	}

	private List<String> getUserTags() {
		return instrumentsFacadeService.findUserTags()
				.stream()
				.map(Tag::getName)
				.toList();
	}

	@Override
	public void open() {
		super.open();
		logger.info("Opened `Add New Expense` form");
	}

	private CreateExpenseRequest defaultRequest() {
		CreateExpenseRequest request = new CreateExpenseRequest();
		request.setUserId(instrumentsFacadeService.getAuthenticatedUser().getId());
		return request;
	}
}
