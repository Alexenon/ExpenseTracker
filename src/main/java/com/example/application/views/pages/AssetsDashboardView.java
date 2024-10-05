package com.example.application.views.pages;

import com.example.application.data.models.NumberType;
import com.example.application.entities.crypto.Asset;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.services.crypto.PortfolioPerformanceTracker;
import com.example.application.views.components.AssetsGrid;
import com.example.application.views.components.complex_components.AssetValueParagraph;
import com.example.application.views.components.complex_components.PriceBadge;
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
@PageTitle("Assets Dashboard")
@Route(value = "assets-dashboard", layout = MainLayout.class)
public class AssetsDashboardView extends Main {

    private final InstrumentsFacadeService instrumentsFacadeService;
    private final PortfolioPerformanceTracker portfolioPerformanceTracker;

    @Autowired
    public AssetsDashboardView(InstrumentsFacadeService instrumentsFacadeService,
                               PortfolioPerformanceTracker portfolioPerformanceTracker) {
        this.instrumentsFacadeService = instrumentsFacadeService;
        this.portfolioPerformanceTracker = portfolioPerformanceTracker;

        AssetsGrid assetsGrid = new AssetsGrid(instrumentsFacadeService, portfolioPerformanceTracker);

        initializeGrid();
        add(performanceSection(), assetsGrid);
    }

    private void initializeGrid() {
        getStyle().set("margin", "100px 30px 30px 30px");
    }

    private Section performanceSection() {
        Section section = new Section();
        H3 title = new H3("Portfolio Statistics");
        title.setClassName("section-title");

        Map<Asset, Double> assetsProfits = instrumentsFacadeService.getAllAssets()
                .stream()
                .collect(Collectors.toMap(asset -> asset, portfolioPerformanceTracker::getAssetProfit, (a, b) -> b));

        // These assets are the ones that gave the most profit
        Asset mostProfitableAsset = Collections.max(assetsProfits.entrySet(), Map.Entry.comparingByValue()).getKey();
        Asset leastProfitableAsset = Collections.min(assetsProfits.entrySet(), Map.Entry.comparingByValue()).getKey();

        // Assets that grew the most from the avg buy rate
        Asset mostGrowingAsset;
        Asset leastGrowingAsset;

        Div profitLossContainer = Container.builder()
                .addClassName("price-profit-wrapper")
                .addComponent(new AssetValueParagraph(portfolioPerformanceTracker.getPortfolioProfit(), NumberType.CURRENCY))
                .addComponent(() -> {
                    double percentage = portfolioPerformanceTracker.getPortfolioProfitPercentage();
                    PriceBadge priceBadge = new PriceBadge(percentage, NumberType.PERCENT);
                    priceBadge.setBackgroundColor(PriceBadge.Color.DEFAULT_BACKGROUND_COLOR);
                    return priceBadge;
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
