package com.example.application.views.pages.crypto.portfolio;

import com.example.application.entities.crypto.Portfolio;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.services.crypto.PortfolioPerformanceTracker;
import com.example.application.views.components.PriceChangeHandler;
import com.example.application.views.components.portfolio.AddPortfolioDialog;
import com.example.application.views.layouts.MainLayout;
import com.example.application.views.pages.DefaultPage;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoIcon;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@PermitAll
@PageTitle("Portfolio Tracker")
@Route(value = "panel", layout = MainLayout.class)
@JsModule("./themes/light_theme/components/javascript/fillPieChart.js")
@JavaScript("https://fastly.jsdelivr.net/npm/echarts@5.4.2/dist/echarts.min.js")
public class TrackerView extends DefaultPage {

	private final InstrumentsFacadeService instrumentsFacadeService;
	private final PortfolioPerformanceTracker portfolioPerformanceTracker;
	private final PriceChangeHandler priceChangeHandler;

	private final UI ui;
	private final Button addPortfolioBtn = new Button(LumoIcon.PLUS.create());
	private final Select<Portfolio> portfolioSelector = new Select<>();

	private Portfolio portfolio;
	private PortfolioDisplayPanel portfolioDisplayPanel;

	@Autowired
	public TrackerView(InstrumentsFacadeService instrumentsFacadeService,
					   PortfolioPerformanceTracker portfolioPerformanceTracker,
					   PriceChangeHandler priceChangeHandler)
	{
		this.instrumentsFacadeService = instrumentsFacadeService;
		this.portfolioPerformanceTracker = portfolioPerformanceTracker;
		this.priceChangeHandler = priceChangeHandler;
		this.ui = UI.getCurrent();
		initializePage();
	}

	private void initializePage() {
		Portfolio defaultPortfolio = instrumentsFacadeService.getAuthenticatedUserMainPortfolio();
		updatePortfolio(defaultPortfolio);

		getStyle().set("margin-top", "100px");
		buildPage();
	}

	public void buildPage() {
		portfolioSelector.setLabel("Portfolio");
		portfolioSelector.setItems(instrumentsFacadeService.getUserPortfolios());
		portfolioSelector.setItemLabelGenerator(Portfolio::getName);
		portfolioSelector.setValue(portfolio);
		portfolioSelector.addValueChangeListener(field -> updatePortfolio(field.getValue()));

		addPortfolioBtn.addClickListener(event -> {
			AddPortfolioDialog dialog = new AddPortfolioDialog(instrumentsFacadeService);
			dialog.open();
			dialog.addOnSuccessfullSaveListener(l -> dialog.getPortfolio().ifPresent(this::updatePortfolio));
		});

		add(
				addPortfolioBtn,
				portfolioSelector,
				portfolioDisplayPanel
		);
	}

	private void updatePortfolio(Portfolio portfolio) {
		this.portfolio = portfolio;
		this.portfolioDisplayPanel = new PortfolioDisplayPanel(portfolio, instrumentsFacadeService, portfolioPerformanceTracker);
		portfolioDisplayPanel.build();
	}


}
