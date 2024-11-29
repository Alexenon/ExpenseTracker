//package com.example.application.views.components.complex_components.fields;
//
//import com.example.application.entities.crypto.Asset;
//import com.example.application.services.crypto.InstrumentsFacadeService;
//import com.example.application.utils.common.MathUtils;
//import com.example.application.views.components.fields.AmountField;
//import com.example.application.views.components.fields.AssetComboBox;
//import com.example.application.views.components.fields.CurrencyField;
//import com.vaadin.flow.component.combobox.ComboBox;
//import com.vaadin.flow.component.formlayout.FormLayout;
//import com.vaadin.flow.component.html.Div;
//import com.vaadin.flow.component.html.Span;
//import com.vaadin.flow.data.value.ValueChangeMode;
//import com.vaadin.flow.router.BeforeEnterEvent;
//import com.vaadin.flow.router.BeforeEnterObserver;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
///**
// * Component to gather the buy price, sell price, invested amount, and amount of tokens placed using input fields
// */
//@Component
//public class BuySellProfitForm extends FormLayout implements BeforeEnterObserver {
//
//    @Autowired
//    private InstrumentsFacadeService instrumentsFacadeService;
//
//    private AssetComboBox assetSymbolField;
//    private final AmountField amountField = new AmountField("Amount");
//    private final CurrencyField buyPriceField = new CurrencyField("Buy Price");
//    private final CurrencyField totalPriceField = new CurrencyField("Total");
//    private final CurrencyField sellPriceField = new CurrencyField("Sell Price");
//
//    @Override
//    public void beforeEnter(BeforeEnterEvent event) {
//        assetSymbolField = new AssetComboBox(instrumentsFacadeService);
//        buildForm();
//    }
//
//    private void buildForm() {
//        initializeFieldsValues();
//        initializeFieldsListeners();
//    }
//
//    private void initializeFieldsValues() {
//
//        assetSymbolField.addValueChangeListener(l -> buyPriceField.setValue(getAssetMarketPrice(assetSymbolField)));
//
//        amountField.setValue("");
//        // TODO: Add average buy price here
//        buyPriceField.setValue(getAssetMarketPrice(assetSymbolField));
//        totalPriceField.setValue(0);
//        // TODO: Add current asset price
//        sellPriceField.setValue("");
//    }
//
//    private void initializeFieldsListeners() {
//        assetSymbolField.addValueChangeListener(l -> {
//            amountField.setValue(getAmountOfTokens(assetSymbolField.getValue()));
//            amountField.setSuffixComponent(new Span(getSelectedAssetSymbol(assetSymbolField)));
//            buyPriceField.setValue(getAssetMarketPrice(assetSymbolField));
//            totalPriceField.setValue(amountField.doubleValue() * buyPriceField.doubleValue());
//        });
//
//        amountField.setValueChangeMode(ValueChangeMode.EAGER);
//        amountField.addKeyUpListener(e -> {
//            double totalPrice = amountField.doubleValue() * buyPriceField.doubleValue();
//            totalPriceField.setValue(totalPrice);
//        });
//
//        buyPriceField.setValueChangeMode(ValueChangeMode.EAGER);
//        buyPriceField.addKeyUpListener(e -> {
//            double totalPrice = amountField.doubleValue() * buyPriceField.doubleValue();
//            totalPriceField.setValue(totalPrice);
//        });
//
//        totalPriceField.setValueChangeMode(ValueChangeMode.EAGER);
//        totalPriceField.addKeyUpListener(e -> {
//            double amountValue = MathUtils.safeZeroDivision(totalPriceField.doubleValue(), buyPriceField.doubleValue());
//            amountField.setValue(amountValue);
//        });
//    }
//
//    protected Div createInputFieldsContainer() {
//        return new Div(assetSymbolField, amountField, buyPriceField, totalPriceField, sellPriceField);
//    }
//
//    protected double getAssetMarketPrice(ComboBox<Asset> assetSymbolField) {
//        Asset selectedAsset = assetSymbolField.getValue();
//        if (selectedAsset == null) {
//            return 0;
//        } else {
//            return instrumentsFacadeService.getAssetPrice(selectedAsset);
//        }
//    }
//
//}
