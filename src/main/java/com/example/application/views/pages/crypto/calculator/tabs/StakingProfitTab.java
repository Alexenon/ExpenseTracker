package com.example.application.views.pages.crypto.calculator.tabs;

import com.example.application.entities.crypto.Portfolio;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.utils.common.formatters.number.AmountFormatter;
import com.example.application.utils.common.formatters.number.CurrencyFormatter;
import com.example.application.utils.common.lang.MathUtils;
import com.example.application.views.components.custom.fields.AmountField;
import com.example.application.views.components.custom.fields.AssetComboBox;
import com.example.application.views.components.custom.fields.CurrencyField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import org.springframework.beans.factory.annotation.Autowired;

import static com.example.application.utils.investment.EarnCalculator.*;

public final class StakingProfitTab extends BaseCalculatorTab {

    private static final AmountFormatter amountFormatter = AmountFormatter.withDefaults();
    private static final CurrencyFormatter currencyFormatter = CurrencyFormatter.withDefaults();

    private final Portfolio portfolio;

    private final AssetComboBox assetSymbolField;
    private final AmountField amountField = new AmountField("Amount of tokens");
    private final CurrencyField worthField = new CurrencyField("Current total worth");
    private final AmountField aprField = new AmountField("APR");

    @Autowired
    public StakingProfitTab(InstrumentsFacadeService instrumentsFacadeService) {
        super("Staking calculator", instrumentsFacadeService);
        this.assetSymbolField = new AssetComboBox(instrumentsFacadeService);
        this.portfolio = instrumentsFacadeService.getAuthenticatedUserMainPortfolio();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        buildForm();
    }

    private void buildForm() {
        initializeFieldsValues();
        initializeFieldsListeners();
    }

    private void initializeFieldsValues() {
        aprField.setValue(1);
        aprField.setSuffixComponent(new Span("%"));
    }

    private void initializeFieldsListeners() {
        assetSymbolField.addValueChangeListener(field -> {
            double amountTokens = assetSymbolField.getAmountTokens(portfolio);
            amountField.setValue(amountTokens);
            amountField.setSuffixComponent(new Span(assetSymbolField.getSymbol()));
            worthField.setValue(amountTokens * assetSymbolField.getMarketPrice());
        });

        amountField.setValueChangeMode(ValueChangeMode.EAGER);
        amountField.addKeyUpListener(e -> worthField.setValue(amountField.doubleValue() * assetSymbolField.getMarketPrice()));

        worthField.setValueChangeMode(ValueChangeMode.EAGER);
        worthField.addKeyUpListener(e -> {
            double amountOfTokens = MathUtils.safeDivision(worthField.doubleValue(), assetSymbolField.getMarketPrice());
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
        calculateBtn.addClassName("add-entity-btn");
        calculateBtn.addClickListener(e -> {
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
                assetSymbolField.getSymbol(),
                currencyFormatter.format(worthEquivalent)
        );
    }

}
