package com.example.application.views.components.custom.forms.layouts;

import com.example.application.data.dtos.AssetDTO;
import com.example.application.data.dtos.PortfolioDTO;
import com.example.application.entities.common.TransactionType;
import com.example.application.finance.FinancialConstants;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.services.crypto.PortfolioPerformanceTracker;
import com.example.application.views.components.custom.fields.AmountField;
import com.example.application.views.components.custom.fields.MoneyField;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.value.ValueChangeMode;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class TransactionalLayout extends Div {

	private final PortfolioDTO portfolio;
	private final InstrumentsFacadeService instrumentsFacadeService;
	private final PortfolioPerformanceTracker portfolioPerformanceTracker;

	private final AmountField amountField = new AmountField("Amount");
	private final MoneyField marketPriceField = new MoneyField("Market Price");
	private final MoneyField totalCostField = new MoneyField("Total");
	private final Select<TransactionType> typeField = new Select<>();

	public TransactionalLayout(PortfolioDTO portfolio,
							   InstrumentsFacadeService instrumentsFacadeService,
							   PortfolioPerformanceTracker portfolioPerformanceTracker)
	{
		this.portfolio = portfolio;
		this.instrumentsFacadeService = instrumentsFacadeService;
		this.portfolioPerformanceTracker = portfolioPerformanceTracker;
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
			BigDecimal amountTokens = totalCost.divide(marketPrice, FinancialConstants.AMOUNT_SCALE, RoundingMode.HALF_UP);
			amountField.setValue(amountTokens);
		});
	}

	public void setValue(AssetDTO asset) {
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

	public Select<TransactionType> getTypeField() {
		return typeField;
	}

	public AmountField getAmountField() {
		return amountField;
	}

	public MoneyField getMarketPriceField() {
		return marketPriceField;
	}

	public MoneyField getTotalCostField() {
		return totalCostField;
	}

}