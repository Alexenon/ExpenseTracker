package com.example.application.views.pages.crypto.comparator;

import com.example.application.entities.crypto.Asset;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.views.components.fields.AmountField;
import com.example.application.views.components.fields.CurrencyField;
import com.example.application.views.components.utils.convertors.FlexibleAmountConvertor;
import com.example.application.views.components.utils.convertors.FlexiblePriceConvertor;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AssetPriceAmountForm extends Div {

    private final InstrumentsFacadeService instrumentsFacadeService;

    private final ComboBox<Asset> assetSymbolField = new ComboBox<>("Asset");
    private final AmountField amountField = new AmountField("Amount");
    private final CurrencyField marketPriceField = new CurrencyField("Price");
    private final CurrencyField totalPriceField = new CurrencyField("Total");
    private final Binder<?> binder = new Binder<>();

    @Autowired
    public AssetPriceAmountForm(InstrumentsFacadeService instrumentsFacadeService) {
        this.instrumentsFacadeService = instrumentsFacadeService;
        buildPage();
    }

    private void buildPage() {
        initializeFields();
        initializeBinder();
        add(assetSymbolField, amountField, marketPriceField, totalPriceField);
    }

    private void initializeFields() {
        assetSymbolField.addValueChangeListener(l -> {
            binder.setValidatorsDisabled(false);
            marketPriceField.setValue(getMarketPriceBySelectedAsset());
            amountField.setSuffixComponent(new Span(getSelectedAssetSymbol()));
        });

        amountField.setValueChangeMode(ValueChangeMode.EAGER);
        amountField.addKeyUpListener(e -> {
            double totalPrice = amountField.doubleValue() * marketPriceField.doubleValue();
            totalPriceField.setValue(totalPrice);
            binder.validate();
        });

        marketPriceField.setValueChangeMode(ValueChangeMode.EAGER);
        marketPriceField.addKeyUpListener(e -> {
            double totalPrice = amountField.doubleValue() * marketPriceField.doubleValue();
            totalPriceField.setValue(totalPrice);
            binder.validate();
        });

        totalPriceField.setValueChangeMode(ValueChangeMode.EAGER);
        totalPriceField.addKeyUpListener(e -> {
            double amount = 0;
            if (marketPriceField.doubleValue() != 0) {
                String textPrice = totalPriceField.getValue().replaceAll(",", "");
                double totalPrice = Double.parseDouble(textPrice.isEmpty() ? "0" : textPrice);
                amount = totalPrice / marketPriceField.doubleValue();
            }

            amountField.setValue(amount);
            binder.validate();
        });

    }

    private void initializeBinder() {
        binder.forField(assetSymbolField)
                .asRequired("Please fill this field");

        binder.forField(amountField)
                .asRequired("Please fill this field")
                .withConverter(new FlexibleAmountConvertor())
                .withValidator(amount -> amount > 0, "Price should be bigger than 0");

        binder.forField(marketPriceField)
                .asRequired("Please fill this field")
                .withConverter(new FlexiblePriceConvertor())
                .withValidator(price -> price > 0, "Price should be bigger than 0");

        binder.forField(totalPriceField)
                .asRequired("Please fill this field")
                .withConverter(new FlexiblePriceConvertor())
                .withValidator(price -> price >= 1, "Total price should be at least one dollar");

    }

    private double getMarketPriceBySelectedAsset() {
        Asset selectedAsset = assetSymbolField.getValue();
        return selectedAsset == null ? 0 : instrumentsFacadeService.getAssetPrice(selectedAsset);

    }

    private String getSelectedAssetSymbol() {
        Asset selectedAsset = assetSymbolField.getValue();
        return selectedAsset == null ? "" : selectedAsset.getSymbol();
    }

    public ComboBox<Asset> getAssetSymbolField() {
        return assetSymbolField;
    }

    public AmountField getAmountField() {
        return amountField;
    }

    public CurrencyField getMarketPriceField() {
        return marketPriceField;
    }

    public CurrencyField getTotalPriceField() {
        return totalPriceField;
    }

    public Binder<?> getBinder() {
        return binder;
    }
}
