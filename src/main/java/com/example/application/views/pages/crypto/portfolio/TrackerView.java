//package com.example.application.views.pages.crypto.portfolio;
//
//import com.example.application.entities.crypto.Portfolio;
//import com.example.application.services.crypto.InstrumentsFacadeService;
//import com.example.application.services.crypto.PortfolioPerformanceTracker;
//import com.example.application.views.components.PriceChangeHandler;
//import com.example.application.views.layouts.MainLayout;
//import com.example.application.views.pages.DefaultPage;
//import com.vaadin.flow.component.UI;
//import com.vaadin.flow.component.dependency.JavaScript;
//import com.vaadin.flow.component.dependency.JsModule;
//import com.vaadin.flow.component.select.Select;
//import com.vaadin.flow.router.PageTitle;
//import com.vaadin.flow.router.Route;
//import jakarta.annotation.security.PermitAll;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//
//@Slf4j
//@PermitAll
//@PageTitle("Portfolio Tracker")
//@Route(value = "portfolio", layout = MainLayout.class)
//@JsModule("./themes/light_theme/components/javascript/fillPieChart.js")
//@JavaScript("https://fastly.jsdelivr.net/npm/echarts@5.4.2/dist/echarts.min.js")
//public class TrackerView extends DefaultPage {
//
//    private final PriceChangeHandler priceChangeHandler;
//    private final InstrumentsFacadeService instrumentsFacadeService;
//    private final PortfolioPerformanceTracker portfolioPerformanceTracker;
//    private final Select<Portfolio> selectPortfolio = new Select<>();
//    private final PortfolioPanel panel;
//    private final UI ui;
//
//    @Autowired
//    public TrackerView(InstrumentsFacadeService instrumentsFacadeService,
//                       PortfolioPerformanceTracker portfolioPerformanceTracker,
//                       PriceChangeHandler priceChangeHandler)
//    {
//        this.instrumentsFacadeService = instrumentsFacadeService;
//        this.portfolioPerformanceTracker = portfolioPerformanceTracker;
//        this.priceChangeHandler = priceChangeHandler;
//        this.panel = new PortfolioPanel(instrumentsFacadeService, portfolioPerformanceTracker);
//        this.ui = UI.getCurrent();
//        initializePage();
//    }
//
//    private void initializePage() {
//
//    }
//
//    public void buildPage() {
//        selectPortfolio.setLabel("Portfolio");
//        selectPortfolio.setItems(instrumentsFacadeService.getPortfolios());
//        selectPortfolio.setItemLabelGenerator(Portfolio::getName);
//        selectPortfolio.addValueChangeListener(e -> {
//            // TODO: HERE
//        });
//
//        add(
//
//        );
//    }
//
//    private void updatePortfolioPanel() {
//
//    }
//
//
//}
