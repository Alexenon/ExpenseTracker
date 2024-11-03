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
import com.vaadin.flow.data.value.ValueChangeMode;
import org.springframework.beans.factory.annotation.Autowired;

/*
    TODO:
        - Add wanted custom sell price
        - Display the amount of token will be staked daily..., not just the dollar amount
* */
public final class StakingProfitTab extends BaseCompareTab {

    private final ComboBox<Asset> assetSymbolField = new ComboBox<>("Asset");
    private final AmountField amountField = new AmountField("Amount of tokens");
    private final CurrencyField worthField = new CurrencyField("Total worth of asset");
    private final AmountField aprField = new AmountField("APR");

    private double assetMarketPrice;

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
            updateAssetMarketPrice(assetSymbolField);
            amountField.setValue(instrumentsFacadeService.getAmountOfTokens(l.getValue()));
            worthField.setValue(amountField.doubleValue() * assetMarketPrice);
        });

        aprField.setValue(1);
    }

    private void initializeFieldsListeners() {
        assetSymbolField.addValueChangeListener(e -> {
            Asset selectedAsset = e.getValue();
            if (selectedAsset == null) {
                return;
            }

            // TODO: Add worth as well ???
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
                    createOutputItem("Daily", EarnCalculator.earnDaily(amount, apr)),
                    createOutputItem("Weekly", EarnCalculator.earnWeekly(amount, apr)),
                    createOutputItem("Monthly", EarnCalculator.earnMonthly(amount, apr)),
                    createOutputItem("Yearly", EarnCalculator.earnYearly(amount, apr))
            );
        });

        return calculateBtn;
    }

}
