package com.example.application.views.components.custom.dialogs.transactions;

import com.example.application.data.dtos.TransactionDTO;
import com.example.application.data.requests.UpdateTransactionRequest;
import com.example.application.entities.common.TransactionType;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.utils.common.formatters.CommonFormatters;
import com.example.application.views.components.core.Container;
import com.example.application.views.components.custom.fields.AmountField;
import com.example.application.views.components.custom.fields.AssetComboBox;
import com.example.application.views.components.custom.fields.CurrencyField;
import com.example.application.views.components.utils.HasNotifications;
import com.example.application.views.components.utils.convertors.FlexibleAmountConvertor;
import com.example.application.views.components.utils.convertors.FlexiblePriceConvertor;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.DoubleRangeValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

public class EditTransactionDialog extends Dialog implements HasNotifications {

	private final TransactionDTO transaction;
	private final UpdateTransactionRequest request;
	private final UpdateTransactionRequest initialRequest;
	private final InstrumentsFacadeService instrumentsFacadeService;

	private final Binder<UpdateTransactionRequest> binder = new Binder<>(UpdateTransactionRequest.class);

	private final AssetComboBox assetSymbolField;
	private final Select<TransactionType> typeField = new Select<>();
	private final AmountField amountField = new AmountField("Amount");
	private final CurrencyField marketPriceField = new CurrencyField("Price");
	private final CurrencyField totalCostField = new CurrencyField("Total");
	private final DateTimePicker datePicker = new DateTimePicker("Date & Time");
	private final TextArea notesField = new TextArea("Notes");

	private final Button saveButton = new Button("Save");
	private final Button cancelButton = new Button("Cancel");
	private final Span symbolSuffix = new Span();

	@Autowired
	public EditTransactionDialog(TransactionDTO transactionDTO,
								 InstrumentsFacadeService instrumentsFacadeService)
	{
		this.transaction = transactionDTO;
		this.request = new UpdateTransactionRequest(transactionDTO);
		this.initialRequest = new UpdateTransactionRequest(request);
		this.instrumentsFacadeService = instrumentsFacadeService;
		this.assetSymbolField = new AssetComboBox(instrumentsFacadeService);

		buildForm();
	}

	private void buildForm() {
		setHeaderTitle("Edit Transaction");
		initializeFields();
		initializeFieldsValues();
		initializeFieldListeners();
		initializeBinder();

		Container formBody = Container.builder("transaction-modal")
				.addComponent(assetSymbolField)
				.addComponent(typeField)
				.addComponent(amountField)
				.addComponent(marketPriceField)
				.addComponent(totalCostField)
				.addComponent(notesField)
				.addComponent(datePicker)
				.build();
		add(formBody);

		getFooter().add(saveButton, cancelButton);
	}

	private void initializeFields() {
		typeField.setLabel("Transaction Type");
		typeField.setItems(TransactionType.values());
		displayHintMarketPrice();
		displayHintAmountOfTokens();
	}

	private void initializeFieldsValues() {
		assetSymbolField.setValue(initialRequest.getAssetSymbol());
		typeField.setValue(initialRequest.getType());
		amountField.setValue(initialRequest.getOrderQuantity());
		totalCostField.setValue(initialRequest.getOrderTotalCost());
		marketPriceField.setValue(initialRequest.getMarketPrice());
		datePicker.setValue(initialRequest.getDateTime());
	}

	private void initializeFieldListeners() {
		assetSymbolField.addValueChangeListener(l -> {
			binder.setValidatorsDisabled(false);
			marketPriceField.setValue(assetSymbolField.getMarketPrice());
			symbolSuffix.setText(assetSymbolField.getSymbol());
			displayHintMarketPrice();
			displayHintAmountOfTokens();
		});

		typeField.addValueChangeListener(e -> displayHintAmountOfTokens());

		amountField.setSuffixComponent(symbolSuffix);
		amountField.setValueChangeMode(ValueChangeMode.EAGER);
		amountField.addKeyUpListener(e -> {
			double totalPrice = amountField.doubleValue() * marketPriceField.doubleValue();
			totalCostField.setValue(totalPrice);
			binder.validate();
		});

		marketPriceField.setValueChangeMode(ValueChangeMode.EAGER);
		marketPriceField.addKeyUpListener(e -> {
			double totalPrice = amountField.doubleValue() * marketPriceField.doubleValue();
			totalCostField.setValue(totalPrice);
			binder.validate();
		});

		totalCostField.setValueChangeMode(ValueChangeMode.EAGER);
		totalCostField.addKeyUpListener(e -> {
			double amount = 0;
			if (marketPriceField.doubleValue() != 0) {
				String textPrice = totalCostField.getValue().replaceAll(",", "");
				double totalPrice = Double.parseDouble(textPrice.isEmpty() ? "0" : textPrice);
				amount = totalPrice / marketPriceField.doubleValue();
			}

			amountField.setValue(amount);
			binder.validate();
		});

		saveButton.addClickShortcut(Key.ENTER);
		saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
		saveButton.addClickListener(e -> handleTransactionSave());

		cancelButton.addClickShortcut(Key.ESCAPE);
		cancelButton.addClickListener(e -> {
			initializeFieldsValues();
			this.close();
		});
	}

	private void initializeBinder() {
		binder.setBean(request);

		binder.forField(assetSymbolField)
				.asRequired("Please fill this field")
				.bind(req -> instrumentsFacadeService.getAssetBySymbol(req.getAssetSymbol()).orElseThrow(),
						(req, field) -> req.setAssetSymbol(field.getSymbol()));

		binder.forField(typeField)
				.asRequired("Please fill this field")
				.bind(UpdateTransactionRequest::getType, UpdateTransactionRequest::setType);

		binder.forField(amountField)
				.asRequired("Please fill this field")
				.withConverter(new FlexibleAmountConvertor())
				.withValidator(new DoubleRangeValidator("Invalid decimal value", 0.0, Double.MAX_VALUE))
				.withValidator(amount -> amount > 0, "Amount should be bigger than 0")
				.bind(UpdateTransactionRequest::getOrderQuantity, UpdateTransactionRequest::setOrderQuantity);

		binder.forField(marketPriceField)
				.asRequired("Please fill this field")
				.withConverter(new FlexiblePriceConvertor())
				.withValidator(new DoubleRangeValidator("Invalid decimal value", 0.0, Double.MAX_VALUE))
				.withValidator(amount -> amount > 0, "Market price should be bigger than 0")
				.bind(UpdateTransactionRequest::getMarketPrice, UpdateTransactionRequest::setMarketPrice);

		binder.forField(totalCostField)
				.asRequired("Please fill this field")
				.withConverter(new FlexiblePriceConvertor())
				.withValidator(new DoubleRangeValidator("Invalid decimal value", 0.0, Double.MAX_VALUE))
				.withValidator(price -> price >= 1, "Total price should be at least one dollar");

		binder.forField(notesField)
				.bind(UpdateTransactionRequest::getNote, UpdateTransactionRequest::setNote);

		binder.forField(datePicker)
				.bind(UpdateTransactionRequest::getDateTime, UpdateTransactionRequest::setDateTime);
	}

	private void handleTransactionSave() {
		if (!binder.validate().isOk()) {
			showErrorNotification("Error! Please fill the fields correctly");
			return;
		}

		if (hasAssetChanged()) {
			ConfirmDialog confirmDialog = new ConfirmDialog();
			confirmDialog.setHeader("Warning: Changing asset for completed transaction");
			confirmDialog.setText("Modifying the asset for a completed transaction may cause calculation errors and compromise record accuracy. Proceed with this change?");
			confirmDialog.setConfirmText("Save");
			confirmDialog.setCancelable(true);
			confirmDialog.addConfirmListener(l -> saveTransaction());
			confirmDialog.open();
		} else {
			saveTransaction();
		}
	}

	private boolean hasAssetChanged() {
		return !Objects.equals(initialRequest.getAssetSymbol(), binder.getBean().getAssetSymbol());
	}

	private void saveTransaction() {
		try {
			TransactionDTO savedTransaction = instrumentsFacadeService.updateTransaction(binder.getBean());
			UI.getCurrent().access(() -> ComponentUtil.fireEvent(UI.getCurrent(), new TransactionCreatedOrUpdatedEvent(this, savedTransaction)));
			showSuccessfulNotification("The transaction was saved succesfully");
			this.close();
		} catch (Exception e) {
			showErrorNotification(e.getLocalizedMessage());
		}
	}

	public UpdateTransactionRequest getRequest() {
		return binder.getBean();
	}

	private void displayHintMarketPrice() {
		String formatedPrice = CommonFormatters.CURRENCY.format(assetSymbolField.getMarketPrice());
		marketPriceField.setHelperText("Current price: %s".formatted(formatedPrice));
	}

	private void displayHintAmountOfTokens() {
		double amountTokens = assetSymbolField.getAmountTokens(transaction.getPortfolioId());
		String formatedAmount = CommonFormatters.AMOUNT.format(amountTokens);

		String helperText = typeField.getOptionalValue()
				.map(t -> t.isBuyTransaction() ? null : "Currently you have %s %s".formatted(formatedAmount, assetSymbolField.getSymbol()))
				.orElse(null);

		amountField.setHelperText(helperText);
	}

}