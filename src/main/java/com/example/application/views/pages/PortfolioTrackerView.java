package com.example.application.views.pages;

import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.CryptoTransaction;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.services.crypto.PortfolioPerformanceTracker;
import com.example.application.utils.common.number.CurrencyFormatter;
import com.example.application.views.components.AssetsGrid;
import com.example.application.views.components.TransactionsGrid;
import com.example.application.views.components.core.Container;
import com.example.application.views.components.custom.NumericValueParagraph;
import com.example.application.views.components.custom.dialogs.transactions.AddTransactionDialog;
import com.example.application.views.components.custom.fields.PricePercentageWrapper;
import com.example.application.views.components.custom.fields.stats.PortfolioStatsDisplay;
import com.example.application.views.layouts.MainLayout;
import com.example.application.views.pages.crypto.AssetDetailsView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoIcon;
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
    TODO:
        [!] Total Trading Volume
        [!] Add chart options -> byCost, byWorth
        [!] Fix chart categories to not display 0 values
 * */

@PermitAll
@PageTitle("Portfolio Tracker")
@Route(value = "portfolio", layout = MainLayout.class)
@JsModule("./themes/light_theme/components/javascript/fillPieChart.js")
@JavaScript("https://fastly.jsdelivr.net/npm/echarts@5.4.2/dist/echarts.min.js")
public class PortfolioTrackerView extends DefaultPage {

    // TODO: Export all these formaters into a class CommonFormatters
    //  where it would be all general formatters, like SHORT_CURRENCY_FORMATTER, LONG_CURRENCY_FORMATTER, ...
    // TODO: Same for data formatters
    private final static CurrencyFormatter currencyFormatter = CurrencyFormatter.withDefaults();

    @Autowired
    private InstrumentsFacadeService instrumentsFacadeService;
    @Autowired
    private PortfolioPerformanceTracker portfolioPerformanceTracker;

    private AssetsGrid assetsGrid;
    private TransactionsGrid transactionsGrid;
    private final Div assetsDiversityChart = new Div();

    @Override
    protected void initializePage() {
        getStyle().set("margin", "100px 30px 30px 30px");
    }

    protected void initializeGrids() {
        assetsGrid = new AssetsGrid(instrumentsFacadeService, portfolioPerformanceTracker);
        assetsGrid.setGridFullSize(true);
        assetsGrid.setItems(instrumentsFacadeService.getAssetsWithNonZeroAmount());

        transactionsGrid = new TransactionsGrid(instrumentsFacadeService);
        transactionsGrid.setItems(instrumentsFacadeService.getAllTransactions());
        transactionsGrid.setPageSize(10);
        transactionsGrid.addUpdateItemListener(l -> rebuildPage());
    }

    @Override
    protected void buildPage() {
        initializeChart();
        initializeGrids();
        add(
                headerSection(),
                statisticsSection(),
                performanceSection(),
                gridSection("Assets", assetsGrid),
                gridSection("Transactions", transactionsGrid)
        );
    }

    private Section headerSection() {
        Section section = new Section();
        section.addClassName("asset-details-header");

        NumericValueParagraph worth = new NumericValueParagraph(portfolioPerformanceTracker.getPortfolioWorth(), currencyFormatter);
        double percentage = portfolioPerformanceTracker.getPortfolioProfitPercentage();
        double profit = portfolioPerformanceTracker.getPortfolioTotalProfit();
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
        section.add(addTransactionBtn);

        return section;
    }

    private void initializeChart() {
        assetsDiversityChart.setId("assets-diverstity-chart");

        Map<String, Double> assetsDiversity = instrumentsFacadeService.getAssetsWithNonZeroAmount()
                .stream()
                .collect(Collectors.toMap(Asset::getSymbol, portfolioPerformanceTracker::getAssetRemainingTokensCost, (a, b) -> b));

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

        double totalProfit = portfolioPerformanceTracker.getPortfolioTotalProfit();
        String nrOfAssets = String.valueOf(instrumentsFacadeService.getAssetsWithNonZeroAmount().size());
        String realized = currencyFormatter.format(portfolioPerformanceTracker.getPortfolioRealizedProfit());
        String unrealized = currencyFormatter.format(portfolioPerformanceTracker.getPortfolioUnrealizedProfit());
        String avgTimeHolding = String.format("%.1f days", portfolioPerformanceTracker.getPortfolioAverageHoldingDays());

        Div totalWorth = new PortfolioStatsDisplay("Total Worth", currencyFormatter.format(portfolioPerformanceTracker.getPortfolioWorth()),
                "Total value of all your holdings based on the latest price");
        Div totalCost = new PortfolioStatsDisplay("Total Cost", currencyFormatter.format(portfolioPerformanceTracker.getPortfolioCost()),
                "Total amount of dollars invested to buy all the assets");
        Div numberOfAssets = new PortfolioStatsDisplay("No. of Assets", nrOfAssets,
                "Current number of assets that are in your portfolio");
        Div profitStats = new PortfolioStatsDisplay("Total Profit", new NumericValueParagraph(totalProfit, currencyFormatter, true),
                "Represents the realized profit + unrealized profit");
        Div realizedProfit = new PortfolioStatsDisplay("Realized Profit", realized,
                "Profit or Loss from all your sold holdings");
        Div unrealizedProfit = new PortfolioStatsDisplay("Unrealized Profit", unrealized,
                "Potential profit or loss if you were to sell all assets now");
        String ratio = portfolioPerformanceTracker.getPortfolioBuySellRatio();
        String[] ratioParts = ratio.split(":");
        Div buySellRatio = new PortfolioStatsDisplay("Buy/Sell % Ratio", ratio,
                String.format("%s%% of transactions are buys, %s%% are sells, in dollar equivalent", ratioParts[0].trim(), ratioParts[1]));
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

        sectionWrapper.add(statisticSectionDetails, assetsDiversityChart);
        return sectionWrapper;
    }

    private Section performanceSection() {
        Map<Asset, Double> assetsProfits = getMostProfitableAssetsByProfit();
        if (assetsProfits.isEmpty()) {
            return new Section();
        }

        Section section = new Section();
        H3 title = new H3("Performance");
        title.setClassName("section-title");

        Asset mostProfitableAsset = Collections.max(assetsProfits.entrySet(), Map.Entry.comparingByValue()).getKey();
        Asset leastProfitableAsset = Collections.min(assetsProfits.entrySet(), Map.Entry.comparingByValue()).getKey();

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

        Image assetImage = new Image(instrumentsFacadeService.getAssetImgUrl(asset), asset.getSymbol());
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

}
