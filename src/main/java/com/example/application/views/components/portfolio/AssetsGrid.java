package com.example.application.views.components.portfolio;

import com.example.application.data.dtos.AssetDTO;
import com.example.application.data.dtos.PortfolioDTO;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.services.crypto.PortfolioPerformanceTracker;
import com.example.application.utils.common.formatters.CommonFormatters;
import com.example.application.views.components.core.Container;
import com.example.application.views.components.custom.display.PercentageBadge;
import com.example.application.views.components.utils.common.GridUtils;
import com.example.application.views.pages.crypto.AssetDetailsView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.theme.lumo.LumoIcon;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.ToDoubleFunction;

/*
    TODO: [LONG TERM]
        => Columns:
            [!] Add Profit (TOTAL) of an asset, by price + percentage (One on top of another)
            [!] Closest Buy ->  $34,000.00 (URGENT icon ❗ -> vaadin:exclamation vaadin:warning)
            [?] Add checkbox on the grid, to hide item tooltip
        => Others:
            [?] Dont allow to remove the asset name from column toggle component
                - cursor: not-allowed;
            [?] grid.setMultiSort(true, MultiSortPriority.APPEND);

     Optimize:
        [!!] REMOVE certain columns from ColumnSelector instead of HIDING
    _____________________________________________________________________________________________________________
    | Name | Price  | 24h Changes | Amount | Avg buy | Avg sell | Total Worth | Invested | Realized | Profit    |
    | BTC  | $64000 | 2%          | 0.0034 | $60000  |    -     | $230        | $200     |     -    | $30 / 15% |
    _____________________________________________________________________________________________________________
*/
public class AssetsGrid extends Div {

	private static final String MISSING_DATA_SIGN = "-";
	private static final int DEFAULT_NUMBER_OF_COLUMNS_VISIBLE = 8;

	private final PortfolioDTO portfolio;
	private final InstrumentsFacadeService instrumentsFacadeService;
	private final PortfolioPerformanceTracker portfolioPerformanceTracker;

	private final TextField searchField = new TextField();
	private final Button syncButton = new Button("Sync", LumoIcon.RELOAD.create());
	private final Checkbox hideAssetsCheckbox = new Checkbox("Hide 0 amount assets");

	private final Grid<AssetGridItem> grid = new Grid<>();
	private final Span hiddenRowsCounterField = new Span();
	private GridListDataView<AssetGridItem> dataView;
	private List<AssetDTO> assets = new ArrayList<>();

	private Grid.Column<AssetGridItem> changes24hCol;
	private Grid.Column<AssetGridItem> totalWorthCol;
	private Grid.Column<AssetGridItem> totalCostCol;
	private Grid.Column<AssetGridItem> realizedCol;
	private Grid.Column<AssetGridItem> unrealizedCol;

	public AssetsGrid(PortfolioDTO portfolio,
					  InstrumentsFacadeService instrumentsFacadeService,
					  PortfolioPerformanceTracker portfolioPerformanceTracker)
	{
		this.portfolio = portfolio;
		this.instrumentsFacadeService = instrumentsFacadeService;
		this.portfolioPerformanceTracker = portfolioPerformanceTracker;

		setItems(assets);

		initializeGrid();
		initializeFilteringBySearch();
		initializeFilteringNonZeroValues();
		initializeSyncButton();

		add(
				gridHeader(),
				grid,
				hiddenRowsContainer()
		);
	}

	private Div gridHeader() {
		Div syncContainer = new Div(syncButton, hideAssetsCheckbox);
		syncContainer.addClassName("sync-container");

		Div header = new Div(searchField, syncContainer);
		header.addClassName("assets-grid-header");
		return header;
	}

	private void initializeGrid() {
		renderColumns();
		grid.setAllRowsVisible(true);
		grid.setColumnReorderingAllowed(true);
		grid.addItemClickListener(row -> {
			System.out.println(row.getItem());
			getUI().ifPresent(ui -> ui.navigate(AssetDetailsView.class, row.getItem().getSymbol()));
		});

		setHiddenRowCount(0);
		grid.addAttachListener(e -> updateColumnFooters());
	}

	private void renderColumns() {
		grid.addColumn(columnNameRenderer())
				.setHeader("Name")
				.setAutoWidth(true)
				.setSortable(true)
				.setFrozen(true)
				.setComparator(AssetGridItem::getSymbol);

		grid.addColumn(columnColoredPriceRenderer())
				.setHeader("Price")
				.setTextAlign(ColumnTextAlign.END)
				.setAutoWidth(true)
				.setSortable(true)
				.setComparator(AssetGridItem::getPrice);

		changes24hCol = grid.addColumn(columnChanges24hRenderer())
				.setHeader("Changes 24h")
				.setTextAlign(ColumnTextAlign.CENTER)
				.setAutoWidth(true)
				.setSortable(true)
				.setComparator(AssetGridItem::getPriceChangesPercentage24h);

		grid.addColumn(GridUtils.columnPriceRenderer(AssetGridItem::getAvgBuy))
				.setHeader("Avg Buy")
				.setAutoWidth(true)
				.setTextAlign(ColumnTextAlign.CENTER);

		grid.addColumn(GridUtils.columnPriceRenderer(AssetGridItem::getAvgSell))
				.setHeader("Avg Sell")
				.setAutoWidth(true)
				.setTextAlign(ColumnTextAlign.CENTER);

		grid.addColumn(GridUtils.columnAmountRenderer(AssetGridItem::getTokenAmount))
				.setHeader("Amount")
				.setTextAlign(ColumnTextAlign.CENTER)
				.setAutoWidth(true)
				.setSortable(true)
				.setComparator(AssetGridItem::getTokenAmount);

		totalWorthCol = grid.addColumn(GridUtils.columnPriceRenderer(AssetGridItem::getTotalWorth))
				.setHeader("Total Worth")
				.setTextAlign(ColumnTextAlign.END)
				.setAutoWidth(true)
				.setSortable(true)
				.setComparator(AssetGridItem::getTotalWorth)
				.setTooltipGenerator(a -> "Total value of your %s holdings based on the latest price.".formatted(a.getSymbol()));

		totalCostCol = grid.addColumn(GridUtils.columnPriceRenderer(AssetGridItem::getTotalCost))
				.setHeader("Total Cost")
				.setTextAlign(ColumnTextAlign.END)
				.setAutoWidth(true)
				.setSortable(true)
				.setComparator(AssetGridItem::getTotalCost)
				.setTooltipGenerator(a -> "Total cost of your %s holdings(How much you have invested)".formatted(a.getSymbol()));

		grid.addColumn(GridUtils.columnPercentageRenderer(AssetGridItem::getDiversityPercentage))
				.setHeader("Diversity")
				.setTextAlign(ColumnTextAlign.CENTER)
				.setAutoWidth(true)
				.setSortable(true)
				.setComparator(AssetGridItem::getDiversityPercentage)
				.setTooltipGenerator(a -> "The percentage contribution of %s to your portfolio's total value.".formatted(a.getSymbol()));

		realizedCol = grid.addColumn(GridUtils.columnPriceRenderer(AssetGridItem::getRealizedProfit))
				.setHeader("Realized")
				.setTextAlign(ColumnTextAlign.CENTER)
				.setAutoWidth(true)
				.setSortable(true)
				.setComparator(AssetGridItem::getRealizedProfit)
				.setTooltipGenerator(a -> "Profit or loss from your sold %s holdings.".formatted(a.getSymbol()));

		unrealizedCol = grid.addColumn(GridUtils.columnPriceRenderer(AssetGridItem::getUnrealizedProfit))
				.setHeader("Unrealized")
				.setTextAlign(ColumnTextAlign.CENTER)
				.setAutoWidth(true)
				.setSortable(true)
				.setComparator(AssetGridItem::getUnrealizedProfit)
				.setTooltipGenerator(a -> "Potential profit or loss if you were to sell %s now.".formatted(a.getSymbol()));

		grid.addColumn(GridUtils.columnPriceRenderer(AssetGridItem::getUnrealizedProfit))
				.setHeader("Profit")
				.setTextAlign(ColumnTextAlign.CENTER)
				.setAutoWidth(true)
				.setSortable(true)
				.setComparator(AssetGridItem::getUnrealizedProfit)
				.setTooltipGenerator(a -> "Potential profit or loss if you were to sell %s now.".formatted(a.getSymbol()));

		grid.addColumn(GridUtils.columnPriceRenderer(AssetGridItem::getClosestBuy))
				.setHeader("Closest Buy")
				.setTextAlign(ColumnTextAlign.CENTER)
				.setAutoWidth(true)
				.setTooltipGenerator(a -> "The closest %s buy price that was added in the watcher".formatted(a.getSymbol()));

		grid.addColumn(GridUtils.columnPriceRenderer(AssetGridItem::getClosestSell))
				.setHeader("Closest Sell")
				.setTextAlign(ColumnTextAlign.CENTER)
				.setAutoWidth(true)
				.setTooltipGenerator(a -> "The closest %s sell price that was added in the watcher".formatted(a.getSymbol()));

		grid.addColumn(GridUtils.columnPercentageRenderer(AssetGridItem::getAvgBuyCompareWithCurrentPrice))
				.setHeader("Avg Buy vs Current Price")
				.setTextAlign(ColumnTextAlign.CENTER)
				.setAutoWidth(true)
				.setTooltipGenerator(a -> "Shows how the average buy price of %s compares to the current price".formatted(a.getSymbol()));

		grid.addColumn(GridUtils.columnPercentageRenderer(AssetGridItem::getAvgSellCompareWithCurrentPrice))
				.setHeader("Avg Sell vs Current Price")
				.setTextAlign(ColumnTextAlign.CENTER)
				.setAutoWidth(true)
				.setTooltipGenerator(a -> "Shows how the average sell price of %s compares to the current price".formatted(a.getSymbol()));

		// Creates the column selector menu based on column visibility
		ColumnToggleMenu columnToggleMenu = new ColumnToggleMenu();
		grid.addColumn(new ComponentRenderer<>(this::threeDotsBtn))
				.setHeader(columnToggleMenu)
				.setTextAlign(ColumnTextAlign.CENTER)
				.setFrozenToEnd(true);

		List<Grid.Column<AssetGridItem>> columnsWithData = grid.getColumns().subList(0, grid.getColumns().size() - 1);

		// Display just a couple of columns, others should be selected to be displayed
		columnsWithData.stream()
				.skip(DEFAULT_NUMBER_OF_COLUMNS_VISIBLE)
				.forEach(c -> c.setVisible(false));

		columnsWithData.forEach(col -> columnToggleMenu.addColumnToggleItem(col.getHeaderText(), col));
	}

	private void initializeFilteringBySearch() {
		searchField.addClassName("asset-search-field");
		searchField.setPlaceholder("Search asset");
		searchField.setSuffixComponent(new Icon(VaadinIcon.SEARCH));
		searchField.setValueChangeMode(ValueChangeMode.EAGER);

		searchField.addValueChangeListener(field -> {
			dataView.setFilter(assetProvided -> {
				String lowercaseSearchTerm = field.getValue().trim().toLowerCase();
				String lowercaseSymbol = assetProvided.getSymbol().toLowerCase();
				String lowercaseFullName = assetProvided.getName().toLowerCase();

				return lowercaseSearchTerm.isEmpty()
					   || lowercaseSymbol.contains(lowercaseSearchTerm)
					   || lowercaseFullName.contains(lowercaseSearchTerm);
			});

			updateColumnFooters();
			updateHiddenRowsCounter();
		});
	}

	private void initializeFilteringNonZeroValues() {
		hideAssetsCheckbox.addClickListener(e -> {
			if (hideAssetsCheckbox.getValue().equals(true)) {
				hideZeroAmountAssets();
			} else {
				resetGridFilteredItems();
			}

			updateColumnFooters();
			updateHiddenRowsCounter();
		});
	}

	private void hideZeroAmountAssets() {
		dataView.setFilter(asset -> asset.getTokenAmount() > 0);
	}

	private LitRenderer<AssetGridItem> columnNameRenderer() {
		return LitRenderer.<AssetGridItem>of("""
						<div class='coin-overview-name-container'>
						    <img class='rounded coin-overview-image' src='${item.imgUrl}' alt='${item.fullName}'/>
						    <p>${item.fullName}</p>
						    <span class='dot'>•</span>
						    <span>${item.symbol}</span>
						</div>
						""")
				.withProperty("imgUrl", AssetGridItem::getImageUrl)
				.withProperty("fullName", AssetGridItem::getName)
				.withProperty("symbol", AssetGridItem::getSymbol);
	}

	private LitRenderer<AssetGridItem> columnColoredPriceRenderer() {
		return LitRenderer.<AssetGridItem>of("<p class='asset-price'>${item.price}</p>")
				.withProperty("price", asset -> CommonFormatters.CURRENCY.format(asset.getPrice()));
	}

	private LitRenderer<AssetGridItem> totalProfitRenderer() {
		return LitRenderer.<AssetGridItem>of("""
						<div class=''>
						    <p>${item.profitUsd}</p>
						    <span>${item.profitPercentage}</span>
						</div>
						""")
				.withProperty("profitUsd", AssetGridItem::getTotalProfitUsd)
				.withProperty("profitPercentage", AssetGridItem::getTotalProfitPercentage);
	}

	private ComponentRenderer<Component, AssetGridItem> columnChanges24hRenderer() {
		return new ComponentRenderer<>(a -> new PercentageBadge(a.getPriceChangesPercentage24h(), true, false));
	}

	private void initializeSyncButton() {
		syncButton.addClickListener(event -> {
			animateSyncButtonIcon();
			instrumentsFacadeService.updateAssetData();
			setItems(assets);
			grid.removeAllColumns();
			renderColumns();
			resetFilterValues();
		});
	}

	private void animateSyncButtonIcon() {
		syncButton.getIcon().getElement().executeJs(
				"""
						this.style.transition = 'transform 1s ease-in-out';
						this.style.transform = 'rotate(360deg)';
						this.style.color = 'var(--color-orange)';
						setTimeout(() => {
						    this.style.transition = '';
						    this.style.transform = '';
						    this.style.color = 'var(--color-black)';
						}, 1000);
						"""
		);
	}

	private Button threeDotsBtn() {
		return new Button(); // TODO: SHOULD BE IMPLEMENTED SOMETHING
	}

	private Div hiddenRowsContainer() {
		return Container.builder("assets-grid-footer")
				.addComponent(new Paragraph("Hidden"))
				.addComponent(hiddenRowsCounterField)
				.addComponent(new Button("Show", e -> resetGridFilteredItems()))
				.build();
	}

	private void updateHiddenRowsCounter() {
		int numberOfHiddenRows = assets.size() - dataView.getItemCount();
		setHiddenRowCount(numberOfHiddenRows);
	}

	private void updateColumnFooters() {
		totalCostCol.setFooter("Total: $%.2f".formatted(columnSum(AssetGridItem::getTotalCost)));
		totalWorthCol.setFooter("Total: $%.2f".formatted(columnSum(AssetGridItem::getTotalWorth)));
		realizedCol.setFooter("Total: $%.2f".formatted(columnSum(AssetGridItem::getRealizedProfit)));
		unrealizedCol.setFooter("Total: $%.2f".formatted(columnSum(AssetGridItem::getUnrealizedProfit)));
		changes24hCol.setFooter("Average: %.0f%%".formatted(columnAverage(AssetGridItem::getPriceChangesPercentage24h)));
	}

	private void setHiddenRowCount(int count) {
		hiddenRowsCounterField.setText(String.format("(≈%d)", count));
	}

	private void resetGridFilteredItems() {
		dataView.setFilter(a -> true);
		resetFilterValues();
		updateColumnFooters();
	}

	private void resetFilterValues() {
		setHiddenRowCount(0);
		searchField.setValue("");
		hideAssetsCheckbox.setValue(false);
	}

	public void setItems(List<AssetDTO> assets) {
		this.assets = assets;
		dataView = grid.setItems(getConvertedGridItems());
	}

	public void setGridFullSize(boolean fullSize) {
		if (fullSize) {
			grid.getElement().executeJs("this.shadowRoot.querySelector('table').style.overflow = 'hidden';");
		}
	}

	private List<AssetGridItem> getConvertedGridItems() {
		return assets.stream()
				.map(asset -> {
					double currentPrice = asset.getMarketPrice();
					double avgBuy = portfolioPerformanceTracker.getAverageBuyPrice(portfolio, asset);
					double avgSell = portfolioPerformanceTracker.getAverageSellPrice(portfolio, asset);

					// TODO: [LONG TERM] Add volume column for: today, this week, this month, this year, total
					return AssetGridItem.builder()
							.symbol(asset.getSymbol())
							.name(asset.getFullName())
							.imageUrl(asset.getImageUrl())
							.price(currentPrice)
							.tokenAmount(instrumentsFacadeService.getAmountOfTokens(portfolio.getId(), asset.getSymbol()))
							.priceChangesPercentage24h(asset.getChangePercentage())
							.closestBuy(instrumentsFacadeService.getClosestBuyWatcherPrice(portfolio.getId(), asset.getSymbol()))
							.closestSell(instrumentsFacadeService.getClosestSellWatcherPrice(portfolio.getId(), asset.getSymbol()))
							.avgBuy(avgBuy)
							.avgSell(avgSell)
							.avgBuyCompareWithCurrentPrice(avgPriceComparedCurrentPrice(avgBuy, currentPrice))
							.avgSellCompareWithCurrentPrice(avgPriceComparedCurrentPrice(avgSell, currentPrice))
							.realizedProfit(portfolioPerformanceTracker.getAssetRealizedProfit(portfolio, asset))
							.unrealizedProfit(portfolioPerformanceTracker.getAssetUnrealizedProfit(portfolio, asset))
							.totalCost(portfolioPerformanceTracker.getAssetRemainingTokensCost(portfolio, asset))
							.totalWorth(portfolioPerformanceTracker.getAssetWorth(portfolio, asset))
							.diversityPercentage(portfolioPerformanceTracker.getAssetDiversityPercentage(portfolio, asset))
							.build();
				})
				.toList();
	}

	/**
	 * @return comparation between any of avg buy/sell price and current price in the percentage format
	 */
	private double avgPriceComparedCurrentPrice(double avgPrice, double currentPrice) {
		if (avgPrice == 0 || Double.isNaN(avgPrice))
			return Double.NaN;

		return ((currentPrice - avgPrice) / avgPrice) * 100;
	}

	private double columnAverage(ToDoubleFunction<AssetGridItem> function) {
		return dataView.getItems().toList().stream()
				.mapToDouble(function)
				.average()
				.orElse(Double.NaN);
	}

	private double columnSum(ToDoubleFunction<AssetGridItem> function) {
		return dataView.getItems().toList().stream()
				.mapToDouble(function)
				.sum();
	}

	@Data
	@Builder
	private static class AssetGridItem {
		private String name;
		private String symbol;
		private String imageUrl;
		private double price;
		private double priceChangesPercentage24h;
		private double avgBuy;
		private double avgSell;
		private double avgBuyCompareWithCurrentPrice;
		private double avgSellCompareWithCurrentPrice;
		private double tokenAmount;
		private double totalWorth;
		private double totalCost;
		private double diversityPercentage;
		private double realizedProfit;
		private double unrealizedProfit;
		private double totalProfitUsd;
		private double totalProfitPercentage;
		private double closestBuy;
		private double closestSell;
	}

	private static class ColumnToggleMenu extends MenuBar {

		private final SubMenu subItems = addItem(VaadinIcon.SLIDERS.create()).getSubMenu();

		void addColumnToggleItem(String label, Grid.Column<AssetGridItem> column) {
			MenuItem menuItem = subItems.addItem(label);
			menuItem.setKeepOpen(true);
			menuItem.setCheckable(true);
			menuItem.setChecked(column.isVisible());
			menuItem.addClickListener(e -> column.setVisible(!column.isVisible()));
		}

	}

}
