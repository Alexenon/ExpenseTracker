package com.example.application.views.components.custom.forms.layouts;

import com.example.application.data.dtos.AssetDTO;
import com.example.application.data.dtos.PortfolioDTO;
import com.example.application.data.models.crypto.common.TransactionInput;
import com.example.application.entities.common.TransactionType;
import com.example.application.finance.FinancialConstants;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.views.components.custom.fields.AmountField;
import com.example.application.views.components.custom.fields.MoneyField;
import com.example.application.views.components.utils.convertors.FlexibleAmountConvertor;
import com.example.application.views.components.utils.convertors.FlexiblePriceConvertor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.BigDecimalRangeValidator;
import com.vaadin.flow.data.value.ValueChangeMode;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class TransactionalLayout extends Div {

	private final PortfolioDTO portfolio;
	private final InstrumentsFacadeService instrumentsFacadeService;

	private final AmountField amountField = new AmountField("Amount");
	private final MoneyField marketPriceField = new MoneyField("Market Price");
	private final MoneyField totalCostField = new MoneyField("Total");
	private final Select<TransactionType> typeField = new Select<>();

	private final TransactionInput transaction = new TransactionInput();
	private final Binder<TransactionInput> binder = new Binder<>(TransactionInput.class);

	public TransactionalLayout(PortfolioDTO portfolio,
							   InstrumentsFacadeService instrumentsFacadeService)
	{
		this.portfolio = portfolio;
		this.instrumentsFacadeService = instrumentsFacadeService;
		initializeBinder();
		initializeFields();
		initializeFieldsValues();
		initializeFieldsListeners();
	}

	private void initializeFields() {
		typeField.setLabel("Transaction Type");
		typeField.setItems(TransactionType.values());

		add(amountField, marketPriceField, totalCostField, typeField);
	}

	private void initializeFieldsValues() {
		typeField.setValue(TransactionType.BUY);
	}

	private void initializeFieldsListeners() {
		amountField.setValueChangeMode(ValueChangeMode.EAGER);
		amountField.addKeyUpListener(e -> {
			BigDecimal amount = amountField.getAmount();
			BigDecimal marketPrice = marketPriceField.getMoneyAmount();
			totalCostField.setValue(marketPrice.multiply(amount));
		});

		marketPriceField.setValueChangeMode(ValueChangeMode.EAGER);
		marketPriceField.addKeyUpListener(e -> {
			BigDecimal amount = amountField.getAmount();
			BigDecimal marketPrice = marketPriceField.getMoneyAmount();
			totalCostField.setValue(marketPrice.multiply(amount));
		});

		totalCostField.setValueChangeMode(ValueChangeMode.EAGER);
		totalCostField.addKeyUpListener(e -> {
			BigDecimal totalCost = totalCostField.getMoneyAmount();
			BigDecimal marketPrice = marketPriceField.getMoneyAmount();
			BigDecimal amountTokens = marketPrice.signum() == 0
					? BigDecimal.ZERO
					: totalCost.divide(marketPrice, FinancialConstants.AMOUNT_SCALE, RoundingMode.HALF_UP);
			amountField.setValue(amountTokens);
		});
	}

	private void initializeBinder() {
		binder.setBean(transaction);

		binder.forField(typeField)
				.asRequired("Please fill this field")
				.bind(TransactionInput::getType, TransactionInput::setType);

		binder.forField(amountField)
				.asRequired("Please fill this field")
				.withConverter(new FlexibleAmountConvertor())
				.withValidator(new BigDecimalRangeValidator("Invalid decimal value", BigDecimal.ZERO, BigDecimal.valueOf(Integer.MAX_VALUE)))
				.withValidator(amount -> amount.signum() > 0, "Amount should be bigger than 0")
				.bind(TransactionInput::getAmount, TransactionInput::setAmount);

		binder.forField(marketPriceField)
				.asRequired("Please fill this field")
				.withConverter(new FlexiblePriceConvertor())
				.withValidator(new BigDecimalRangeValidator("Invalid decimal value", BigDecimal.ZERO, BigDecimal.valueOf(Integer.MAX_VALUE)))
				.withValidator(price -> price.signum() > 0, "Market price should be bigger than 0")
				.bind(TransactionInput::getPrice, TransactionInput::setPrice);
	}

	public void setAsset(AssetDTO asset) {
		if (asset == null) {
			amountField.setValue("");
			marketPriceField.setValue("");
			totalCostField.setValue("");
			return;
		}

		BigDecimal amountOfTokens = instrumentsFacadeService.getAmountOfTokens(portfolio.getId(), asset.getSymbol());
		amountField.setValue(amountOfTokens);
		marketPriceField.setValue(asset.getMarketPrice());
		totalCostField.setValue(amountOfTokens.multiply(marketPriceField.getMoneyAmount()));
	}

	public TransactionInput getTransactionInput() {
		return binder.getBean();
	}

	public boolean isValid() {
		return binder.validate().isOk();
	}

}