package com.example.application.views.pages.crypto.calculator.tabs;

import com.example.application.data.dtos.AssetDTO;
import com.example.application.data.dtos.PortfolioDTO;
import com.example.application.finance.FinancialConstants;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.services.crypto.PortfolioPerformanceTracker;
import com.example.application.utils.common.formatters.number.PercentageFormatter;
import com.example.application.utils.investment.ProfitUtils;
import com.example.application.views.components.core.Container;
import com.example.application.views.components.custom.display.NumericValueParagraph;
import com.example.application.views.components.custom.fields.AmountField;
import com.example.application.views.components.custom.fields.AssetComboBox;
import com.example.application.views.components.custom.fields.MoneyField;
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
import com.vaadin.flow.router.BeforeEnterEvent;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

/*

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

public class SellProfitTab extends BaseCalculatorTab {

    private final PortfolioDTO portfolio;
    private final PortfolioPerformanceTracker portfolioPerformanceTracker;

    private final AssetComboBox assetSymbolField;
    private final AmountField amountField = new AmountField("Amount of tokens");
    private final MoneyField buyPriceField = new MoneyField("Buy Price");
    private final MoneyField totalCostField = new MoneyField("Total Cost");
    private final MoneyField sellPriceField = new MoneyField("Sell Price");

    @Autowired
    public SellProfitTab(InstrumentsFacadeService instrumentsFacadeService, PortfolioPerformanceTracker portfolioPerformanceTracker) {
        super("Sell profit calculator", instrumentsFacadeService);
        this.portfolioPerformanceTracker = portfolioPerformanceTracker;
        this.assetSymbolField = new AssetComboBox(instrumentsFacadeService);
        this.portfolio = instrumentsFacadeService.getActivePortfolio();
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
        AssetDTO selectedAsset = assetSymbolField.getValue();
        BigDecimal averageBuyPrice = portfolioPerformanceTracker.getAverageBuyPrice(portfolio, selectedAsset);
        BigDecimal amountOfTokens = assetSymbolField.getAmountTokens(portfolio.getId());
        amountField.setValue(amountOfTokens);
        buyPriceField.setValue(averageBuyPrice);
        sellPriceField.setValue(assetSymbolField.getMarketPrice());
        totalCostField.setValue(amountOfTokens.multiply(averageBuyPrice));
    }

    private void initializeFieldsListeners() {
        assetSymbolField.addValueChangeListener(l -> {
            initializeFieldsValues();
            amountField.setSuffixComponent(new Span(assetSymbolField.getSymbol()));
        });

        amountField.setValueChangeMode(ValueChangeMode.EAGER);
        amountField.addKeyUpListener(e -> {
			BigDecimal amount = amountField.getAmount();
			BigDecimal buyPrice = buyPriceField.getMoneyAmount();
            totalCostField.setValue(amount.multiply(buyPrice));
        });

        buyPriceField.setValueChangeMode(ValueChangeMode.EAGER);
        buyPriceField.addKeyUpListener(e -> {
			BigDecimal amount = amountField.getAmount();
			BigDecimal buyPrice = buyPriceField.getMoneyAmount();
			totalCostField.setValue(amount.multiply(buyPrice));
        });

        totalCostField.setValueChangeMode(ValueChangeMode.EAGER);
        totalCostField.addKeyUpListener(e -> {
			BigDecimal totalCost = totalCostField.getMoneyAmount();
			BigDecimal buyPrice = buyPriceField.getMoneyAmount();
			BigDecimal amount = totalCost.divide(buyPrice, FinancialConstants.PRICE_SCALE, RoundingMode.HALF_UP);
			amountField.setValue(amount);
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
            BigDecimal invested = totalCostField.getMoneyAmount();
            BigDecimal buyPrice = buyPriceField.getMoneyAmount();
            BigDecimal sellPrice = sellPriceField.getMoneyAmount();
            BigDecimal amountTokens = amountField.getAmount();
            BigDecimal profit = ProfitUtils.netProfit(buyPrice, sellPrice, invested);
            BigDecimal profitPercentage = ProfitUtils.profitPercentage(buyPrice, sellPrice, invested);
            BigDecimal totalWorth = profit.add(invested);

            PercentageFormatter percentageFormatter = new PercentageFormatter();
            percentageFormatter.setMaximumFractionDigits(0);

            BigDecimal netProfitPerUnit = profit.divide(amountTokens, FinancialConstants.PRICE_SCALE, RoundingMode.HALF_UP);
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

    private String zeroQuantitySellProfit(BigDecimal invested, BigDecimal sellPrice) {
        BigDecimal amountTokens = invested.divide(sellPrice, FinancialConstants.AMOUNT_SCALE, RoundingMode.HALF_UP);
        return "%s %s".formatted(amountFormatter.format(amountTokens), assetSymbolField.getSymbol());
    }

    private Paragraph getTokensProfitWrapper() {
        String selectedSymbol = assetSymbolField.getSymbol();
		BigDecimal tokensToSellToBeInZero = totalCostField.getMoneyAmount()
				.divide(sellPriceField.getMoneyAmount(), FinancialConstants.AMOUNT_SCALE, RoundingMode.HALF_UP);
		BigDecimal profitTokens = amountField.getAmount().subtract(tokensToSellToBeInZero);
        BigDecimal profitTokensValue = profitTokens.multiply(buyPriceField.getMoneyAmount());
        return new Paragraph(String.format("%s %s ≈ $%.2f", amountFormatter.format(profitTokens), selectedSymbol, profitTokensValue));
    }

    private Div marketCapStatsWrapper(AssetDTO asset, BigDecimal buyPrice, BigDecimal sellPrice) {
        BigInteger circulationSupply = asset.getCirculationSupply();
        BigDecimal prevMarketCap = ProfitUtils.marketCap(circulationSupply, buyPrice);
        BigDecimal newMarketCap = ProfitUtils.marketCap(circulationSupply, sellPrice);

        MonoIcon arrowIcon = PictogramIcon.ARROW_RIGHT_THIN.create();
        Paragraph previousMarketCap = new Paragraph(compactFormatter.format(prevMarketCap));
        Paragraph followingMarketCap = new Paragraph(compactFormatter.format(newMarketCap));

        return new Container("centered-row", previousMarketCap, arrowIcon, followingMarketCap);
    }

    private Div fdvStatsWrapper(AssetDTO asset, BigDecimal buyPrice, BigDecimal sellPrice) {
        BigInteger totalSupply = asset.getTotalSupply();
        BigDecimal currentValueFDV = ProfitUtils.fdv(totalSupply, buyPrice);
        BigDecimal followingValueFDV = ProfitUtils.fdv(totalSupply, sellPrice);

        MonoIcon arrowIcon = PictogramIcon.ARROW_RIGHT_THIN.create();
        Paragraph previousFDV = new Paragraph(compactFormatter.format(currentValueFDV));
        Paragraph followingFDV = new Paragraph(compactFormatter.format(followingValueFDV));

        return new Container("centered-row", previousFDV, arrowIcon, followingFDV);
    }

}
