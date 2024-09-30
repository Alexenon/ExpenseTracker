package com.example.application.views.components;

import com.example.application.data.enums.Symbols;
import com.example.application.data.models.NumberType;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.services.crypto.PortfolioPerformanceTracker;
import com.example.application.views.components.complex_components.PriceBadge;
import com.example.application.views.components.native_components.Container;
import com.example.application.views.pages.AssetDetailsView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.theme.lumo.LumoIcon;
import lombok.Builder;
import lombok.Data;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.ToDoubleFunction;

/*
    TODO:
     - [!!] Optimize -> REMOVE certain columns from ColumnSelector instead of HIDING
     - [?] Display footer statistics details for certain columns -> Average, Total, ....
            dataProvider.addDataProviderListener(changeEvent -> {
                quantityColumn.setFooter("Total Quantity: " + calculateTotalQuantityOnGrid(dataProvider));
                priceColumn.setFooter("Total Price: "+ calculateTotalPriceOnGrid(dataProvider));
            });
     - [?] grid.setMultiSort(true, MultiSortPriority.APPEND);
     - ICONS: vaadin:trending-down | vaadin:trending-up
     - Closest Buy ->  $34,000.00 ❗
        -URGENT -> vaadin:exclamation vaadin:warning
    _______________________________________________________________________________________________________________________________________
    | Name | Price  | 24h Changes | Amount | Avg buy | Avg sell | All-time low | All-time high | Total Worth | Total Invested | Realized  |
    | BTC  | $64000 | 2%          | 0.0034 | $60000  |    -     | $10          | $73000        | $230        | $200           | $30 / 10% |
    _______________________________________________________________________________________________________________________________________
*/
public class AssetsGrid extends Div {

    private final InstrumentsFacadeService instrumentsFacadeService;
    private final PortfolioPerformanceTracker portfolioPerformanceTracker;

    private final TextField searchField = new TextField();
    private final Button syncButton = new Button("Sync", LumoIcon.RELOAD.create());
    private final Checkbox hideAssetsCheckbox = new Checkbox("Hide 0 amount assets");

    private final Grid<AssetGridItem> grid = new Grid<>();
    private final Span hiddenRowsCounterField = new Span();
    private GridListDataView<AssetGridItem> dataView;

    private Grid.Column<AssetGridItem> nameCol;
    private Grid.Column<AssetGridItem> priceCol;
    private Grid.Column<AssetGridItem> amountCol;
    private Grid.Column<AssetGridItem> changes24hCol;
    private Grid.Column<AssetGridItem> avgBuyCol;
    private Grid.Column<AssetGridItem> avgSellCol;
    private Grid.Column<AssetGridItem> diversityCol;
    private Grid.Column<AssetGridItem> totalWorthCol;
    private Grid.Column<AssetGridItem> totalCostCol;
    private Grid.Column<AssetGridItem> realizedCol;
    private Grid.Column<AssetGridItem> unrealizedCol;
    private Grid.Column<AssetGridItem> closestBuyCol;
    private Grid.Column<AssetGridItem> closestSellCol;

    public AssetsGrid(InstrumentsFacadeService instrumentsFacadeService,
                      PortfolioPerformanceTracker portfolioPerformanceTracker) {
        this.instrumentsFacadeService = instrumentsFacadeService;
        this.portfolioPerformanceTracker = portfolioPerformanceTracker;

        this.dataView = grid.setItems(getConvertedGridItems());
        initializeGrid();
        initializeFilteringBySearch();
        initializeFilteringNonZeroValues();
        initializeSyncButton();

        syncButton.addClickListener(e -> {
            instrumentsFacadeService.updateAssetMetadata();
            // TODO: The columns should be re-rendered again after updating metadata
        });

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
        List<Grid.Column<AssetGridItem>> listColumnsToSelect = List.of(nameCol, priceCol, changes24hCol, avgBuyCol,
                avgSellCol, amountCol, totalWorthCol, totalCostCol, diversityCol,
                realizedCol, unrealizedCol, closestBuyCol, closestSellCol);

        // Display just the first columns, others should be selected to be displayed
        listColumnsToSelect.stream().skip(8).forEach(c -> c.setVisible(false));

        Icon menuButton = VaadinIcon.SLIDERS.create();
        ColumnToggleContextMenu columnToggleContextMenu = new ColumnToggleContextMenu(menuButton);

        listColumnsToSelect.forEach(a -> columnToggleContextMenu.addColumnToggleItem(a.getKey(), a));

        grid.addColumn(new ComponentRenderer<>(this::threeDotsBtn))
                .setHeader(menuButton)
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFrozenToEnd(true);

        grid.setColumnReorderingAllowed(true);
        grid.getColumns().forEach(c -> c.setAutoWidth(true));
        grid.addItemClickListener(row -> {
            System.out.println(row.getItem());
            UI.getCurrent().navigate(AssetDetailsView.class, row.getItem().getSymbol().toUpperCase());
        });

        grid.setAllRowsVisible(true);
        grid.getElement().executeJs("this.shadowRoot.querySelector('table').style.overflow = 'hidden';");

        setHiddenRowCount(0);
    }

    private void renderColumns() {
        nameCol = grid.addColumn(columnNameRenderer())
                .setKey("Name")
                .setHeader("Name")
                .setSortable(true)
                .setFrozen(true)
                .setComparator(AssetGridItem::getSymbol);

        priceCol = grid.addColumn(columnPriceRenderer())
                .setKey("Price")
                .setHeader("Price")
                .setTextAlign(ColumnTextAlign.END)
                .setSortable(true)
                .setComparator(AssetGridItem::getPrice);

        changes24hCol = grid.addColumn(columnChanges24hRenderer())
                .setKey("Changes 24h")
                .setHeader("Changes 24h")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setSortable(true)
                .setComparator(AssetGridItem::getPriceChangesPercentage24h);

        avgBuyCol = grid.addColumn(columnPriceRenderer(AssetGridItem::getAvgBuy))
                .setKey("Avg Buy")
                .setHeader("Avg Buy")
                .setTextAlign(ColumnTextAlign.CENTER);

        avgSellCol = grid.addColumn(columnPriceRenderer(AssetGridItem::getAvgSell))
                .setKey("Avg Sell")
                .setHeader("Avg Sell")
                .setTextAlign(ColumnTextAlign.CENTER);

        amountCol = grid.addColumn(columnAmountRenderer(AssetGridItem::getTokenAmount))
                .setKey("Amount")
                .setHeader("Amount")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setSortable(true)
                .setComparator(AssetGridItem::getTokenAmount);

        totalWorthCol = grid.addColumn(columnPriceRenderer(AssetGridItem::getTotalWorth))
                .setKey("Total Worth")
                .setHeader("Total Worth")
                .setTextAlign(ColumnTextAlign.END)
                .setSortable(true)
                .setComparator(AssetGridItem::getTotalWorth)
                .setTooltipGenerator(a -> "Total value of your %s holdings based on the latest price.".formatted(a.getSymbol()));

        totalCostCol = grid.addColumn(columnPriceRenderer(AssetGridItem::getTotalCost))
                .setKey("Total Cost")
                .setHeader("Total Cost")
                .setTextAlign(ColumnTextAlign.END)
                .setSortable(true)
                .setComparator(AssetGridItem::getTotalCost)
                .setTooltipGenerator(a -> "Total cost of your %s holdings based on the latest price.".formatted(a.getSymbol()));

        diversityCol = grid.addColumn(columnPercentageRenderer(AssetGridItem::getDiversityPercentage))
                .setKey("Diversity")
                .setHeader("Diversity")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setSortable(true)
                .setComparator(AssetGridItem::getDiversityPercentage)
                .setTooltipGenerator(a -> "The percentage contribution of %s to your portfolio's total value.".formatted(a.getSymbol()));

        realizedCol = grid.addColumn(columnPriceRenderer(AssetGridItem::getRealizedProfit))
                .setKey("Realized")
                .setHeader("Realized")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setSortable(true)
                .setComparator(AssetGridItem::getRealizedProfit)
                .setTooltipGenerator(a -> "Profit or loss from your sold %s holdings.".formatted(a.getSymbol()));

        unrealizedCol = grid.addColumn(columnPriceRenderer(AssetGridItem::getUnrealizedProfit))
                .setKey("Unrealized")
                .setHeader("Unrealized")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setSortable(true)
                .setComparator(AssetGridItem::getUnrealizedProfit)
                .setTooltipGenerator(a -> "Potential profit or loss if you were to sell %s now.".formatted(a.getSymbol()));

        closestBuyCol = grid.addColumn(columnPriceRenderer(AssetGridItem::getClosestBuy))
                .setKey("Closest Buy")
                .setHeader("Closest Buy")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setTooltipGenerator(a -> "The closest %s buy price that was added in the watcher".formatted(a.getSymbol()));

        closestSellCol = grid.addColumn(columnPriceRenderer(AssetGridItem::getClosestSell))
                .setKey("Closest Sell")
                .setHeader("Closest Sell")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setTooltipGenerator(a -> "The closest %s sell price that was added in the watcher".formatted(a.getSymbol()));

        updateColumnFooters();
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
        return LitRenderer.<AssetGridItem>of(
                        "<div class='coin-overview-name-container'>" +
                        "  <img class='rounded coin-overview-image' src='${item.imgUrl}' alt='${item.fullName}'/>" +
                        "  <p>${item.fullName}</p>" +
                        "  <span class='dot'>•</span>" +
                        "  <span>${item.symbol}</span>" +
                        "</div>")
                .withProperty("imgUrl", AssetGridItem::getImageUrl)
                .withProperty("fullName", AssetGridItem::getName)
                .withProperty("symbol", AssetGridItem::getSymbol);
    }

    private LitRenderer<AssetGridItem> columnPriceRenderer() {
        return LitRenderer.<AssetGridItem>of("<p class='asset-price'>${item.price}</p>")
                .withProperty("price", asset -> NumberType.PRICE.parse(asset.getPrice()));
    }

    private LitRenderer<AssetGridItem> columnPriceRenderer(ValueProvider<AssetGridItem, Number> priceProvider) {
        return LitRenderer.<AssetGridItem>of("<p>${item.price}</p>")
                .withProperty("price", asset -> {
                    Number price = priceProvider.apply(asset);
                    NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);

                    return price.doubleValue() <= 0 ? "-" : nf.format(price);
                });
    }

    private LitRenderer<AssetGridItem> columnAmountRenderer(ValueProvider<AssetGridItem, Number> amountProvider) {
        return LitRenderer.<AssetGridItem>of("<p>${item.amount}</p>")
                .withProperty("amount", asset -> {
                    Number amount = amountProvider.apply(asset);
                    NumberFormat nf = NumberFormat.getNumberInstance();
                    nf.setMaximumFractionDigits(6);

                    return amount.doubleValue() <= 0 ? "-" : nf.format(amount);
                });
    }

    private LitRenderer<AssetGridItem> columnPercentageRenderer(ValueProvider<AssetGridItem, Number> percentageProvider) {
        return LitRenderer.<AssetGridItem>of("<p>${item.percentage}</p>")
                .withProperty("percentage", asset ->
                        NumberType.PERCENT.parse(percentageProvider.apply(asset).doubleValue()));
    }

    private ComponentRenderer<Component, AssetGridItem> columnChanges24hRenderer() {
        return new ComponentRenderer<>(a -> new PriceBadge(a.getPriceChangesPercentage24h(), NumberType.PERCENT));
    }

    private void initializeSyncButton() {
        syncButton.addClickListener(event -> {
            animateSyncButtonIcon();
            instrumentsFacadeService.updateAssetMetadata();
            dataView = grid.setItems(getConvertedGridItems());
            renderColumns();
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
        return new Button();
    }

    public void setItems(List<AssetGridItem> assets) {
        Objects.requireNonNull(assets, "Grid items cannot be null");
        grid.setItems(assets);
    }

    private Div hiddenRowsContainer() {
        return Container.builder("assets-grid-footer")
                .addComponent(new Paragraph("Hidden"))
                .addComponent(hiddenRowsCounterField)
                .addComponent(new Button("Show", e -> resetGridFilteredItems()))
                .build();
    }

    private void updateHiddenRowsCounter() {
        int numberOfHiddenRows = Symbols.getAll().size() - dataView.getItemCount();
        setHiddenRowCount(numberOfHiddenRows);
    }

    private void updateColumnFooters() {
        totalCostCol.setFooter("Total: $%.2f".formatted(getColumnSum(AssetGridItem::getTotalCost)));
        totalWorthCol.setFooter("Total: $%.2f".formatted(getColumnSum(AssetGridItem::getTotalWorth)));
        realizedCol.setFooter("Total: $%.2f".formatted(getColumnSum(AssetGridItem::getRealizedProfit)));
        unrealizedCol.setFooter("Total: $%.2f".formatted(getColumnSum(AssetGridItem::getUnrealizedProfit)));
        changes24hCol.setFooter("Average: %.0f%%".formatted(getColumnAverage(AssetGridItem::getPriceChangesPercentage24h)));
    }

    private void setHiddenRowCount(int count) {
        hiddenRowsCounterField.setText(String.format("(≈%d)", count));
    }

    private void resetGridFilteredItems() {
        dataView.setFilter(a -> true);
        searchField.setValue("");
        hideAssetsCheckbox.setValue(false);
        updateColumnFooters();
        updateHiddenRowsCounter();
    }

    private List<AssetGridItem> getConvertedGridItems() {
        return instrumentsFacadeService.getAllAssets()
                .stream()
                .map(asset -> AssetGridItem.builder()
                        .symbol(asset.getSymbol())
                        .name(instrumentsFacadeService.getAssetFullName(asset))
                        .imageUrl(instrumentsFacadeService.getAssetImgUrl(asset))
                        .price(instrumentsFacadeService.getAssetPrice(asset))
                        .tokenAmount(instrumentsFacadeService.getAmountOfTokens(asset))
                        .priceChangesPercentage24h(instrumentsFacadeService.getAsset24HourChangePercentage(asset))
                        .closestBuy(instrumentsFacadeService.getClosestBuyWatcherPrice(asset))
                        .closestSell(instrumentsFacadeService.getClosestSellWatcherPrice(asset))
                        // TODO: Add volume column
                        .avgBuy(portfolioPerformanceTracker.getAverageBuyPrice(asset))
                        .avgSell(portfolioPerformanceTracker.getAverageSellPrice(asset))
                        .realizedProfit(portfolioPerformanceTracker.getAssetProfit(asset))
                        .unrealizedProfit(0) // TODO: Is Unrealized profit the same as TotalWorth ???
                        .totalCost(portfolioPerformanceTracker.getAssetCost(asset))
                        .totalWorth(portfolioPerformanceTracker.getAssetWorth(asset))
                        .diversityPercentage(portfolioPerformanceTracker.getAssetDiversityPercentage(asset))
                        .build())
                .toList();
    }

    private double getColumnAverage(ToDoubleFunction<AssetGridItem> function) {
        return dataView.getItems().toList().stream()
                .mapToDouble(function)
                .average()
                .orElse(0.0);
    }

    private double getColumnSum(ToDoubleFunction<AssetGridItem> function) {
        return dataView.getItems().toList().stream()
                .mapToDouble(function)
                .sum();
    }

    @Data
    @Builder
    public static class AssetGridItem {
        private String name;
        private String symbol;
        private String imageUrl;
        private double price;
        private double priceChangesPercentage24h;
        private double avgBuy;
        private double avgSell;
        private double tokenAmount;
        private double totalWorth;
        private double totalCost;
        private double diversityPercentage;
        private double realizedProfit;
        private double unrealizedProfit;
        private double closestBuy;
        private double closestSell;
    }

    private static class ColumnToggleContextMenu extends ContextMenu {

        public ColumnToggleContextMenu(Component target) {
            super(target);
            setOpenOnClick(true);
        }

        void addColumnToggleItem(String label, Grid.Column<AssetGridItem> column) {
            MenuItem menuItem = this.addItem(label);
            menuItem.setCheckable(true);
            menuItem.setChecked(column.isVisible());

            // Prevents from closing menu on inside menu item click, still closes on outside click
            menuItem.getElement().setAttribute("onclick", "event.stopPropagation()");
            menuItem.getElement().executeJs("this.click();");

            menuItem.addClickListener(e -> {
                boolean isChecked = !menuItem.isChecked();
                column.setVisible(!menuItem.isChecked());
                updateMenuItemCheckmark(menuItem, isChecked);
            });
        }

        private void updateMenuItemCheckmark(MenuItem menuItem, boolean isChecked) {
            if (isChecked) {
                menuItem.getElement().setAttribute("menu-item-checked", "");
            } else {
                menuItem.getElement().removeAttribute("aria-selected");
                menuItem.getElement().removeAttribute("menu-item-checked");
            }
        }
    }


}
