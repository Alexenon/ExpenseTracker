package com.example.application.views.pages;

import com.example.application.data.models.NumberType;
import com.example.application.entities.crypto.Asset;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.services.crypto.PortfolioPerformanceTracker;
import com.example.application.views.components.AssetsGrid;
import com.example.application.views.components.TransactionsGrid;
import com.example.application.views.components.complex_components.PercentageBadge;
import com.example.application.views.components.complex_components.ProfitValueParagraph;
import com.example.application.views.components.complex_components.fields.PricePercentageWrapper;
import com.example.application.views.components.native_components.Container;
import com.example.application.views.layouts.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@PermitAll
@PageTitle("Portfolio Tracker")
@Route(value = "portfolio-tracker", layout = MainLayout.class)
public class PortfolioTrackerView extends Main {

    private final InstrumentsFacadeService instrumentsFacadeService;
    private final PortfolioPerformanceTracker portfolioPerformanceTracker;

    private final AssetsGrid assetsGrid;
    private final TransactionsGrid transactionsGrid;

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
                performanceSection(),
                gridSection("Assets", assetsGrid),
                gridSection("Transactions", transactionsGrid)
        );
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

        ProfitValueParagraph worth = new ProfitValueParagraph(portfolioPerformanceTracker.getPortfolioWorth());
        double percentage = portfolioPerformanceTracker.getPortfolioProfitPercentage();
        double profit = portfolioPerformanceTracker.getPortfolioProfit();
        PricePercentageWrapper profitWrapper = new PricePercentageWrapper(profit, percentage);

        Container portfolioWorthWrapper = new Container("price-wrapper", worth, profitWrapper);

        section.add(portfolioWorthWrapper);
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

    @SuppressWarnings("unused")
    private Section performanceSection() {
        Section section = new Section();
        H3 title = new H3("Portfolio Statistics");
        title.setClassName("section-title");

        Map<Asset, Double> assetsProfits = instrumentsFacadeService.getAllAssets()
                .stream()
                .collect(Collectors.toMap(asset -> asset, portfolioPerformanceTracker::getAssetProfit, (a, b) -> b));

        // TODO: These assets are the ones that gave the most profit
        //  - Add link to these assets
        Asset mostProfitableAsset = Collections.max(assetsProfits.entrySet(), Map.Entry.comparingByValue()).getKey();
        Asset leastProfitableAsset = Collections.min(assetsProfits.entrySet(), Map.Entry.comparingByValue()).getKey();

        // TODO: Assets that grew the most from the avg buy rate
        Asset mostGrowingAsset;
        Asset leastGrowingAsset;

        // TODO: How much amount of holding asset token to sell, to be in 0
        //  How much remains, how much profit is it ?

        Div profitLossContainer = Container.builder()
                .addClassName("price-profit-wrapper")
                .addComponent(new ProfitValueParagraph(portfolioPerformanceTracker.getPortfolioProfit(), NumberType.CURRENCY))
                .addComponent(() -> {
                    double percentage = portfolioPerformanceTracker.getPortfolioProfitPercentage();
                    PercentageBadge percentageBadge = new PercentageBadge(percentage);
                    percentageBadge.setHasBackground(false);
                    return percentageBadge;
                })
                .build();

        // TODO: Add hints for help
        Div totalWorth = createStatsItem("Total Worth", NumberType.CURRENCY.parse(portfolioPerformanceTracker.getPortfolioWorth()));
        Div totalCost = createStatsItem("Total Cost", NumberType.CURRENCY.parse(portfolioPerformanceTracker.getPortfolioCost()));
        Div profitStats = createStatsItem("Profit", profitLossContainer);

        Div body = new Div();
        body.addClassNames("section-card-wrapper");
        body.add(totalWorth, totalCost, profitStats);
        section.add(body);
        return section;
    }

    private Div createStatsItem(String labelText, String valueText) {
        return createStatsItem(labelText, new Paragraph(valueText));
    }

    private Div createStatsItem(String labelText, Component valueComponent) {
        Paragraph label = new Paragraph(labelText);
        label.addClassName("stats-title");
        Div div = new Div(label, valueComponent);
        valueComponent.addClassName("stats-item");
        div.addClassName("stats-details-wrapper");
        return div;
    }

}
