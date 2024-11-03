package com.example.application.views.pages.crypto.comparator;

import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.views.components.native_components.Container;
import com.example.application.views.layouts.MainLayout;
import com.example.application.views.pages.crypto.comparator.tabs.SellProfitTab;
import com.example.application.views.pages.crypto.comparator.tabs.StakingProfitTab;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

@PermitAll
@PageTitle("Asset Details")
@Route(value = "details", layout = MainLayout.class)
public class AssetCompareView extends Main {

    private final InstrumentsFacadeService instrumentsFacadeService;

    @Autowired
    public AssetCompareView(InstrumentsFacadeService instrumentsFacadeService) {
        this.instrumentsFacadeService = instrumentsFacadeService;
        buildPage();
    }

    private void buildPage() {
        getStyle().set("margin", "100px 0 50px 0");

        Tab sellProfitTab = new SellProfitTab(instrumentsFacadeService);
        Tab stakingProfitTab = new StakingProfitTab(instrumentsFacadeService);

        TabSheet tabSheet = new TabSheet();
        tabSheet.setId("comparator-tabs");

        tabSheet.add("Profit Calculator", sellProfitTab);
        tabSheet.add("Staking Calculator", stakingProfitTab);
        tabSheet.add("Compare Assets", getComparationTab());

        add(tabSheet);
    }

    private Tab getComparationTab() {
        Tab tab = new Tab();
        tab.addClassName("compare-tab-content");

        H3 title = new H3("Compare assets");
        SellProfitTab assetForm1 = new SellProfitTab(instrumentsFacadeService);
        SellProfitTab assetForm2 = new SellProfitTab(instrumentsFacadeService);
        Container comparationBody = new Container("comparation-body", assetForm1, assetForm2);
        Button compareBtn = new Button("Compare");
        Div output = new Div();

        assetForm1.addClassName("asset-compare-form");
        assetForm2.addClassName("asset-compare-form");

        tab.add(title, comparationBody, compareBtn, output);
        return tab;
    }


}
