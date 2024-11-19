package com.example.application.views.pages;

import com.example.application.entities.crypto.Asset;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.services.crypto.PortfolioPerformanceTracker;
import com.example.application.utils.common.number.CurrencyFormatter;
import com.example.application.views.components.AssetsGrid;
import com.example.application.views.components.TransactionsGrid;
import com.example.application.views.components.complex_components.NumericValueParagraph;
import com.example.application.views.components.complex_components.fields.PricePercentageWrapper;
import com.example.application.views.components.native_components.Container;
import com.example.application.views.layouts.MainLayout;
import com.example.application.views.pages.crypto.AssetDetailsView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/*

TODO: Analitics
     - How much amount of holding asset token to sell, to be in 0, How much remains, how much profit is it ?
     - mostTradedAsset, leastTradedAsset
     - largestSellTransaction, largestBuyTransaction
     - mostHeldAsset, leastHeldAsset

* */

@PermitAll
@PageTitle("Portfolio Tracker")
@Route(value = "portfolio-tracker", layout = MainLayout.class)
@JsModule("./themes/light_theme/components/javascript/fillPieChart.js")
@JavaScript("https://fastly.jsdelivr.net/npm/echarts@5.4.2/dist/echarts.min.js")
public class PortfolioTrackerView extends Main {

    private final static CurrencyFormatter currencyFormatter = CurrencyFormatter.withDefaults();

    private final InstrumentsFacadeService instrumentsFacadeService;
    private final PortfolioPerformanceTracker portfolioPerformanceTracker;

    private final AssetsGrid assetsGrid;
    private final TransactionsGrid transactionsGrid;
    private final Div assetsDiversityChart = new Div();

    @Autowired
    public PortfolioTrackerView(InstrumentsFacadeService instrumentsFacadeService,
                                PortfolioPerformanceTracker portfolioPerformanceTracker) {
        this.instrumentsFacadeService = instrumentsFacadeService;
        this.portfolioPerformanceTracker = portfolioPerformanceTracker;
        this.assetsGrid = new AssetsGrid(instrumentsFacadeService, portfolioPerformanceTracker);
        this.transactionsGrid = new TransactionsGrid(instrumentsFacadeService);

        initializePage();
        add(
                headerSection(),
                statisticsSection(),
                performanceSection(),
                gridSection("Assets", assetsGrid),
                gridSection("Transactions", transactionsGrid)
        );
        initializeChart();
    }

    private void initializePage() {
        getStyle().set("margin", "100px 30px 30px 30px");
        assetsGrid.setGridFullSize(true);
        assetsGrid.setItems(instrumentsFacadeService.getAssetsWithNonZeroAmount());
        transactionsGrid.setItems(instrumentsFacadeService.getAllTransactions());
        transactionsGrid.setPageSize(10);
    }

    private Section headerSection() {
        Section section = new Section();
        section.addClassName("asset-details-header");

        NumericValueParagraph worth = new NumericValueParagraph(portfolioPerformanceTracker.getPortfolioWorth());
        double percentage = portfolioPerformanceTracker.getPortfolioProfitPercentage();
        double profit = portfolioPerformanceTracker.getPortfolioProfit();
        PricePercentageWrapper profitWrapper = new PricePercentageWrapper(profit, percentage);

        Container portfolioWorthWrapper = new Container("price-wrapper", worth, profitWrapper);
        portfolioWorthWrapper.getStyle().set("flex-direction", "column");
        section.add(portfolioWorthWrapper);
        return section;
    }

    private void initializeChart() {
        assetsDiversityChart.setId("assets-diverstity-chart");

        Map<String, Double> assetsDiversity = instrumentsFacadeService.getAssetsWithNonZeroAmount()
                .stream()
                .collect(Collectors.toMap(Asset::getSymbol, portfolioPerformanceTracker::getAssetTotalCost, (a, b) -> b));

        JsonArray jsonOptionData = Json.createArray();
        AtomicInteger index = new AtomicInteger(0);
        assetsDiversity.forEach((assetName, diversityPercentage) -> {
            JsonObject jsonObject = Json.createObject();
            jsonObject.put("name", assetName);
            jsonObject.put("value", diversityPercentage);
            jsonOptionData.set(index.get(), jsonObject);

            System.out.println(jsonObject);

            index.addAndGet(1);
        });

        UI.getCurrent().getPage().executeJs("fillAssetsDiversityChart($0);", jsonOptionData.toJson());
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

        double profit = portfolioPerformanceTracker.getPortfolioProfit();
        String nrOfAssets = String.valueOf(instrumentsFacadeService.getAssetsWithNonZeroAmount().size());
        String realized = currencyFormatter.format(portfolioPerformanceTracker.getPortfolioRealizedProfit());
        String unrealized = currencyFormatter.format(portfolioPerformanceTracker.getPortfolioUnrealizedProfit());
        String avgTimeHolding = String.format("%.1f days", portfolioPerformanceTracker.getPortfolioAverageHoldingDays());

        // TODO: Add hints for help
        Div totalWorth = createStatsItem("Total Worth", currencyFormatter.format(portfolioPerformanceTracker.getPortfolioWorth()));
        Div totalCost = createStatsItem("Total Cost", currencyFormatter.format(portfolioPerformanceTracker.getPortfolioCost()));
        Div numberOfAssets = createStatsItem("No. of Assets", nrOfAssets);
        Div profitStats = createStatsItem("Profit", new NumericValueParagraph(profit));
        Div realizedProfit = createStatsItem("Realized Profit", realized);
        Div unrealizedProfit = createStatsItem("Unrealized Profit", unrealized);
        // TOOLTIP: 69:31 (69% of transactions are buys, 31% are sells.)
        Div buySellRatio = createStatsItem("Buy/Sell Ratio", portfolioPerformanceTracker.getPortfolioBuySellRatio());
        Div avgHoldingTime = createStatsItem("Average Holding Time", avgTimeHolding);

        Div body = new Div();
        body.addClassNames("section-card-wrapper");
        body.add(
                totalWorth, totalCost, numberOfAssets,
                profitStats, realizedProfit, unrealizedProfit,
                buySellRatio, avgHoldingTime
        );
        statisticSectionDetails.add(title, body);

        sectionWrapper.add(statisticSectionDetails, assetsDiversityChart);
        return sectionWrapper;
    }

    private Section performanceSection() {
        Section section = new Section();
        H3 title = new H3("Performance");
        title.setClassName("section-title");

        Map<Asset, Double> assetsProfits = instrumentsFacadeService.getAssetsWithNonZeroAmount()
                .stream()
                .collect(Collectors.toMap(asset -> asset, portfolioPerformanceTracker::getAssetProfit, (a, b) -> b));

        // Assets are the most profitable, the profit is realized
        Asset mostProfitableAsset = Collections.max(assetsProfits.entrySet(), Map.Entry.comparingByValue()).getKey();
        Asset leastProfitableAsset = Collections.min(assetsProfits.entrySet(), Map.Entry.comparingByValue()).getKey();

        Div body = new Div();
        body.addClassNames("section-card-wrapper");
        body.add(
                createPerformanceItem("Top Gainer", mostProfitableAsset),
                createPerformanceItem("Top Loser", leastProfitableAsset)
        );

        section.add(title, body);
        return section;
    }

    private Div createPerformanceItem(String labelText, Asset asset) {
        Div container = new Div();
        container.addClassName("performance-item");

        Paragraph label = new Paragraph(labelText);
        label.addClassName("performance-label");
        Paragraph assetSymbol = new Paragraph(asset.getSymbol());
        assetSymbol.addClassName("performance-symbol");

        Image assetImage = new Image(instrumentsFacadeService.getAssetImgUrl(asset), asset.getSymbol());
        assetImage.addClassNames("coin-overview-image", "performance-asset-image");

        double profit = portfolioPerformanceTracker.getAssetProfit(asset);
        double percentageProfit = portfolioPerformanceTracker.getAssetProfitPercentage(asset);
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

    private Div createStatsItem(String labelText, String valueText) {
        return createStatsItem(labelText, new Paragraph(valueText));
    }

    private Div createStatsItem(String labelText, Component valueComponent) {
        Paragraph label = new Paragraph(labelText);
        label.addClassName("stats-title");
        Div div = new Div(label, valueComponent);
        valueComponent.addClassName("stats-item");
        div.addClassName("portfolio-stats-details");
        return div;
    }

}
