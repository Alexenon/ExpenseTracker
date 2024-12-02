package com.example.application.views.components.custom.forms.layouts;

import com.example.application.utils.common.MathUtils;
import com.example.application.views.components.custom.fields.AmountField;
import com.example.application.views.components.custom.fields.CurrencyField;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.springframework.stereotype.Component;

/**
 * Component to gather the buy price, sell price, invested amount, and amount of tokens placed using input fields
 * */
@Component
public class BuySellForm extends Div {

    private final AmountField amountField = new AmountField("Amount");
    private final CurrencyField buyPriceField = new CurrencyField("Buy Price");
    private final CurrencyField totalPriceField = new CurrencyField("Total");
    private final CurrencyField sellPriceField = new CurrencyField("Sell Price");

    public BuySellForm() {
        initializeFieldsListeners();
        add(amountField, buyPriceField, totalPriceField, sellPriceField);
    }

    private void initializeFieldsListeners() {
        amountField.setValueChangeMode(ValueChangeMode.EAGER);
        amountField.addKeyUpListener(e -> {
            double totalPrice = amountField.doubleValue() * buyPriceField.doubleValue();
            totalPriceField.setValue(totalPrice);
        });

        buyPriceField.setValueChangeMode(ValueChangeMode.EAGER);
        buyPriceField.addKeyUpListener(e -> {
            double totalPrice = amountField.doubleValue() * buyPriceField.doubleValue();
            totalPriceField.setValue(totalPrice);
        });

        totalPriceField.setValueChangeMode(ValueChangeMode.EAGER);
        totalPriceField.addKeyUpListener(e -> {
            double amountValue = MathUtils.safeZeroDivision(totalPriceField.doubleValue(), buyPriceField.doubleValue());
            amountField.setValue(amountValue);
        });
    }

    public AmountField getAmountField() {
        return amountField;
    }

    public CurrencyField getBuyPriceField() {
        return buyPriceField;
    }

    public CurrencyField getTotalPriceField() {
        return totalPriceField;
    }

    public CurrencyField getSellPriceField() {
        return sellPriceField;
    }
}
