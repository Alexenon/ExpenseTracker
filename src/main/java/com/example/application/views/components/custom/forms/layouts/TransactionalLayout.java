package com.example.application.views.components.custom.forms.layouts;

import com.example.application.data.dtos.AssetDTO;
import com.example.application.data.dtos.PortfolioDTO;
import com.example.application.entities.common.TransactionType;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.services.crypto.PortfolioPerformanceTracker;
import com.example.application.utils.common.lang.MathUtils;
import com.example.application.views.components.custom.fields.AmountField;
import com.example.application.views.components.custom.fields.CurrencyField;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.value.ValueChangeMode;

public class TransactionalLayout extends Div {

    private final PortfolioDTO portfolio;
    private final InstrumentsFacadeService instrumentsFacadeService;
    private final PortfolioPerformanceTracker portfolioPerformanceTracker;

    private final AmountField amountField = new AmountField("Amount");
    private final CurrencyField marketPriceField = new CurrencyField("Market Price");
    private final CurrencyField totalCostField = new CurrencyField("Total");
    private final Select<TransactionType> typeField = new Select<>();

    public TransactionalLayout(PortfolioDTO portfolio,
                               InstrumentsFacadeService instrumentsFacadeService,
                               PortfolioPerformanceTracker portfolioPerformanceTracker) {
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

        add(amountField , marketPriceField, totalCostField, typeField);
    }

    private void initializeFieldsValues() {
        typeField.setValue(TransactionType.BUY);
    }

    private void initializeFieldsListeners() {
        amountField.setValueChangeMode(ValueChangeMode.EAGER);
        amountField.addKeyUpListener(e -> {
            double totalCost = amountField.doubleValue() * marketPriceField.doubleValue();
            totalCostField.setValue(totalCost);
        });

        marketPriceField.setValueChangeMode(ValueChangeMode.EAGER);
        marketPriceField.addKeyUpListener(e -> {
            double totalCost = amountField.doubleValue() * marketPriceField.doubleValue();
            totalCostField.setValue(totalCost);
        });

        totalCostField.setValueChangeMode(ValueChangeMode.EAGER);
        totalCostField.addKeyUpListener(e -> {
            double amountTokens = MathUtils.safeDivision(totalCostField.doubleValue(), marketPriceField.doubleValue());
            amountField.setValue(amountTokens);
        });
    }

    public void setValue(AssetDTO asset) {
        if(asset == null) {
            amountField.setValue("");
            marketPriceField.setValue("");
            totalCostField.setValue("");
            return;
        }

        double amountOfTokens = instrumentsFacadeService.getAmountOfTokens(portfolio.getId(), asset.getSymbol());
        amountField.setValue(amountOfTokens);
        marketPriceField.setValue(getAvgPriceByType(asset));
        totalCostField.setValue(amountOfTokens * marketPriceField.doubleValue());
    }

    // TODO: Dont use avg price for marketPrice, use current price
    //  - But add another field with average buy, current amount
    private double getAvgPriceByType(AssetDTO asset) {
        if(instrumentsFacadeService.getTransactionsByAsset(portfolio.getId(), asset.getSymbol()).isEmpty())
            return asset.getMarketPrice();

        return typeField.getValue().isBuyTransaction()
                ? portfolioPerformanceTracker.getAverageBuyPrice(portfolio, asset)
                : portfolioPerformanceTracker.getAverageSellPrice(portfolio, asset);
    }

    public Select<TransactionType> getTypeField() {
        return typeField;
    }

    public AmountField getAmountField() {
        return amountField;
    }

    public CurrencyField getMarketPriceField() {
        return marketPriceField;
    }

    public CurrencyField getTotalCostField() {
        return totalCostField;
    }

}
