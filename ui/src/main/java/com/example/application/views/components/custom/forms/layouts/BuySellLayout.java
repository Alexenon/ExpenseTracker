package com.example.application.views.components.custom.forms.layouts;

import com.example.application.finance.FinancialConstants;
import com.example.application.views.components.custom.fields.AmountField;
import com.example.application.views.components.custom.fields.MoneyField;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Component to gather the buy price, sell price, invested amount, and amount of tokens placed using input fields
 */
@Component
public class BuySellLayout extends Div {

	private final AmountField amountField = new AmountField("Amount");
	private final MoneyField buyPriceField = new MoneyField("Buy Price");
	private final MoneyField sellPriceField = new MoneyField("Sell Price");
	private final MoneyField totalCostField = new MoneyField("Total Cost");

	public BuySellLayout() {
		initializeFieldsListeners();
		add(amountField, buyPriceField, totalCostField, sellPriceField);
	}

	private void initializeFieldsListeners() {
		amountField.setValueChangeMode(ValueChangeMode.EAGER);
		amountField.addKeyUpListener(e -> {
			BigDecimal amount = amountField.getAmount();
			BigDecimal buyPrice = buyPriceField.getMoneyAmount();
			totalCostField.setValue(buyPrice.multiply(amount));
		});

		buyPriceField.setValueChangeMode(ValueChangeMode.EAGER);
		buyPriceField.addKeyUpListener(e -> {
			BigDecimal amount = amountField.getAmount();
			BigDecimal buyPrice = buyPriceField.getMoneyAmount();
			totalCostField.setValue(buyPrice.multiply(amount));
		});

		totalCostField.setValueChangeMode(ValueChangeMode.EAGER);
		totalCostField.addKeyUpListener(e -> {
			BigDecimal totalCost = totalCostField.getMoneyAmount();
			BigDecimal buyPrice = buyPriceField.getMoneyAmount();
			BigDecimal amountValue = buyPrice.signum() == 0
					? BigDecimal.ZERO
					: totalCost.divide(buyPrice, FinancialConstants.AMOUNT_SCALE, RoundingMode.HALF_UP);
			amountField.setValue(amountValue);
		});
	}

	public AmountField getAmountField() {
		return amountField;
	}

	public MoneyField getBuyPriceField() {
		return buyPriceField;
	}

	public MoneyField gettotalCostField() {
		return totalCostField;
	}

	public MoneyField getSellPriceField() {
		return sellPriceField;
	}
}
