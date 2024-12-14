package com.example.application.views.pages.crypto.calculator.tabs;

import com.example.application.entities.crypto.Asset;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.services.crypto.PortfolioPerformanceTracker;
import com.example.application.views.components.custom.fields.AssetComboBox;
import com.example.application.views.components.custom.fields.stats.ProfitStatsDisplay;
import com.example.application.views.components.custom.forms.layouts.TransactionalLayout;
import com.example.application.views.components.custom.icons.MonoIcon;
import com.example.application.views.components.custom.icons.PictogramIcon;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.theme.lumo.LumoIcon;

import java.util.ArrayList;
import java.util.List;


/*
    | Type  | Symbol | Price | Amount tokens / currency | Total |
    | Buy   | SOL    | $110  | 0.23 SOL ~ $120          | $200  |
    | Sell  | SOL    | $130  | 0.23 SOL ~ $120          | $200  |
    | Buy   | SOL    | $130  | 0.23 SOL ~ $120          | $200  |
    | Sell  | SOL    | $130  | 0.23 SOL ~ $120          | $200  |
* */

public class ProfitEmulatorTab extends BaseCalculatorTab {

    private final InstrumentsFacadeService instrumentsFacadeService;
    private final PortfolioPerformanceTracker portfolioPerformanceTracker;

    private final AssetComboBox assetSymbolField;
    private final List<TransactionalLayout> transactionalLayouts = new ArrayList<>();
    private final Div assetDetailsContainer = new Div();

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
        });
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

    @Override
    protected Button createDisplayResultsBtn() {
        return new Button("Don't click");
    }

    private void updateAssetDetailsContainer(Asset asset) {
        if (asset == null) {
            assetDetailsContainer.setVisible(false);
            return;
        }

        assetDetailsContainer.setVisible(true);
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

    private Div transactionDetailsLayout(Asset asset) {
        Div div = new Div();

        double totalAmountBought = 0.0;

        // TODO: Update these values
        div.add(
                new ProfitStatsDisplay("Avg Buy:", "20 ARB / $220.00"),
                new ProfitStatsDisplay("Avg Sell:", "20 ARB / $220.00"),
                new ProfitStatsDisplay("Amount of tokens left:", "30 ARB ~ $34.56"),

                new ProfitStatsDisplay("Buy Trading Volume", "3496 ARB = $220"), // No decimal points
                new ProfitStatsDisplay("Buy Trading Volume", "12946 ARB = $220"),

                new ProfitStatsDisplay("Total BUY:", "20 ARB / $220.00"),
                new ProfitStatsDisplay("Total SOLD:", "20 ARB / $220.00")
        );

        return div;
    }

    // Details about MarketCap, currentPrice, ...
    private Div assetDetailsLayout(Asset asset) {
        Div div = new Div();

        /*
                      | Current | Avg Buy | Avg Sell |
         | Market Cap |  1.38 B | 1.19 B  | 1.79 B   |
         | FDV        |   ...   |   ...   |    ...   |


        https://codepen.io/caplock221b/pen/WNraREK
        https://stackoverflow.com/questions/35571603/removing-outer-border-in-html-table
        * */

        // TODO: Update this fields
        div.add(
                new ProfitStatsDisplay("Market Cap", "459 B"),
                new ProfitStatsDisplay("FDV", "1.38 T -> 2.39 T"),
                new ProfitStatsDisplay("Price", "20 ARB / $220.00")

        );

        return div;
    }
}



