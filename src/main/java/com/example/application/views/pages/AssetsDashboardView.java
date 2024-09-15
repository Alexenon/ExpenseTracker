package com.example.application.views.pages;

import com.example.application.data.models.InstrumentsProvider;
import com.example.application.data.models.NumberType;
import com.example.application.services.SecurityService;
import com.example.application.services.UserService;
import com.example.application.services.crypto.AssetWatcherService;
import com.example.application.views.components.AssetsGrid;
import com.example.application.views.components.complex_components.PriceBadge;
import com.example.application.views.components.native_components.Container;
import com.example.application.views.layouts.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.beans.factory.annotation.Autowired;

/*
 * TODO:
 *   - Finish performance Section
 * */

@AnonymousAllowed
@PageTitle("Assets Dashboard")
@Route(value = "assets-dashboard", layout = MainLayout.class)
public class AssetsDashboardView extends Main {

    private final AssetWatcherService assetWatcherService;
    private final InstrumentsProvider instrumentsProvider;
    private final SecurityService securityService;
    private final UserService userService;

    private final AssetsGrid assetsGrid = new AssetsGrid();

    @Autowired
    public AssetsDashboardView(
            InstrumentsProvider instrumentsProvider,
            AssetWatcherService assetWatcherService,
            SecurityService securityService,
            UserService userService) {
        this.instrumentsProvider = instrumentsProvider;
        this.assetWatcherService = assetWatcherService;
        this.securityService = securityService;
        this.userService = userService;

        initializeGrid();
        add(performanceSection(), assetsGrid);
    }

    private void initializeGrid() {
        getStyle().set("margin", "100px 30px 30px 30px");
        assetsGrid.setItems(instrumentsProvider.getListOfAssetData());
        assetsGrid.addClickSyncBtnListener(grid -> {
            instrumentsProvider.updateAssetData();
            assetsGrid.setItems(instrumentsProvider.getListOfAssetData());
        });

    }

    // TODO: Replace with actual values
    private Section performanceSection() {
        Section section = new Section();
        H3 title = new H3("Portfolio Statistics");
        title.setClassName("section-title");

        Div body = new Div();
        body.addClassNames("section-card-wrapper");

        // TODO: Add hints for help
        Div totalWorth = createStatsItem("Total Worth", "$16,250"); // How much costs your assets now
        Div totalCost = createStatsItem("Total Cost", "$10,000");   // Total cost of your assets

        Container profitWrapper = Container.builder()
                .addComponent(() -> {
                    String profit = NumberType.CURRENCY.parse(6250);
                    return new Paragraph(profit);
                })
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
