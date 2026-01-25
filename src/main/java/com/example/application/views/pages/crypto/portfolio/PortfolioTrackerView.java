package com.example.application.views.pages.crypto.portfolio;

import com.example.application.data.dtos.PortfolioDTO;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.services.crypto.PortfolioPerformanceTracker;
import com.example.application.views.components.PriceChangeNotifier;
import com.example.application.views.components.portfolio.dialogs.AddPortfolioDialog;
import com.example.application.views.components.portfolio.dialogs.PortfolioCreatedOrUpdatedEvent;
import com.example.application.views.layouts.MainLayout;
import com.example.application.views.pages.DefaultPage;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
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

import java.util.List;
import java.util.Optional;

@Slf4j
@PermitAll
@PageTitle("Portfolio Tracker")
@Route(value = "panel", layout = MainLayout.class)
@JsModule("./themes/light_theme/components/javascript/fillPieChart.js")
@JavaScript("https://fastly.jsdelivr.net/npm/echarts@5.4.2/dist/echarts.min.js")
public class PortfolioTrackerView extends DefaultPage {

	private final InstrumentsFacadeService instrumentsFacadeService;
	private final PortfolioPerformanceTracker portfolioPerformanceTracker;
	private final PriceChangeNotifier priceChangeNotifier;

	private final UI ui;
	private final Button addPortfolioBtn = new Button(LumoIcon.PLUS.create());
	private final Select<PortfolioDTO> portfolioSelector = new Select<>();

	private PortfolioPanel portfolioPanel;

	@Autowired
	public PortfolioTrackerView(InstrumentsFacadeService instrumentsFacadeService,
								PortfolioPerformanceTracker portfolioPerformanceTracker,
								PriceChangeNotifier priceChangeNotifier)
	{
		this.instrumentsFacadeService = instrumentsFacadeService;
		this.portfolioPerformanceTracker = portfolioPerformanceTracker;
		this.priceChangeNotifier = priceChangeNotifier;
		this.ui = UI.getCurrent();
		initializePage();
	}

	private void initializePage() {
		getStyle().set("margin-top", "100px");
		intializeFields();
		buildPage(instrumentsFacadeService.getActivePortfolio());

		ComponentUtil.addListener(UI.getCurrent(), PortfolioCreatedOrUpdatedEvent.class,
				event -> ui.access(() -> rebuildPage(event.getPortfolio())));
	}

	private void intializeFields() {
		portfolioSelector.setLabel("Portfolio");
		portfolioSelector.setItemLabelGenerator(PortfolioDTO::getName);
		portfolioSelector.setEmptySelectionAllowed(false);
		portfolioSelector.addValueChangeListener(event -> {
			PortfolioDTO selectedPortfolio = event.getValue();

			if(selectedPortfolio == null)
				return;

			instrumentsFacadeService.setPortfolioAsActive(selectedPortfolio.getId());
			ui.access(() -> updatePortfolioPanel(selectedPortfolio));
		});

		addPortfolioBtn.addClickListener(e -> new AddPortfolioDialog(instrumentsFacadeService).open());
	}

	private void buildPage(PortfolioDTO portfolio) {
		List<PortfolioDTO> updatedPortfolioList = instrumentsFacadeService.getUserPortfolios();
		portfolioSelector.setItems(updatedPortfolioList);
		portfolioSelector.setValue(portfolio);
		updatePortfolioPanel(portfolio);
		add(addPortfolioBtn, portfolioSelector);
	}

	private void rebuildPage(PortfolioDTO portfolio) {
		this.removeAll();
		buildPage(portfolio);
	}

	private void updatePortfolioPanel(PortfolioDTO portfolio) {
		Optional.ofNullable(portfolioPanel).ifPresent(Component::removeFromParent);
		portfolioPanel = new PortfolioPanel(portfolio, instrumentsFacadeService, portfolioPerformanceTracker, priceChangeNotifier);
		add(portfolioPanel);
	}

}