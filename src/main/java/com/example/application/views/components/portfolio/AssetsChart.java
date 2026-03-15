package com.example.application.views.components.portfolio;

import com.example.application.data.dtos.AssetBalanceDTO;
import com.example.application.data.dtos.AssetDTO;
import com.example.application.data.dtos.PortfolioDTO;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.services.crypto.PortfolioPerformanceTracker;
import com.example.application.utils.common.lang.MathUtils;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.select.Select;
import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class AssetsChart extends Div {

	private final PortfolioDTO portfolio;
	private final InstrumentsFacadeService instrumentsFacadeService;
	private final PortfolioPerformanceTracker portfolioPerformanceTracker;

	private final Select<ChartOptions> options = new Select<>();

	@Autowired
	public AssetsChart(PortfolioDTO portfolio,
					   InstrumentsFacadeService instrumentsFacadeService,
					   PortfolioPerformanceTracker portfolioPerformanceTracker)
	{
		this.portfolio = portfolio;
		this.instrumentsFacadeService = instrumentsFacadeService;
		this.portfolioPerformanceTracker = portfolioPerformanceTracker;
		initialize();
	}

	private void initialize() {
		addClassName("assets-chart-container");
		options.setLabel("Group by");
		options.setItems(ChartOptions.values());
		options.setValue(ChartOptions.WORTH);
		options.addValueChangeListener(e -> updateChartItems());
		add(
				options,
				createChart()
		);
		updateChartItems();
	}

	private Div createChart() {
		Div chart = new Div();
		chart.setId("assets-diverstity-chart");
		return chart;
	}

	public void updateChartItems() {
		JsonArray jsonOptionData = Json.createArray();
		AtomicInteger index = new AtomicInteger(0);
		getChartItems().forEach((assetName, diversityPercentage) -> {
			JsonObject jsonObject = Json.createObject();
			jsonObject.put("name", assetName);
			jsonObject.put("value", MathUtils.twoDecimal(diversityPercentage));
			jsonOptionData.set(index.get(), jsonObject);
			index.addAndGet(1);
		});

		getUI().ifPresent(ui -> ui.getPage().executeJs("fillAssetsDiversityChart($0);", jsonOptionData.toJson()));
		log.info("Created assets pie chart with {} elements", index.intValue());
	}

	private Map<String, Double> getChartItems() {
		return instrumentsFacadeService.getPorfolioAssetBalances(portfolio.getId())
				.stream()
				.map(AssetBalanceDTO::getAssetSymbol)
				.map(s -> instrumentsFacadeService.getAssetBySymbol(s).orElseThrow())
				.collect(Collectors.toMap(AssetDTO::getSymbol, chartMapper(), (a, b) -> b));
	}

	private Function<AssetDTO, Double> chartMapper() {
		return switch (options.getValue()) {
			case WORTH -> asset -> portfolioPerformanceTracker.getAssetWorth(portfolio, asset);
			case INVESTED -> asset -> portfolioPerformanceTracker.getAssetRemainingTokensCost(portfolio, asset);
		};
	}

	private enum ChartOptions {
		WORTH,
		INVESTED
	}

}
