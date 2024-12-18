package com.example.application.views.pages.crypto.calculator.tabs;

import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.CryptoTransaction;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.services.crypto.PortfolioPerformanceTracker;
import com.example.application.utils.investment.ProfitCalculator;
import com.example.application.utils.investment.ProfitUtils;
import com.example.application.views.components.core.Container;
import com.example.application.views.components.custom.fields.AssetComboBox;
import com.example.application.views.components.custom.fields.stats.ProfitStatsDisplay;
import com.example.application.views.components.custom.forms.layouts.TransactionalLayout;
import com.example.application.views.components.custom.icons.MonoIcon;
import com.example.application.views.components.custom.icons.PictogramIcon;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.theme.lumo.LumoIcon;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;


/*
    | Type  | Symbol | Price | Amount tokens / currency | Total |
    | Buy   | SOL    | $110  | 0.23 SOL ~ $120          | $200  |
    | Sell  | SOL    | $130  | 0.23 SOL ~ $120          | $200  |
    | Buy   | SOL    | $130  | 0.23 SOL ~ $120          | $200  |
    | Sell  | SOL    | $130  | 0.23 SOL ~ $120          | $200  |
* */

@Slf4j
public class ProfitEmulatorTab extends BaseCalculatorTab {

    private final InstrumentsFacadeService instrumentsFacadeService;
    private final PortfolioPerformanceTracker portfolioPerformanceTracker;

    private final AssetComboBox assetSymbolField;
    private final List<TransactionalLayout> transactionalLayouts = new ArrayList<>();
    private final Container assetMetaDetailsContainer = new Container("profit-meta-data-details");

    public ProfitEmulatorTab(InstrumentsFacadeService instrumentsFacadeService,
                             PortfolioPerformanceTracker portfolioPerformanceTracker) {
        super("Profit Buy/Sell Emulator", instrumentsFacadeService);
        this.instrumentsFacadeService = instrumentsFacadeService;
        this.portfolioPerformanceTracker = portfolioPerformanceTracker;
        this.assetSymbolField = new AssetComboBox(instrumentsFacadeService);
        buildTab();
    }

    private void buildTab() {
        assetSymbolField.addValueChangeListener(field -> {
            if (field.getHasValue().isEmpty()) {
                return;
            }

            Asset selectedAsset = field.getValue();
            transactionalLayouts.forEach(layout -> layout.setDefaultValues(selectedAsset));
            updateVisibilityForMetaData(selectedAsset);
        });

        add(assetMetaDetailsContainer);
    }

    @Override
    protected Div createInputFieldsContainer() {
        Button addNewLayoutBtn = new Button("Add Transaction", LumoIcon.PLUS.create());
        addNewLayoutBtn.setIconAfterText(false);
        addNewLayoutBtn.addClickListener(e -> createNewLayout());
        addNewLayoutBtn.addClassName("add-entity-btn");

        TransactionalLayout defaultLayout = new TransactionalLayout(instrumentsFacadeService, portfolioPerformanceTracker);
        defaultLayout.addClassName("buy-sell-layout");
        transactionalLayouts.add(defaultLayout);

        return new Div(assetSymbolField, addNewLayoutBtn, defaultLayout);
    }

    // TODO: Update this fields
    @Override
    protected Button createDisplayResultsBtn() {
        return new Button("Calculate", e -> {
            String symbol = assetSymbolField.getSymbol();
            List<CryptoTransaction> allTransactions = getListOfTransactions();

            double avgBuy = ProfitCalculator.getAverageBuyPrice(allTransactions);
            double avgSell = ProfitCalculator.getAverageSellPrice(allTransactions);
            double amountOfRemainingTokens = ProfitCalculator.getAmountOfRemainingTokens(allTransactions);
            double realizedProfit = ProfitCalculator.getRealizedProfit(allTransactions);
            double unrealizedProfit = amountOfRemainingTokens * assetSymbolField.getMarketPrice();
            double totalProfit = realizedProfit + unrealizedProfit;

            String buyVolumeInfo = currencyFormatter.format(ProfitCalculator.calculateTotalCostForBuyTransactions(allTransactions));
            String sellVolumeInfo = currencyFormatter.format(ProfitCalculator.calculateTotalCostForSellTransactions(allTransactions));
            String remainingCostInfo = currencyFormatter.format(ProfitCalculator.getTransactionsRemainingCost(allTransactions));

            resultsContainer.removeAll();
            resultsContainer.add(
                    // TODO: for avgBuy/sell: 20 ARB / $220.00
                    //  - explanation (display average buy amount and average buy price)
                    new ProfitStatsDisplay("Avg Buy:", currencyFormatter.format(avgBuy)),
                    new ProfitStatsDisplay("Avg Sell:", currencyFormatter.format(avgSell)),

                    new ProfitStatsDisplay("Avg Growth Rate", percentageFormatter.format(ProfitUtils.growthPercentage(avgBuy, avgSell))),
                    new ProfitStatsDisplay("Realized Profit", currencyFormatter.format(realizedProfit)),
                    new ProfitStatsDisplay("Unrealized Profit", currencyFormatter.format(unrealizedProfit)),
                    new ProfitStatsDisplay("Total Profit", currencyFormatter.format(totalProfit)),

                    // TODO EXAMPLE: "3496 ARB = $220"   - without decimal points
                    new ProfitStatsDisplay("Buy Trading Volume", buyVolumeInfo),
                    new ProfitStatsDisplay("Sell Trading Volume", sellVolumeInfo),

                    new ProfitStatsDisplay("Amount of tokens left:", amountFormatter.format(amountOfRemainingTokens, symbol)),
                    new ProfitStatsDisplay("Cost for remaining tokens:", remainingCostInfo)
            );

            /*
                                  | Current | Avg Buy | Avg Sell |
                     | Price      |  1.38 B | 1.19 B  | 1.79 B   |
                     | Market Cap |  1.38 B | 1.19 B  | 1.79 B   |
                     | FDV        |   ...   |   ...   |    ...   |


                    https://codepen.io/caplock221b/pen/WNraREK
                    https://stackoverflow.com/questions/35571603/removing-outer-border-in-html-table
            * */

            // TODO: Update this fields
            assetMetaDetailsContainer.removeAll();
            assetMetaDetailsContainer.add(
                    new ProfitStatsDisplay("Price", "20 ARB / $220.00"),
                    new ProfitStatsDisplay("Market Cap", "459 B"),
                    new ProfitStatsDisplay("FDV", "1.38 T -> 2.39 T")
            );
        });
    }

    private void updateVisibilityForMetaData(Asset asset) {
        if (asset == null) {
            assetMetaDetailsContainer.setVisible(false);
            return;
        }

        assetMetaDetailsContainer.setVisible(true);
    }

    private Div statsItem(String labelText, double value) {
        return new Div(new Paragraph(labelText), new Paragraph(String.valueOf(value)));
    }

    private void createNewLayout() {
        TransactionalLayout newLayout = new TransactionalLayout(instrumentsFacadeService, portfolioPerformanceTracker);
        newLayout.addClassName("buy-sell-layout");

        MonoIcon deleteBtn = PictogramIcon.TRASH_CAN_OUTLINE.create();
        deleteBtn.addClickListener(e -> newLayout.removeFromParent());

        newLayout.add(deleteBtn);
        transactionalLayouts.add(newLayout);
        inputFieldsContainer.add(newLayout);
    }

    private List<CryptoTransaction> getListOfTransactions() {
        return transactionalLayouts.stream()
                .map(layout -> {
                    Asset selectedAsset = assetSymbolField.getSelectedAsset();
                    double marketPrice = layout.getMarketPriceField().doubleValue();
                    double orderTotalCost = layout.getTotalCostField().doubleValue();
                    CryptoTransaction.TransactionType type = layout.getTypeField().getValue();

                    return new CryptoTransaction(selectedAsset, marketPrice, orderTotalCost, type);
                }).toList();
    }

}



