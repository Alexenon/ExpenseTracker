package com.example.application.views.pages.crypto.comparator.tabs;

import com.example.application.entities.crypto.Asset;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.utils.common.MathUtils;
import com.example.application.utils.investment.EarnCalculator;
import com.example.application.views.components.fields.AmountField;
import com.example.application.views.components.fields.CurrencyField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.springframework.beans.factory.annotation.Autowired;

/*
    TODO:
        - Add wanted custom sell price
        - Display the amount of token will be staked daily..., not just the dollar amount
* */
public final class StakingProfitTab extends BaseCalculatorTab {

    private final ComboBox<Asset> assetSymbolField = new ComboBox<>("Asset");
    private final AmountField amountField = new AmountField("Amount of tokens");
    private final CurrencyField worthField = new CurrencyField("Total worth of asset");
    private final AmountField aprField = new AmountField("APR");

    @Autowired
    public StakingProfitTab(InstrumentsFacadeService instrumentsFacadeService) {
        super("Staking calculator", instrumentsFacadeService);
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
        assetSymbolField.addValueChangeListener(l -> {
            amountField.setValue(instrumentsFacadeService.getAmountOfTokens(l.getValue()));
            worthField.setValue(amountField.doubleValue() * getAssetMarketPrice(assetSymbolField));
        });

        aprField.setValue(1);
        aprField.setSuffixComponent(new Span("%"));
    }

    private void initializeFieldsListeners() {
        assetSymbolField.addValueChangeListener(e -> {
            amountField.setValue(getAmountOfTokens(assetSymbolField.getValue()));
            amountField.setSuffixComponent(new Span(getSelectedAssetSymbol(assetSymbolField)));
            worthField.setValue(amountField.doubleValue() * getAssetMarketPrice(assetSymbolField));
        });

        amountField.setValueChangeMode(ValueChangeMode.EAGER);
        amountField.addKeyUpListener(e -> worthField.setValue(amountField.doubleValue() * getAssetMarketPrice(assetSymbolField)));

        worthField.setValueChangeMode(ValueChangeMode.EAGER);
        worthField.addKeyUpListener(e -> {
            double amountOfTokens = MathUtils.safeZeroDivision(worthField.doubleValue(), getAssetMarketPrice(assetSymbolField));
            amountField.setValue(amountOfTokens);
        });
    }

    @Override
    protected Div createInputFieldsContainer() {
        return new Div(assetSymbolField, amountField, worthField, aprField);
    }

    @Override
    protected Button createDisplayResultsBtn() {
        Button calculateBtn = new Button("Calculate");

        calculateBtn.addClickListener(e -> {
            double apr = aprField.doubleValue();
            double amount = worthField.doubleValue();

            resultsContainer.removeAll();
            resultsContainer.add(
                    createResultItem("Daily", EarnCalculator.earnDaily(amount, apr)),
                    createResultItem("Weekly", EarnCalculator.earnWeekly(amount, apr)),
                    createResultItem("Monthly", EarnCalculator.earnMonthly(amount, apr)),
                    createResultItem("Yearly", EarnCalculator.earnYearly(amount, apr))
            );
        });

        return calculateBtn;
    }

}
