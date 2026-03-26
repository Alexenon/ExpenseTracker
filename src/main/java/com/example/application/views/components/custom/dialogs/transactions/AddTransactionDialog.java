package com.example.application.views.components.custom.dialogs.transactions;

import com.example.application.data.dtos.AssetDTO;
import com.example.application.data.dtos.PortfolioDTO;
import com.example.application.data.dtos.TransactionDTO;
import com.example.application.data.requests.CreateTransactionRequest;
import com.example.application.entities.common.TransactionType;
import com.example.application.finance.FinancialConstants;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.utils.common.formatters.CommonFormatters;
import com.example.application.views.components.core.Container;
import com.example.application.views.components.custom.fields.AmountField;
import com.example.application.views.components.custom.fields.AssetComboBox;
import com.example.application.views.components.custom.fields.MoneyField;
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
import com.vaadin.flow.data.validator.BigDecimalRangeValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;

/*
	TODO: [LONG TERM]
		- Add slider for percentage buy/transfer (ideally should be revisted this form design,
			with a more complex solution -> sliders, % and $, deposit...)
* */
public class AddTransactionDialog extends Dialog implements HasNotifications {

	private final AssetDTO asset;
	private final PortfolioDTO portfolio;
	private final InstrumentsFacadeService instrumentsFacadeService;
	private final Binder<CreateTransactionRequest> binder = new Binder<>(CreateTransactionRequest.class);

	private final AssetComboBox assetSymbolField;
	private final Select<TransactionType> typeField = new Select<>();
	private final AmountField amountField = new AmountField("Amount");
	private final MoneyField marketPriceField = new MoneyField("Price");
	private final MoneyField totalCostField = new MoneyField("Total");
	private final DateTimePicker datePicker = new DateTimePicker("Date & Time");
	private final TextArea notesField = new TextArea("Notes");

	private final Button saveButton = new Button("Save");
	private final Button cancelButton = new Button("Cancel");
	private final Span symbolSuffix = new Span();

	public AddTransactionDialog(PortfolioDTO portfolio, InstrumentsFacadeService instrumentsFacadeService) {
		this(portfolio, null, instrumentsFacadeService);
	}

	@Autowired
	public AddTransactionDialog(@NotNull PortfolioDTO portfolio,
								@Nullable AssetDTO asset,
								@NotNull InstrumentsFacadeService instrumentsFacadeService)
	{
		this.portfolio = Objects.requireNonNull(portfolio, "portfolio");
		this.asset = asset;
		this.instrumentsFacadeService = Objects.requireNonNull(instrumentsFacadeService, "instrumentsFacadeService");
		this.assetSymbolField = new AssetComboBox(instrumentsFacadeService);
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
		totalCostField.setValue("0");
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
			BigDecimal totalPrice = amountField.getAmount().multiply(marketPriceField.getMoneyAmount());
			totalCostField.setValue(totalPrice);
			binder.validate();
		});

		marketPriceField.setValueChangeMode(ValueChangeMode.EAGER);
		marketPriceField.addKeyUpListener(e -> {
			BigDecimal totalPrice = amountField.getAmount().multiply(marketPriceField.getMoneyAmount());
			totalCostField.setValue(totalPrice);
			binder.validate();
		});

		totalCostField.setValueChangeMode(ValueChangeMode.EAGER);
		totalCostField.addKeyUpListener(e -> {
			BigDecimal marketPrice = marketPriceField.getMoneyAmount();
			BigDecimal totalCost = totalCostField.getMoneyAmount();
			BigDecimal amount = marketPrice.compareTo(BigDecimal.ZERO) == 0
					? BigDecimal.ZERO
					: totalCost.divide(marketPrice, FinancialConstants.AMOUNT_SCALE, RoundingMode.HALF_UP);

			amountField.setValue(amount);
			binder.validate();
		});

		saveButton.addClickShortcut(Key.ENTER);
		saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
		saveButton.addClickListener(e -> {
			if (binder.validate().isOk()) {
				TransactionDTO savedTransaction = instrumentsFacadeService.createTransaction(binder.getBean());
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
		binder.setBean(defaultRequest());

		binder.forField(assetSymbolField)
				.asRequired("Please fill this field")
				.bind(req -> req.getAssetSymbol() == null ? null : instrumentsFacadeService.getAssetBySymbol(req.getAssetSymbol()).orElseThrow(),
						(req, asset) -> req.setAssetSymbol(asset == null ? null : asset.getSymbol()));

		binder.forField(typeField)
				.asRequired("Please fill this field")
				.bind(CreateTransactionRequest::getType, CreateTransactionRequest::setType);

		binder.forField(amountField)
				.asRequired("Please fill this field")
				.withConverter(new FlexibleAmountConvertor())
				.withValidator(new BigDecimalRangeValidator("Invalid decimal value", BigDecimal.ZERO, BigDecimal.valueOf(Integer.MAX_VALUE)))
				.withValidator(amount -> amount.signum() > 0, "Amount should be bigger than 0")
				.bind(CreateTransactionRequest::getOrderQuantity, CreateTransactionRequest::setOrderQuantity);

		binder.forField(marketPriceField)
				.asRequired("Please fill this field")
				.withConverter(new FlexiblePriceConvertor())
				.withValidator(new BigDecimalRangeValidator("Invalid decimal value", BigDecimal.ZERO, BigDecimal.valueOf(Integer.MAX_VALUE)))
				.withValidator(price -> price.signum() > 0, "Market price should be bigger than 0")
				.bind(CreateTransactionRequest::getMarketPrice, CreateTransactionRequest::setMarketPrice);

		binder.forField(totalCostField)
				.asRequired("Please fill this field")
				.withConverter(new FlexiblePriceConvertor())
				.withValidator(new BigDecimalRangeValidator("Invalid decimal value", BigDecimal.ZERO, BigDecimal.valueOf(Integer.MAX_VALUE)))
				.withValidator(price -> price.signum() > 0, "Total cost should be bigger than 0");

		binder.forField(notesField)
				.withValidator(s -> s.length() < 255, "Notes lenght cannot be bigger than 255 characters")
				.bind(CreateTransactionRequest::getNote, CreateTransactionRequest::setNote);

		binder.forField(datePicker)
				.asRequired("Please fill this field")
				.bind(CreateTransactionRequest::getDateTime, CreateTransactionRequest::setDateTime);
	}

	private void displayHintMarketPrice() {
		String formatedPrice = CommonFormatters.CURRENCY.format(assetSymbolField.getMarketPrice());
		marketPriceField.setHelperText("Current price: %s".formatted(formatedPrice));
	}

	private void displayHintAmountOfTokens() {
		BigDecimal amountTokens = assetSymbolField.getAmountTokens(portfolio.getId());
		String formatedAmount = CommonFormatters.AMOUNT.format(amountTokens);
		String helperText = typeField.getValue().isBuyTransaction()
				? null
				: "Currently you have %s %s".formatted(formatedAmount, assetSymbolField.getSymbol());

		amountField.setHelperText(helperText);
	}

	private CreateTransactionRequest defaultRequest() {
		CreateTransactionRequest newTransaction = new CreateTransactionRequest();
		newTransaction.setPortfolioId(portfolio.getId());
		newTransaction.setAssetSymbol(asset == null ? null : asset.getSymbol());
		return newTransaction;
	}

}