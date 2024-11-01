package com.example.application.views.pages.crypto.comparator.form;

import com.example.application.entities.crypto.Asset;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.utils.common.MathUtils;
import com.example.application.views.components.fields.AmountField;
import com.example.application.views.components.fields.CurrencyField;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.springframework.beans.factory.annotation.Autowired;

public class ProfitStakingForm extends Div {

    private final InstrumentsFacadeService instrumentsFacadeService;

    private final ComboBox<Asset> assetSymbolField = new ComboBox<>("Asset");
    private final AmountField amountField = new AmountField("Amount of tokens");
    private final CurrencyField worthField = new CurrencyField("Total worth of asset");
    private final AmountField aprField = new AmountField("APR");

    private double assetMarketPrice;

    @Autowired
    public ProfitStakingForm(InstrumentsFacadeService instrumentsFacadeService) {
        this.instrumentsFacadeService = instrumentsFacadeService;
        buildForm();
    }

    private void buildForm() {
        initializeFieldsValues();
        initializeFieldsListeners();
        add(assetSymbolField, amountField, worthField, aprField);
    }

    private void initializeFieldsValues() {
        assetSymbolField.setItems(instrumentsFacadeService.getAllAssets());
        assetSymbolField.setItemLabelGenerator(instrumentsFacadeService::getAssetFullName);
        assetSymbolField.setRenderer(assetSymbolRenderer());
        assetSymbolField.addValueChangeListener(l -> {
            updateAssetMarketPrice();
            amountField.setValue(instrumentsFacadeService.getAmountOfTokens(l.getValue()));
            worthField.setValue(amountField.doubleValue() * assetMarketPrice);
        });

        aprField.setValue(1);
        //worthField.setValue(0);
    }

    private void initializeFieldsListeners() {
        assetSymbolField.addValueChangeListener(e -> {
            Asset selectedAsset = e.getValue();
            if (selectedAsset == null) {
                return;
            }

            amountField.setValue(instrumentsFacadeService.getAmountOfTokens(selectedAsset));
        });

        amountField.setValueChangeMode(ValueChangeMode.EAGER);
        amountField.addKeyUpListener(e -> worthField.setValue(amountField.doubleValue() * assetMarketPrice));

        worthField.setValueChangeMode(ValueChangeMode.EAGER);
        worthField.addKeyUpListener(e -> {
            double amountOfTokens = MathUtils.safeZeroDivision(worthField.doubleValue(), assetMarketPrice);
            amountField.setValue(amountOfTokens);
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

    private void updateAssetMarketPrice() {
        Asset selectedAsset = assetSymbolField.getValue();
        if (selectedAsset == null) {
            assetMarketPrice = 0;
        } else {
            assetMarketPrice = instrumentsFacadeService.getAssetPrice(selectedAsset);
        }
    }

    public String getSelectedAssetSymbol() {
        Asset selectedAsset = assetSymbolField.getValue();
        return selectedAsset == null ? "" : selectedAsset.getSymbol();
    }

    public double getApr() {
        return aprField.doubleValue();
    }

    public double getStakingWorth() {
        return worthField.doubleValue();
    }
}
