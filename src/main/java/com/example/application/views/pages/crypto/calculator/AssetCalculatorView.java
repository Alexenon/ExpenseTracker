package com.example.application.views.pages.crypto.calculator;

import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.services.crypto.PortfolioPerformanceTracker;
import com.example.application.views.layouts.MainLayout;
import com.example.application.views.pages.AbstractPage;
import com.example.application.views.pages.crypto.calculator.tabs.ProfitEmulatorTab;
import com.example.application.views.pages.crypto.calculator.tabs.SellProfitTab;
import com.example.application.views.pages.crypto.calculator.tabs.StakingProfitTab;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

@PermitAll
@PageTitle("Calculator")
@Route(value = "calculator", layout = MainLayout.class)
public class AssetCalculatorView extends AbstractPage {

    private final InstrumentsFacadeService instrumentsFacadeService;
    private final PortfolioPerformanceTracker portfolioPerformanceTracker;

    @Autowired
    public AssetCalculatorView(InstrumentsFacadeService instrumentsFacadeService, PortfolioPerformanceTracker portfolioPerformanceTracker) {
        this.instrumentsFacadeService = instrumentsFacadeService;
        this.portfolioPerformanceTracker = portfolioPerformanceTracker;
        buildPage();
    }

    private void buildPage() {
        getStyle().set("margin", "100px 0 50px 0");

        Tab sellProfitTab = new SellProfitTab(instrumentsFacadeService, portfolioPerformanceTracker);
        Tab stakingProfitTab = new StakingProfitTab(instrumentsFacadeService);
        Tab profitEmulatorTab = new ProfitEmulatorTab(instrumentsFacadeService, portfolioPerformanceTracker);

        TabSheet tabSheet = new TabSheet();
        tabSheet.setId("comparator-tabs");

        tabSheet.add("Profit Calculator", sellProfitTab);
        tabSheet.add("Staking Calculator", stakingProfitTab);
        tabSheet.add("Profit Emulator", profitEmulatorTab);

        add(tabSheet);
    }


}
