package com.example.application.views.components.custom.dialogs.transactions;

import com.example.application.entities.common.TransactionType;
import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.Portfolio;
import com.example.application.entities.crypto.Transaction;
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
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.DoubleRangeValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.Objects;

/*
	TODO: [LONG TERM]
		- Add slider for percentage buy/transfer (ideally should be revisted this form design,
			with a more complex solution -> sliders, % and $, deposit...)
* */
public class AddTransactionDialog extends Dialog implements HasNotifications {

	private final Asset asset;
	private final Portfolio portfolio;
	private final Transaction transaction;
	private final InstrumentsFacadeService instrumentsFacadeService;
	private final Binder<Transaction> binder = new Binder<>(Transaction.class);

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

	public AddTransactionDialog(Portfolio portfolio, InstrumentsFacadeService instrumentsFacadeService) {
		this(portfolio, null, instrumentsFacadeService);
	}

	@Autowired
	public AddTransactionDialog(@NotNull Portfolio portfolio,
								@Nullable Asset asset,
								@NotNull InstrumentsFacadeService instrumentsFacadeService)
	{
		this.portfolio = Objects.requireNonNull(portfolio, "portfolio");
		this.asset = asset;
		this.instrumentsFacadeService = Objects.requireNonNull(instrumentsFacadeService, "instrumentsFacadeService");
		this.assetSymbolField = new AssetComboBox(instrumentsFacadeService);
		this.transaction = defaultTransaction();
		buildForm();
	}

	private void buildForm() {
		setHeaderTitle("Add Transaction");
		initializeBinder();
		initializeFields();
		initializeFieldValues();
		initializeFieldListeners();

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
	}

	private void initializeFieldValues() {
		assetSymbolField.setValue(asset);
		typeField.setValue(TransactionType.BUY);
		amountField.setValue("");
		marketPriceField.setValue(assetSymbolField.getMarketPrice());
		totalCostField.setValue(0);
		datePicker.setValue(LocalDateTime.now());
	}

	private void initializeFieldListeners() {
		assetSymbolField.addValueChangeListener(l -> {
			binder.setValidatorsDisabled(false);
			marketPriceField.setValue(assetSymbolField.getMarketPrice());
			symbolSuffix.setText(assetSymbolField.getSymbol());
			displayHintMarketPrice();
			displayHintAmountOfTokens();
		});

		typeField.addValueChangeListener(l -> displayHintAmountOfTokens());

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
		saveButton.addClickListener(e -> {
			if (binder.validate().isOk()) {
				Transaction savedTransaction = instrumentsFacadeService.saveTransaction(binder.getBean());
				showSuccessfulNotification("The transaction was saved succesfully");
				UI.getCurrent().access(() -> ComponentUtil.fireEvent(UI.getCurrent(), new TransactionCreatedOrUpdatedEvent(this, savedTransaction)));
				this.close();
			} else {
				showErrorNotification("Error! Please fill the fields with as required");
			}
		});

		cancelButton.addClickShortcut(Key.ESCAPE);
		cancelButton.addClickListener(e -> this.close());
	}

	private void initializeBinder() {
		binder.setBean(transaction);

		binder.forField(assetSymbolField)
				.asRequired("Please fill this field")
				.bind(Transaction::getAsset, Transaction::setAsset);

		binder.forField(typeField)
				.asRequired("Please fill this field")
				.bind(Transaction::getType, Transaction::setType);

		binder.forField(amountField)
				.asRequired("Please fill this field")
				.withConverter(new FlexibleAmountConvertor())
				.withValidator(new DoubleRangeValidator("Invalid decimal value", 0.0, Double.MAX_VALUE))
				.withValidator(amount -> amount > 0, "Amount should be bigger than 0")
				.bind(Transaction::getOrderQuantity, Transaction::setOrderQuantity);

		binder.forField(marketPriceField)
				.asRequired("Please fill this field")
				.withConverter(new FlexiblePriceConvertor())
				.withValidator(new DoubleRangeValidator("Invalid decimal value", 0.0, Double.MAX_VALUE))
				.withValidator(price -> price > 0, "Market price should be bigger than 0")
				.bind(Transaction::getMarketPrice, Transaction::setMarketPrice);

		binder.forField(totalCostField)
				.asRequired("Please fill this field")
				.withConverter(new FlexiblePriceConvertor())
				.withValidator(new DoubleRangeValidator("Invalid decimal value", 0.0, Double.MAX_VALUE))
				.withValidator(price -> price >= 1, "Total price should be at least one dollar")
				.bind(Transaction::getOrderTotalCost, Transaction::setOrderTotalCost);

		binder.forField(notesField)
				.bind(Transaction::getNote, Transaction::setNote);

		binder.forField(datePicker)
				.asRequired("Please fill this field")
				.bind(Transaction::getDateTime, Transaction::setDateTime);
	}

	private void displayHintMarketPrice() {
		String formatedPrice = CommonFormatters.CURRENCY.format(assetSymbolField.getMarketPrice());
		marketPriceField.setHelperText("Current price: %s".formatted(formatedPrice));
	}

	private void displayHintAmountOfTokens() {
		double amountTokens = assetSymbolField.getAmountTokens(portfolio);
		String formatedAmount = CommonFormatters.AMOUNT.format(amountTokens);
		String helperText = typeField.getValue().isBuyTransaction()
				? null
				: "Currently you have %s %s".formatted(formatedAmount, assetSymbolField.getSymbol());

		amountField.setHelperText(helperText);
	}

	private Transaction defaultTransaction() {
		Transaction newTransaction = new Transaction();
		newTransaction.setPortfolio(portfolio);
		newTransaction.setAsset(asset);
		return newTransaction;
	}

}