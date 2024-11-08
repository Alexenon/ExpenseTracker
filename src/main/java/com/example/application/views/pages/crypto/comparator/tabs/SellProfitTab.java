package com.example.application.views.pages.crypto.comparator.tabs;

import com.example.application.entities.crypto.Asset;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.utils.common.MathUtils;
import com.example.application.views.components.complex_components.fields.PricePercentageWrapper;
import com.example.application.views.components.fields.AmountField;
import com.example.application.views.components.fields.CurrencyField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/*
    FIX:
        - Incorrect profit percetage value, check this
* */

@Component
public class SellProfitTab extends BaseCalculatorTab {

    private final ComboBox<Asset> assetSymbolField = new ComboBox<>("Asset");
    private final AmountField amountField = new AmountField("Amount");
    private final CurrencyField buyPriceField = new CurrencyField("Buy Price");
    private final CurrencyField totalPriceField = new CurrencyField("Total");
    private final CurrencyField sellPriceField = new CurrencyField("Sell Price");

    @Autowired
    public SellProfitTab(InstrumentsFacadeService instrumentsFacadeService) {
        super("Sell profit calculator", instrumentsFacadeService);
        buildForm();
    }

    private void buildForm() {
        initializeFieldsValues();
        initializeFieldsListeners();
    }

    private void initializeFieldsValues() {
        assetSymbolField.setItems(instrumentsFacadeService.getAllAssets());
        assetSymbolField.setItemLabelGenerator(instrumentsFacadeService::getAssetFullName);
        assetSymbolField.setRenderer(assetSymbolRenderer());
        assetSymbolField.addValueChangeListener(l -> buyPriceField.setValue(getAssetMarketPrice(assetSymbolField)));

        amountField.setValue("");
        buyPriceField.setValue(getAssetMarketPrice(assetSymbolField));
        totalPriceField.setValue(0);
        sellPriceField.setValue("");
    }

    private void initializeFieldsListeners() {
        assetSymbolField.addValueChangeListener(l -> {
            amountField.setValue(getAmountOfTokens(assetSymbolField.getValue()));
            amountField.setSuffixComponent(new Span(getSelectedAssetSymbol(assetSymbolField)));
            buyPriceField.setValue(getAssetMarketPrice(assetSymbolField));
            totalPriceField.setValue(amountField.doubleValue() * buyPriceField.doubleValue());
        });

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

    @Override
    protected Div createInputFieldsContainer() {
        return new Div(assetSymbolField, amountField, buyPriceField, totalPriceField, sellPriceField);
    }

    @Override
    protected Button createDisplayResultsBtn() {
        Button calculateBtn = new Button("Calculate");
        calculateBtn.addClickListener(e -> {
            double invested = totalPriceField.doubleValue();
            double buyPrice = buyPriceField.doubleValue();
            double sellPrice = sellPriceField.doubleValue();
            double totalProfit = MathUtils.profit(buyPrice, sellPrice, invested);
            double totalProfitPercentage = MathUtils.profitPercentage(buyPrice, sellPrice);
            double netProfit = totalProfit - invested;
            double netProfitPercentage = totalProfitPercentage - 100;
            PricePercentageWrapper worthWrapper = new PricePercentageWrapper(totalProfit, totalProfitPercentage);
            PricePercentageWrapper netProfitWrapper = new PricePercentageWrapper(netProfit, netProfitPercentage);

            resultsContainer.removeAll();
            resultsContainer.add(
                    createResultItem("Invested", invested),
                    createResultItem("Buy Price", buyPrice),
                    createResultItem("Sell Price", sellPrice),
                    createResultItem("Total Worth", worthWrapper),
                    createResultItem("Net Profit", netProfitWrapper)
            );

        });

        return calculateBtn;
    }


}
