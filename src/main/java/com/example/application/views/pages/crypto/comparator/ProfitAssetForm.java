package com.example.application.views.pages.crypto.comparator;

import com.example.application.entities.crypto.Asset;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.views.components.fields.AmountField;
import com.example.application.views.components.fields.CurrencyField;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProfitAssetForm extends Div {

    private final InstrumentsFacadeService instrumentsFacadeService;

    private final ComboBox<Asset> assetSymbolField = new ComboBox<>("Asset");
    private final AmountField amountField = new AmountField("Amount");
    private final CurrencyField buyPriceField = new CurrencyField("Buy Price");
    private final CurrencyField totalPriceField = new CurrencyField("Total");
    private final CurrencyField sellPriceField = new CurrencyField("Sell Price");
    private final Binder<?> binder = new Binder<>();

    @Autowired
    public ProfitAssetForm(InstrumentsFacadeService instrumentsFacadeService) {
        this.instrumentsFacadeService = instrumentsFacadeService;
        buildForm();
    }

    private void buildForm() {
        //initializeBinder();
        initializeFieldsValues();
        initializeFieldsListeners();
        add(assetSymbolField, amountField, buyPriceField, totalPriceField, sellPriceField);
    }

    private void initializeFieldsValues() {
        assetSymbolField.setItems(instrumentsFacadeService.getAllAssets());
        assetSymbolField.setItemLabelGenerator(instrumentsFacadeService::getAssetFullName);
        assetSymbolField.setRenderer(assetSymbolRenderer());
        assetSymbolField.addValueChangeListener(l -> buyPriceField.setValue(getMarketPriceBySelectedAsset()));

        amountField.setValue("");
        buyPriceField.setValue(getMarketPriceBySelectedAsset());
        totalPriceField.setValue(0);
        sellPriceField.setValue("");
    }

    private void initializeFieldsListeners() {
        assetSymbolField.addValueChangeListener(l -> {
            binder.setValidatorsDisabled(false);
            buyPriceField.setValue(getMarketPriceBySelectedAsset());
            amountField.setSuffixComponent(new Span(getSelectedAssetSymbol()));
        });

        amountField.setValueChangeMode(ValueChangeMode.EAGER);
        amountField.addKeyUpListener(e -> {
            double totalPrice = amountField.doubleValue() * buyPriceField.doubleValue();
            totalPriceField.setValue(totalPrice);
            binder.validate();
        });

        buyPriceField.setValueChangeMode(ValueChangeMode.EAGER);
        buyPriceField.addKeyUpListener(e -> {
            double totalPrice = amountField.doubleValue() * buyPriceField.doubleValue();
            totalPriceField.setValue(totalPrice);
            binder.validate();
        });

        totalPriceField.setValueChangeMode(ValueChangeMode.EAGER);
        totalPriceField.addKeyUpListener(e -> {
            double amount = 0;
            if (buyPriceField.doubleValue() != 0) {
                String textPrice = totalPriceField.getValue().replaceAll(",", "");
                double totalPrice = Double.parseDouble(textPrice.isEmpty() ? "0" : textPrice);
                amount = totalPrice / buyPriceField.doubleValue();
            }

            amountField.setValue(amount);
            binder.validate();
        });
    }

    private LitRenderer<Asset> assetSymbolRenderer() {
        return LitRenderer.<Asset>of(
                        "<div class='coin-overview-name-container'>" +
                        "  <img class='rounded coin-overview-image' src='${item.imgUrl}' alt='${item.fullName}'/>" +
                        "  <span>${item.symbol}</span>" +
                        "  <p>${item.fullName}</p>" +
                        "</div>")
                .withProperty("imgUrl", instrumentsFacadeService::getAssetImgUrl)
                .withProperty("symbol", Asset::getSymbol)
                .withProperty("fullName", instrumentsFacadeService::getAssetFullName);
    }

    private double getMarketPriceBySelectedAsset() {
        Asset selectedAsset = assetSymbolField.getValue();
        return selectedAsset == null ? 0 : instrumentsFacadeService.getAssetPrice(selectedAsset);

    }

    public String getSelectedAssetSymbol() {
        Asset selectedAsset = assetSymbolField.getValue();
        return selectedAsset == null ? "" : selectedAsset.getSymbol();
    }

    public double getAmount() {
        return amountField.doubleValue();
    }

    public double getBuyPrice() {
        return buyPriceField.doubleValue();
    }

    public double getTotalPrice() {
        return totalPriceField.doubleValue();
    }

    public double getSellPrice() {
        return sellPriceField.doubleValue();
    }
    
    public Binder<?> getBinder() {
        return binder;
    }
}
