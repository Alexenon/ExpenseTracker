package com.example.application.views.components.custom.forms.layouts;

import com.example.application.entities.crypto.Asset;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.utils.common.MathUtils;
import com.example.application.views.components.custom.fields.AmountField;
import com.example.application.views.components.custom.fields.AssetComboBox;
import com.example.application.views.components.custom.fields.CurrencyField;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/*
 * TODO: Understand which one to use AssetPricingLayout or BuySellForm
 *   - if this one, then refactor -> remove last method
 * */

/**
 * Component to gather the buy/sell price, invested and tokens amount, placed using input fields
 */
@Component
public class AssetPricingLayout extends Div implements BeforeEnterObserver {

    @Autowired
    private InstrumentsFacadeService instrumentsFacadeService;

    @Autowired
    private AssetComboBox assetSymbolField;

    private final AmountField amountField = new AmountField("Amount");
    private final CurrencyField buyPriceField = new CurrencyField("Buy Price");
    private final CurrencyField totalCostField = new CurrencyField("Total");
    private final CurrencyField sellPriceField = new CurrencyField("Sell Price");

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        buildForm();
    }

    private void buildForm() {
        initializeFieldsValues();
        initializeFieldsListeners();
    }

    private void initializeFieldsValues() {
        assetSymbolField.addValueChangeListener(l -> buyPriceField.setValue(getAssetMarketPrice(assetSymbolField)));

        amountField.setValue("");
        // TODO: Add average buy price here
        buyPriceField.setValue(getAssetMarketPrice(assetSymbolField));
        totalCostField.setValue(0);
        // TODO: Add current asset price
        sellPriceField.setValue("");
    }

    private void initializeFieldsListeners() {
        assetSymbolField.addValueChangeListener(l -> {
            amountField.setValue(assetSymbolField.getAmountTokens());
            amountField.setSuffixComponent(new Span(assetSymbolField.getSymbol()));
            buyPriceField.setValue(getAssetMarketPrice(assetSymbolField));
            totalCostField.setValue(amountField.doubleValue() * buyPriceField.doubleValue());
        });

        amountField.setValueChangeMode(ValueChangeMode.EAGER);
        amountField.addKeyUpListener(e -> {
            double totalPrice = amountField.doubleValue() * buyPriceField.doubleValue();
            totalCostField.setValue(totalPrice);
        });

        buyPriceField.setValueChangeMode(ValueChangeMode.EAGER);
        buyPriceField.addKeyUpListener(e -> {
            double totalPrice = amountField.doubleValue() * buyPriceField.doubleValue();
            totalCostField.setValue(totalPrice);
        });

        totalCostField.setValueChangeMode(ValueChangeMode.EAGER);
        totalCostField.addKeyUpListener(e -> {
            double amountValue = MathUtils.safeZeroDivision(totalCostField.doubleValue(), buyPriceField.doubleValue());
            amountField.setValue(amountValue);
        });
    }

    protected Div createInputFieldsContainer() {
        return new Div(assetSymbolField, amountField, buyPriceField, totalCostField, sellPriceField);
    }

    protected double getAssetMarketPrice(ComboBox<Asset> assetSymbolField) {
        Asset selectedAsset = assetSymbolField.getValue();
        if (selectedAsset == null) {
            return 0;
        } else {
            return instrumentsFacadeService.getAssetMarketPrice(selectedAsset);
        }
    }

}
