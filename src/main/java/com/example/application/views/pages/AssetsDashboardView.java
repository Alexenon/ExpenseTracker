package com.example.application.views.pages;

import com.example.application.data.models.NumberType;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.services.crypto.PortfolioPerformanceTracker;
import com.example.application.views.components.AssetsGrid;
import com.example.application.views.components.complex_components.PriceBadge;
import com.example.application.views.components.native_components.Container;
import com.example.application.views.layouts.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

/*
 * TODO:
 *   - Finish performance Section
 * */

@PermitAll
@PageTitle("Assets Dashboard")
@Route(value = "assets-dashboard", layout = MainLayout.class)
public class AssetsDashboardView extends Main {

    private final InstrumentsFacadeService instrumentsFacadeService;
    private final PortfolioPerformanceTracker portfolioPerformanceTracker;
    private final AssetsGrid assetsGrid;

    @Autowired
    public AssetsDashboardView(InstrumentsFacadeService instrumentsFacadeService,
                               PortfolioPerformanceTracker portfolioPerformanceTracker) {
        this.instrumentsFacadeService = instrumentsFacadeService;
        this.portfolioPerformanceTracker = portfolioPerformanceTracker;

        this.assetsGrid = new AssetsGrid(instrumentsFacadeService, portfolioPerformanceTracker);

        initializeGrid();
        add(performanceSection(), assetsGrid);
    }

    private void initializeGrid() {
        getStyle().set("margin", "100px 30px 30px 30px");
    }

    // TODO: Replace with actual values
    private Section performanceSection() {
        Section section = new Section();
        H3 title = new H3("Portfolio Statistics");
        title.setClassName("section-title");

        Div body = new Div();
        body.addClassNames("section-card-wrapper");

        // TODO: Add hints for help
        Div totalWorth = createStatsItem("Total Worth", NumberType.CURRENCY.parse(portfolioPerformanceTracker.getPortfolioWorth()));
        Div totalCost = createStatsItem("Total Cost", NumberType.CURRENCY.parse(portfolioPerformanceTracker.getPortfolioCost()));

        Container profitWrapper = Container.builder()
                .addComponent(() -> {
                    String profit = NumberType.CURRENCY.parse(6250);
                    return new Paragraph(profit);
                })
                // TODO: When should compare percent change ???
                //  Previous month ?
                //  Previous transaction ?
                .addComponent(() -> new PriceBadge(34.23, NumberType.PERCENT))
                .build();

        Div profitStats = createStatsItem("Profit", profitWrapper);

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
