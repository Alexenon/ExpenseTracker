package com.example.application.views.pages.crypto;

import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.CryptoTransaction;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.services.crypto.PortfolioPerformanceTracker;
import com.example.application.utils.common.formatters.CommonFormatters;
import com.example.application.views.components.PriceChangeHandler;
import com.example.application.views.components.PriceChangeblePage;
import com.example.application.views.components.TransactionsGrid;
import com.example.application.views.components.core.Container;
import com.example.application.views.components.custom.dialogs.transactions.AddTransactionDialog;
import com.example.application.views.components.custom.dialogs.transactions.export.ImportTransactionsDialog;
import com.example.application.views.components.custom.display.NumericValueParagraph;
import com.example.application.views.components.custom.fields.PricePercentageWrapper;
import com.example.application.views.components.custom.fields.stats.PortfolioStatsDisplay;
import com.example.application.views.components.portfolio.AssetsChart;
import com.example.application.views.components.portfolio.AssetsGrid;
import com.example.application.views.layouts.MainLayout;
import com.example.application.views.pages.DefaultPage;
import com.example.application.views.pages.RebuildablePage;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.router.*;
import com.vaadin.flow.theme.lumo.LumoIcon;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/*
    TODO:
        => Info
            [!] Total Trading Volume
            [!] Gainers vs Loosers (How many assets are now in profit VS aren't)
        => Chart
            [!] Add chart options -> byCost, byWorth
            [!] Fix chart categories to not display 0 values
 * */

@Slf4j
@PermitAll
@PageTitle("Portfolio Tracker")
@Route(value = "portfolio", layout = MainLayout.class)
@JsModule("./themes/light_theme/components/javascript/fillPieChart.js")
@JavaScript("https://fastly.jsdelivr.net/npm/echarts@5.4.2/dist/echarts.min.js")
public class PortfolioTrackerView extends DefaultPage implements RebuildablePage, BeforeEnterObserver, BeforeLeaveObserver, PriceChangeblePage {

    private final PriceChangeHandler priceChangeHandler;
    private final InstrumentsFacadeService instrumentsFacadeService;
    private final PortfolioPerformanceTracker portfolioPerformanceTracker;
    private final AssetsGrid assetsGrid;
    private final AssetsChart assetsChart;
    private final TransactionsGrid transactionsGrid;

    private final UI ui;

    @Autowired
    public PortfolioTrackerView(InstrumentsFacadeService instrumentsFacadeService,
                                PortfolioPerformanceTracker portfolioPerformanceTracker,
                                PriceChangeHandler priceChangeHandler)
    {
        this.instrumentsFacadeService = instrumentsFacadeService;
        this.portfolioPerformanceTracker = portfolioPerformanceTracker;
        this.priceChangeHandler = priceChangeHandler;
        this.assetsGrid = new AssetsGrid(instrumentsFacadeService, portfolioPerformanceTracker);
        this.assetsChart = new AssetsChart(instrumentsFacadeService, portfolioPerformanceTracker);
        this.transactionsGrid = new TransactionsGrid(instrumentsFacadeService);
        this.ui = UI.getCurrent();
        initializePage();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        buildPage();
        priceChangeHandler.addObserver(ui, this);
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        priceChangeHandler.removeObserver(ui);
    }

    @Override
    public void initializePage() {
        getStyle().set("margin", "100px 30px 30px 30px");
        initializeGrids();
    }

    @Override
    public void buildPage() {
        add(
                headerSection(),
                statisticsSection(),
                performanceSection(),
                gridSection("Assets", assetsGrid),
                gridSection("Transactions", transactionsGrid)
        );
        updateGridItems();
    }

    @Override
    public void updatePage() {
        rebuildPage();
    }

    @Override
    public void rebuildPage() {
        ui.access(() -> {
            this.removeAll();
            this.buildPage();
        });
    }

    private void initializeGrids() {
        assetsGrid.setGridFullSize(true);

        transactionsGrid.setPageSize(10);
        transactionsGrid.addUpdateItemListener(l -> rebuildPage());
    }

    private void updateGridItems() {
        assetsGrid.setItems(instrumentsFacadeService.getAssetsWithNonZeroAmount());
        transactionsGrid.setItems(instrumentsFacadeService.getAllTransactions());
    }

    private Section headerSection() {
        Section section = new Section();
        section.addClassName("asset-details-header");

        NumericValueParagraph worth = new NumericValueParagraph(portfolioPerformanceTracker.getPortfolioWorth(), CommonFormatters.CURRENCY);
        double profit = portfolioPerformanceTracker.getPortfolioTotalProfit();
        double percentage = portfolioPerformanceTracker.getPortfolioProfitPercentage();
        PricePercentageWrapper profitWrapper = new PricePercentageWrapper(profit, percentage);

        Container portfolioWorthWrapper = new Container("price-wrapper", worth, profitWrapper);
        portfolioWorthWrapper.getStyle().set("flex-direction", "column");
        section.add(portfolioWorthWrapper);

        Button addTransactionBtn = new Button("Add Transaction", LumoIcon.PLUS.create());
        addTransactionBtn.addClassName("add-entity-btn");
        addTransactionBtn.setIconAfterText(false);
        addTransactionBtn.addClickListener(e -> {
            AddTransactionDialog dialog = new AddTransactionDialog(instrumentsFacadeService);
            dialog.open();
            dialog.addSaveBtnClickListener(l -> rebuildPage());
        });

        Button importBtn = new Button("Import", LumoIcon.UPLOAD.create());
        importBtn.addClassName("add-entity-btn");
        importBtn.setIconAfterText(false);
        importBtn.addClickListener(e -> {
            ImportTransactionsDialog dialog = new ImportTransactionsDialog(instrumentsFacadeService);
            dialog.open();
//            dialog.addSaveBtnClickListener(l -> rebuildPage());
        });



        section.add(addTransactionBtn, importBtn);

        return section;
    }

    private Section gridSection(String titleName, Component grid) {
        Section section = new Section();
        section.add();
        H3 title = new H3(titleName);
        title.setClassName("section-title");

        section.add(title, grid);
        return section;
    }

    private Div statisticsSection() {
        Div sectionWrapper = new Div();
        sectionWrapper.setClassName("statistics-section-wrapper");

        Section statisticSectionDetails = new Section();
        H3 title = new H3("Portfolio Statistics");
        title.setClassName("section-title");

        double totalProfit = portfolioPerformanceTracker.getPortfolioTotalProfit();
        String nrOfAssets = String.valueOf(instrumentsFacadeService.getAssetsWithNonZeroAmount().size());
        String realized = CommonFormatters.CURRENCY.format(portfolioPerformanceTracker.getPortfolioRealizedProfit());
        String unrealized = CommonFormatters.CURRENCY.format(portfolioPerformanceTracker.getPortfolioUnrealizedProfit());
        String avgTimeHolding = String.format("%.1f days", portfolioPerformanceTracker.getPortfolioAverageHoldingDays());
        String ratio = portfolioPerformanceTracker.getPortfolioBuySellRatio();

        Div totalWorth = new PortfolioStatsDisplay("Total Worth",
                CommonFormatters.CURRENCY.format(portfolioPerformanceTracker.getPortfolioWorth()),
                "Total value of all your holdings based on the latest price");
        Div totalCost = new PortfolioStatsDisplay("Total Cost",
                CommonFormatters.CURRENCY.format(portfolioPerformanceTracker.getPortfolioCost()),
                "Total amount of dollars invested to buy all the assets");
        Div numberOfAssets = new PortfolioStatsDisplay("No. of Assets", nrOfAssets,
                "Current number of assets that are in your portfolio");
        Div profitStats = new PortfolioStatsDisplay("Total Profit",
                new NumericValueParagraph(totalProfit, CommonFormatters.CURRENCY, true),
                "Represents the realized profit + unrealized profit");
        Div realizedProfit = new PortfolioStatsDisplay("Realized Profit", realized,
                "Profit or Loss from all your sold holdings");
        Div unrealizedProfit = new PortfolioStatsDisplay("Unrealized Profit", unrealized,
                "Potential profit or loss if you were to sell all assets now");
        Div buySellRatio = new PortfolioStatsDisplay("Buy/Sell % Ratio", ratio, ratioHint(ratio));
        Div avgHoldingTime = new PortfolioStatsDisplay("Avg Holding Time", avgTimeHolding,
                "Average holding time for all assets, from the first bought");

        Div body = new Div();
        body.addClassNames("section-card-wrapper");
        body.add(
                totalWorth, totalCost, numberOfAssets,
                profitStats, realizedProfit, unrealizedProfit,
                buySellRatio, avgHoldingTime
        );
        statisticSectionDetails.add(title, body);

        sectionWrapper.add(statisticSectionDetails, assetsChart);
        return sectionWrapper;
    }

    private Section performanceSection() {
        Map<Asset, Double> mostProfitableAssets = getMostProfitableAssetsByProfit();

        if (mostProfitableAssets.isEmpty())
            return new Section();

        Section section = new Section();
        H3 title = new H3("Performance");
        title.setClassName("section-title");

        Asset mostProfitableAsset = Collections.max(mostProfitableAssets.entrySet(), Map.Entry.comparingByValue()).getKey();
        Asset leastProfitableAsset = Collections.min(mostProfitableAssets.entrySet(), Map.Entry.comparingByValue()).getKey();

        // The Assets that are most traded, by NUMBER of trades
        Map<Asset, Long> assetsNrTransactions = instrumentsFacadeService.getAllTransactions()
                .stream()
                .collect(Collectors.groupingBy(CryptoTransaction::getAsset, Collectors.counting()));

        Asset mostTradedAsset = Collections.max(assetsNrTransactions.entrySet(), Map.Entry.comparingByValue()).getKey();
        Asset leastTradedAsset = Collections.min(assetsNrTransactions.entrySet(), Map.Entry.comparingByValue()).getKey();


        Div body = new Div();
        body.addClassNames("section-card-wrapper");
        body.add(
                createPerformanceItem("Top Gainer", mostProfitableAsset),
                createPerformanceItem("Top Loser", leastProfitableAsset),
                createPerformanceItem("Most Traded", mostTradedAsset),
                createPerformanceItem("Least Traded", leastTradedAsset)
        );

        section.add(title, body);
        return section;
    }

    private Map<Asset, Double> getMostProfitableAssetsByProfit() {
        return instrumentsFacadeService.getAssetsWithNonZeroAmount()
                .stream()
                .collect(Collectors.toMap(asset -> asset, portfolioPerformanceTracker::getAssetTotalProfit, (a, b) -> b));
    }

    private Div createPerformanceItem(String labelText, Asset asset) {
        Div container = new Div();
        container.addClassName("performance-item");

        Paragraph label = new Paragraph(labelText);
        label.addClassName("performance-label");
        Paragraph assetSymbol = new Paragraph(asset.getSymbol());
        assetSymbol.addClassName("performance-symbol");

        Image assetImage = new Image(asset.getImageUrl(), asset.getSymbol());
        assetImage.addClassNames("coin-overview-image", "performance-asset-image");

        double profit = portfolioPerformanceTracker.getAssetTotalProfit(asset);
        double percentageProfit = portfolioPerformanceTracker.getAssetNetProfitPercentage(asset);
        PricePercentageWrapper pricePercentageWrapper = new PricePercentageWrapper(profit, percentageProfit);
        pricePercentageWrapper.addClassName("performance-values");

        Container performanceDetails = Container.builder("performance-details")
                .addComponent(new Div(label, assetSymbol))
                .addComponent(pricePercentageWrapper)
                .build();

        container.add(assetImage, performanceDetails);
        container.addClickListener(e -> UI.getCurrent().navigate(AssetDetailsView.class, asset.getSymbol()));
        return container;
    }

    private String ratioHint(String ratio) {
        String[] ratioParts = ratio.split(":");
        return ratio.equals("N/A")
                ? "Ratio between BUY and SELL transactions, in dollar equivalent"
                : String.format("%s%% of transactions are buys, %s%% are sells, in dollar equivalent", ratioParts[0].trim(), ratioParts[1]);
    }

}
