package com.example.application.views.pages.crypto.calculator.tabs;

import com.example.application.entities.crypto.Asset;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.services.crypto.PortfolioPerformanceTracker;
import com.example.application.utils.common.MathUtils;
import com.example.application.utils.common.number.PercentageFormatter;
import com.example.application.utils.investment.ProfitUtils;
import com.example.application.views.components.core.Container;
import com.example.application.views.components.custom.display.NumericValueParagraph;
import com.example.application.views.components.custom.fields.AmountField;
import com.example.application.views.components.custom.fields.AssetComboBox;
import com.example.application.views.components.custom.fields.CurrencyField;
import com.example.application.views.components.custom.fields.PricePercentageWrapper;
import com.example.application.views.components.custom.icons.MonoIcon;
import com.example.application.views.components.custom.icons.PictogramIcon;
import com.vaadin.flow.component.ScrollOptions;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

/*
    TODO:
        - Wanted Profit: To gain a $50,000 profit from $50,000, BTC needs to hit 50,000 + 50,000 = 100,000 (n %)
        - Inflation: If inflation is 3%, the inflation-adjusted profit would be  20,000 / (1+0.03) = 19,417.47.
        - Compound Growth: Reinvesting $20,000 at a 15% annual return for 5 years yields 20,000 * ( 1 + 0.15 ) 5 = 40,228.86
        - Market Cap: What market cap will be at that sell price -> which top will enter

    FIXME:
        - When user don't have amount of such tokens then:
            1. BuyPriceField = current price
            2. SellPriceField = empty
            3. AmountField = empty
            4. Total = don't touch, automatically will be calculated
            5. Force user to add SellPrice

    TODO: Ideally, move as two separate features into 2 different calculators:
        - Profit sell calculator
        - What happens when achieve such price, without having amount of tokens
        * [Maybe add a checkbox to give user to exclude placing amount/total cost]
* */

@Component
public class SellProfitTab extends BaseCalculatorTab {

    private final PortfolioPerformanceTracker portfolioPerformanceTracker;

    private final AssetComboBox assetSymbolField;
    private final AmountField amountField = new AmountField("Amount of tokens");
    private final CurrencyField buyPriceField = new CurrencyField("Buy Price");
    private final CurrencyField totalCostField = new CurrencyField("Total Cost");
    private final CurrencyField sellPriceField = new CurrencyField("Sell Price");

    @Autowired
    public SellProfitTab(InstrumentsFacadeService instrumentsFacadeService, PortfolioPerformanceTracker portfolioPerformanceTracker) {
        super("Sell profit calculator", instrumentsFacadeService);
        this.portfolioPerformanceTracker = portfolioPerformanceTracker;
        this.assetSymbolField = new AssetComboBox(instrumentsFacadeService);
        buildForm();
    }

    private void buildForm() {
        initializeFields();
        initializeFieldsValues();
        initializeFieldsListeners();
    }

    private void initializeFields() {

    }

    private void initializeFieldsValues() {
        Asset selectedAsset = assetSymbolField.getValue();
        double averageBuyPrice = portfolioPerformanceTracker.getAverageBuyPrice(selectedAsset);
        double amountOfTokens = assetSymbolField.getAmountTokens();
        amountField.setValue(amountOfTokens);
        buyPriceField.setValue(averageBuyPrice);
        sellPriceField.setValue(assetSymbolField.getMarketPrice());
        totalCostField.setValue(amountOfTokens * averageBuyPrice);
    }

    private void initializeFieldsListeners() {
        assetSymbolField.addValueChangeListener(l -> {
            initializeFieldsValues();
            amountField.setSuffixComponent(new Span(assetSymbolField.getSymbol()));
        });

        amountField.setValueChangeMode(ValueChangeMode.EAGER);
        amountField.addKeyUpListener(e -> {
            double totalPrice = amountField.doubleValue() * buyPriceField.doubleValue();
            totalCostField.setValue(totalPrice);
        });

        buyPriceField.setValueChangeMode(ValueChangeMode.EAGER);
        buyPriceField.addKeyUpListener(e -> {
            double totalPrice = amountField.doubleValue() * buyPriceField.doubleValue();
            totalCostField.setValue(totalPrice);
        });

        totalCostField.setValueChangeMode(ValueChangeMode.EAGER);
        totalCostField.addKeyUpListener(e -> {
            double amountValue = MathUtils.safeZeroDivision(totalCostField.doubleValue(), buyPriceField.doubleValue());
            amountField.setValue(amountValue);
        });
    }

    @Override
    protected Div createInputFieldsContainer() {
        return new Div(assetSymbolField, amountField, buyPriceField, totalCostField, sellPriceField);
    }

    @Override
    protected Button createDisplayResultsBtn() {
        Button calculateBtn = new Button("Calculate");
        calculateBtn.addClassName("add-entity-btn");
        calculateBtn.addClickListener(e -> {
            double invested = totalCostField.doubleValue();
            double buyPrice = buyPriceField.doubleValue();
            double sellPrice = sellPriceField.doubleValue();
            double amountTokens = amountField.doubleValue();
            double profit = ProfitUtils.netProfit(buyPrice, sellPrice, invested);
            double profitPercentage = ProfitUtils.profitPercentage(buyPrice, sellPrice, invested);
            double totalWorth = profit + invested;

            PercentageFormatter percentageFormatter = new PercentageFormatter();
            percentageFormatter.setMaximumFractionDigits(0);

            double netProfitPerUnit = MathUtils.safeZeroDivision(profit, amountTokens);
            NumericValueParagraph worthParagraph = new NumericValueParagraph(totalWorth, currencyFormatter, true);
            PricePercentageWrapper netProfitWrapper = new PricePercentageWrapper(profit, profitPercentage);
            netProfitWrapper.setPercentageFormatter(percentageFormatter);

            resultsContainer.removeAll();
            resultsContainer.add(
                    createResultItem("Invested", invested),
                    createResultItem("Buy Price", buyPrice),
                    createResultItem("Sell Price", sellPrice),
                    new Hr(),
                    createResultItem("Growth Rate", percentageFormatter.format(ProfitUtils.growthPercentage(buyPrice, sellPrice))),
                    createResultItem("Market Cap", marketCapStatsWrapper(assetSymbolField.getValue(), buyPrice, sellPrice)),
                    createResultItem("FDV", fdvStatsWrapper(assetSymbolField.getValue(), buyPrice, sellPrice)),
                    new Hr(),
                    createResultItem("Total Worth", worthParagraph),
                    createResultItem("Net Profit", netProfitWrapper),
                    createResultItem("Net Profit per unit", netProfitPerUnit),
                    new Hr(),
                    createResultItem("Sell quantity for zero profit", zeroQuantitySellProfit(invested, sellPrice),
                            "How many tokens can you sell to safely exit from holding without loses"),
                    createResultItem("Remaining tokens profit", getTokensProfitWrapper(),
                            "The amount of tokens remained after safe holding exit")
            );

            // Scroll to the bottom of element
            ScrollOptions options = new ScrollOptions();
            options.setBehavior(ScrollOptions.Behavior.SMOOTH);
            options.setBlock(ScrollOptions.Alignment.END);
            resultsContainer.getElement().scrollIntoView(options);
        });

        return calculateBtn;
    }

    private String zeroQuantitySellProfit(double invested, double sellPrice) {
        double amountTokens = MathUtils.safeZeroDivision(invested, sellPrice);
        return "%s %s".formatted(amountFormatter.format(amountTokens), assetSymbolField.getSymbol());
    }

    private Paragraph getTokensProfitWrapper() {
        String selectedSymbol = assetSymbolField.getSymbol();
        double tokensToSellToBeInZero = MathUtils.safeZeroDivision(totalCostField.doubleValue(), sellPriceField.doubleValue());
        double profitTokens = amountField.doubleValue() - tokensToSellToBeInZero;
        double profitTokensValue = profitTokens * buyPriceField.doubleValue();
        return new Paragraph(String.format("%s %s ≈ $%.2f", amountFormatter.format(profitTokens), selectedSymbol, profitTokensValue));
    }

    private Div marketCapStatsWrapper(Asset asset, double buyPrice, double sellPrice) {
        BigInteger circulationSupply = instrumentsFacadeService.getAssetSupplyCirculating(asset);
        double prevMarketCap = ProfitUtils.marketCap(circulationSupply, buyPrice);
        double newMarketCap = ProfitUtils.marketCap(circulationSupply, sellPrice);

        MonoIcon arrowIcon = PictogramIcon.ARROW_RIGHT_THIN.create();
        Paragraph previousMarketCap = new Paragraph(compactFormatter.format(prevMarketCap));
        Paragraph followingMarketCap = new Paragraph(compactFormatter.format(newMarketCap));

        return new Container("centered-row", previousMarketCap, arrowIcon, followingMarketCap);
    }

    private Div fdvStatsWrapper(Asset asset, double buyPrice, double sellPrice) {
        BigInteger totalMarketSupply = instrumentsFacadeService.getAssetSupplyTotal(asset);
        double currentValueFDV = ProfitUtils.fdv(totalMarketSupply, buyPrice);
        double followingValueFDV = ProfitUtils.fdv(totalMarketSupply, sellPrice);

        MonoIcon arrowIcon = PictogramIcon.ARROW_RIGHT_THIN.create();
        Paragraph previousFDV = new Paragraph(compactFormatter.format(currentValueFDV));
        Paragraph followingFDV = new Paragraph(compactFormatter.format(followingValueFDV));

        return new Container("centered-row", previousFDV, arrowIcon, followingFDV);
    }

}
