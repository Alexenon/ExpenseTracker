package com.example.application.views.pages.crypto.calculator.tabs;

import com.example.application.entities.crypto.Asset;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.utils.common.MathUtils;
import com.example.application.utils.common.number.AmountFormatter;
import com.example.application.utils.common.number.CurrencyFormatter;
import com.example.application.views.components.fields.AmountField;
import com.example.application.views.components.fields.CurrencyField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.springframework.beans.factory.annotation.Autowired;

import static com.example.application.utils.investment.EarnCalculator.*;

public final class StakingProfitTab extends BaseCalculatorTab {

    private static final AmountFormatter amountFormatter = AmountFormatter.withDefaults();
    private static final CurrencyFormatter currencyFormatter = CurrencyFormatter.withDefaults();

    private final ComboBox<Asset> assetSymbolField = new ComboBox<>("Asset");
    private final AmountField amountField = new AmountField("Amount of tokens");
    private final CurrencyField worthField = new CurrencyField("Current total worth");
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
            String symbol = getSelectedAssetSymbol(assetSymbolField);
            double apr = aprField.doubleValue();
            double worth = worthField.doubleValue();
            double amount = amountField.doubleValue();

            String daily = stakedAmount(earnDaily(amount, apr), earnDaily(worth, apr));
            String weekly = stakedAmount(earnWeekly(amount, apr), earnWeekly(worth, apr));
            String monthly = stakedAmount(earnMonthly(amount, apr), earnMonthly(worth, apr));
            String yearly = stakedAmount(earnYearly(amount, apr), earnYearly(worth, apr));

            resultsContainer.removeAll();
            resultsContainer.add(
                    createResultItem("Daily", daily),
                    createResultItem("Weekly", weekly),
                    createResultItem("Monthly", monthly),
                    createResultItem("Yearly", yearly)
            );
        });

        return calculateBtn;
    }

    private String stakedAmount(double amountTokens, double worthEquivalent) {
        return "+ %s %s ≈ %s".formatted(
                amountFormatter.format(amountTokens),
                getSelectedAssetSymbol(assetSymbolField),
                currencyFormatter.format(worthEquivalent)
        );
    }

}
