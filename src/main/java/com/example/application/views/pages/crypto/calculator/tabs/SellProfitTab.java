package com.example.application.views.pages.crypto.calculator.tabs;

import com.example.application.entities.crypto.Asset;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.services.crypto.PortfolioPerformanceTracker;
import com.example.application.utils.common.MathUtils;
import com.example.application.utils.common.number.PercentageFormatter;
import com.example.application.views.components.complex_components.NumericValueParagraph;
import com.example.application.views.components.complex_components.fields.PricePercentageWrapper;
import com.example.application.views.components.fields.AmountField;
import com.example.application.views.components.fields.CurrencyField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/*
    TODO:
        - Wanted Profit: To gain a $50,000 profit from $50,000, BTC needs to hit 50,000 + 50,000 = 100,000 (n %)
        - Inflation: If inflation is 3%, the inflation-adjusted profit would be  20,000 / (1+0.03) = 19,417.47.
        - Compound Growth: Reinvesting $20,000 at a 15% annual return for 5 years yields 20,000 * ( 1 + 0.15 ) 5 = 40,228.86
        - Market Cap: What market cap will be at that sell price -> which top will enter
* */

@Component
public class SellProfitTab extends BaseCalculatorTab {

    private final PortfolioPerformanceTracker portfolioPerformanceTracker;

    private final ComboBox<Asset> assetSymbolField = new ComboBox<>("Asset");
    private final AmountField amountField = new AmountField("Amount");
    private final CurrencyField buyPriceField = new CurrencyField("Buy Price");
    private final CurrencyField totalPriceField = new CurrencyField("Total");
    private final CurrencyField sellPriceField = new CurrencyField("Sell Price");

    @Autowired
    public SellProfitTab(InstrumentsFacadeService instrumentsFacadeService, PortfolioPerformanceTracker portfolioPerformanceTracker) {
        super("Sell profit calculator", instrumentsFacadeService);
        this.portfolioPerformanceTracker = portfolioPerformanceTracker;
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

        updateFieldsValues();
    }

    private void updateFieldsValues() {
        Asset selectedAsset = assetSymbolField.getValue();
        double amountOfTokens = getAmountOfTokens(selectedAsset);
        double currentPrice = getAssetMarketPrice(assetSymbolField);
        amountField.setValue(amountOfTokens);
        buyPriceField.setValue(portfolioPerformanceTracker.getAverageBuyPrice(selectedAsset));
        sellPriceField.setValue(amountOfTokens * currentPrice);
        totalPriceField.setValue(amountOfTokens * buyPriceField.doubleValue());
    }

    private void initializeFieldsListeners() {
        assetSymbolField.addValueChangeListener(l -> {
            updateFieldsValues();
            amountField.setSuffixComponent(new Span(getSelectedAssetSymbol(assetSymbolField)));
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
            String symbol = getSelectedAssetSymbol(assetSymbolField);
            double invested = totalPriceField.doubleValue();
            double buyPrice = buyPriceField.doubleValue();
            double sellPrice = sellPriceField.doubleValue();
            double amountTokens = amountField.doubleValue();
            double profit = MathUtils.profit(buyPrice, sellPrice, invested);
            double profitPercentage = MathUtils.profitPercentage(buyPrice, sellPrice);
            double totalWorth = profit + invested;
            double buyPricePerUnit = MathUtils.buyPricePerUnit(buyPrice, amountTokens);
            double sellPricePerUnit = MathUtils.sellPricePerUnit(sellPrice, amountTokens);
            double tokensToSellToBeInZero = MathUtils.safeZeroDivision(invested, sellPrice);
            double profitTokens = amountTokens - tokensToSellToBeInZero;
            double profitTokensValue = profitTokens * buyPrice;

            double netProfitPerUnit = MathUtils.safeZeroDivision(profit, amountTokens);
            NumericValueParagraph worthParagraph = new NumericValueParagraph(totalWorth, currencyFormatter, true);
            PricePercentageWrapper netProfitWrapper = new PricePercentageWrapper(profit, profitPercentage);

            PercentageFormatter percentageFormatter = new PercentageFormatter();
            percentageFormatter.setMaximumFractionDigits(0);
            netProfitWrapper.setPercentageFormatter(percentageFormatter);

            String text = String.format("%s %s ≈ $%.2f", amountFormatter.format(profitTokens), symbol, profitTokensValue);
            Paragraph tokensProfitWrapper = new Paragraph(text);

            String zeroProfitSellQuantity = "%s %s".formatted(amountFormatter.format(tokensToSellToBeInZero), symbol);

            resultsContainer.removeAll();
            resultsContainer.add(
                    createResultItem("Invested", invested),
                    createResultItem("Buy Price", buyPrice),
                    createResultItem("Sell Price", sellPrice),
                    createResultItem("Buy price per unit", buyPricePerUnit),
                    createResultItem("Sell price per unit", sellPricePerUnit),
                    // TODO: new Hr() with
                    //  - How many % the token price raised, or dropped
                    //  - What market cap was, and what cap gained
                    new Hr(),
                    createResultItem("Total Worth", worthParagraph),
                    createResultItem("Net Profit", netProfitWrapper),
                    createResultItem("Net Profit per unit", netProfitPerUnit),
                    new Hr(),
                    createResultItem("Sell quantity for zero profit", zeroProfitSellQuantity,
                            "How many tokens can you sell to safely exit from holding without loses"),
                    createResultItem("Remaining tokens profit", tokensProfitWrapper,
                            "The amount of tokens remained after safe holding exit")
            );

        });

        return calculateBtn;
    }

}
