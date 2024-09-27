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

/*
    TODO:
     - [!!!] Optimize sorting columns, by using https://vaadin.com/blog/using-the-right-r
     - [!!!] Optimize -> REMOVE certain columns from ColumnSelector instead of HIDING
     - [?] Think about column header style(maybe align item center), column style maybe align left/right
     - [?] Display footer statistics details for certain columns -> Average, Total, ....
            dataProvider.addDataProviderListener(changeEvent -> {
                quantityColumn.setFooter("Total Quantity: " + calculateTotalQuantityOnGrid(dataProvider));
                priceColumn.setFooter("Total Price: "+ calculateTotalPriceOnGrid(dataProvider));
            });
     - [?] grid.setMultiSort(true, MultiSortPriority.APPEND);
     - [?] Make to display the closest asset to buy price / sell price

     - select, sliders, sort, combobox

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
    private final GridListDataView<AssetGridItem> dataView = grid.setItems();

    private final Span hiddenRowsCounterField = new Span();

    public AssetsGrid(InstrumentsFacadeService instrumentsFacadeService,
                      PortfolioPerformanceTracker portfolioPerformanceTracker) {
        this.instrumentsFacadeService = instrumentsFacadeService;
        this.portfolioPerformanceTracker = portfolioPerformanceTracker;

        initializeGrid();
        initializeFilteringBySearch();
        initializeFilteringNonZeroValues();
        initializeSyncButton();

        grid.setItems(getConvertedGridItems());
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
        Grid.Column<AssetGridItem> nameCol = grid.addColumn(columnNameRenderer())
                .setKey("Name")
                .setHeader("Name")
                .setSortable(true)
                .setFrozen(true)
                .setComparator(AssetGridItem::getSymbol);

        Grid.Column<AssetGridItem> priceCol = grid.addColumn(columnCurrencyRenderer(AssetGridItem::getPrice))
                .setKey("Price")
                .setHeader("Price")
                .setSortable(true)
                .setComparator(AssetGridItem::getPrice);

        Grid.Column<AssetGridItem> changes24hCol = grid.addColumn(columnChanges24hRenderer())
                .setKey("Changes 24h")
                .setHeader("Changes 24h")
                .setSortable(true)
                .setComparator(AssetGridItem::getPriceChangesPercentage24h);

        Grid.Column<AssetGridItem> avgBuyCol = grid.addColumn(columnCurrencyRenderer(AssetGridItem::getAvgBuy))
                .setKey("Avg Buy")
                .setHeader("Avg Buy");

        Grid.Column<AssetGridItem> avgSellCol = grid.addColumn(columnCurrencyRenderer(AssetGridItem::getAvgSell))
                .setKey("Avg Sell")
                .setHeader("Avg Sell");

        Grid.Column<AssetGridItem> amountCol = grid.addColumn(columnAmountRenderer(AssetGridItem::getTokenAmount))
                .setKey("Amount")
                .setHeader("Amount")
                .setSortable(true)
                .setComparator(AssetGridItem::getTokenAmount);

        Grid.Column<AssetGridItem> totalWorthCol = grid.addColumn(columnCurrencyRenderer(AssetGridItem::getTotalWorth))
                .setKey("Total Worth")
                .setHeader("Total Worth")
                .setSortable(true)
                .setComparator(AssetGridItem::getTotalWorth)
                .setTooltipGenerator(a -> "Total value of your %s holdings based on the latest price.".formatted(a.getSymbol()));

        Grid.Column<AssetGridItem> totalCostCol = grid.addColumn(columnCurrencyRenderer(AssetGridItem::getTotalCost))
                .setKey("Total Cost")
                .setHeader("Total Cost")
                .setSortable(true)
                .setComparator(AssetGridItem::getTotalCost)
                .setTooltipGenerator(a -> "Total cost of your %s holdings based on the latest price.".formatted(a.getSymbol()));

        Grid.Column<AssetGridItem> diversityCol = grid.addColumn(AssetGridItem::getDiversityPercentage)
                .setKey("Diversity")
                .setHeader("Diversity")
                .setSortable(true)
                .setComparator(AssetGridItem::getDiversityPercentage)
                .setTooltipGenerator(a -> "The percentage contribution of %s to your portfolio's total value.".formatted(a.getSymbol()));

        Grid.Column<AssetGridItem> realizedCol = grid.addColumn(columnCurrencyRenderer(AssetGridItem::getRealizedProfit))
                .setKey("Realized")
                .setHeader("Realized")
                .setSortable(true)
                .setComparator(AssetGridItem::getRealizedProfit)
                .setTooltipGenerator(a -> "Profit or loss from your sold %s holdings.".formatted(a.getSymbol()));

        Grid.Column<AssetGridItem> unrealizedCol = grid.addColumn(columnCurrencyRenderer(AssetGridItem::getUnrealizedProfit))
                .setKey("Unrealized")
                .setHeader("Unrealized")
                .setSortable(true)
                .setComparator(AssetGridItem::getUnrealizedProfit)
                .setTooltipGenerator(a -> "Potential profit or loss if you were to sell %s now.".formatted(a.getSymbol()));

        Grid.Column<AssetGridItem> nextBuyCol = grid.addColumn(columnCurrencyRenderer(AssetGridItem::getNextBuy))
                .setKey("Next Buy")
                .setHeader("Next Buy");
        //.setTooltipGenerator(a -> "The next price point for purchasing more %s is $%s".formatted(a.getSymbol()));

        Grid.Column<AssetGridItem> nextSellCol = grid.addColumn(columnCurrencyRenderer(AssetGridItem::getNextSell))
                .setKey("Next Sell")
                .setHeader("Next Sell");
        //.setTooltipGenerator(a -> "The next price point for purchasing more %s is $%s".formatted(a.getSymbol()));


        List<Grid.Column<AssetGridItem>> listColumnsToSelect = List.of(nameCol, priceCol, changes24hCol, avgBuyCol, avgSellCol, amountCol,
                totalWorthCol, totalCostCol, diversityCol, realizedCol, unrealizedCol, nextBuyCol, nextSellCol);

        // Display just the first columns, others should be selected to be displayed
        listColumnsToSelect.stream().skip(8).forEach(c -> c.setVisible(false));

        Button menuButton = new Button(VaadinIcon.SELECT.create());
        ColumnToggleContextMenu columnToggleContextMenu = new ColumnToggleContextMenu(menuButton);

        listColumnsToSelect.forEach(a -> columnToggleContextMenu.addColumnToggleItem(a.getKey(), a));

        grid.addColumn(new ComponentRenderer<>(this::threeDotsBtn))
                .setHeader(menuButton)
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

            updateHiddenRowsCounter();
        });
    }

    private void hideZeroAmountAssets() {
        dataView.setFilter(asset -> asset.getTokenAmount() > 0);
    }

    private ComponentRenderer<Component, AssetGridItem> columnChanges24hRenderer() {
        return new ComponentRenderer<>(a -> {
            PriceBadge percentageBadge = new PriceBadge(a.getPriceChangesPercentage24h(), NumberType.PERCENT);
            percentageBadge.getStyle().set("margin-bottom", "0px");
            percentageBadge.setBackgroundColor(PriceBadge.Color.DEFAULT_BACKGROUND_COLOR);
            return percentageBadge;
        });
    }

    private LitRenderer<AssetGridItem> columnAmountRenderer(ValueProvider<AssetGridItem, Number> priceProvider) {
        return LitRenderer.<AssetGridItem>of("${item.amount}")
                .withProperty("amount", asset -> {
                    NumberFormat nf = NumberFormat.getNumberInstance();
                    nf.setMaximumFractionDigits(6);
                    return nf.format(priceProvider.apply(asset));
                });
    }

    private LitRenderer<AssetGridItem> columnCurrencyRenderer(ValueProvider<AssetGridItem, Number> currencyProvider) {
        return LitRenderer.<AssetGridItem>of("${item.currency}")
                .withProperty("currency", asset -> {
                    NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
                    return nf.format(currencyProvider.apply(asset));
                });
    }

    private void initializeSyncButton() {
        syncButton.addClickListener(event -> animateSyncButtonIcon());
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
        return Container.builder()
                .addComponent(new Paragraph("Hidden"))
                .addComponent(hiddenRowsCounterField)
                .addComponent(new Button("Show", e -> resetGridFilteredItems()))
                .setStyle("""
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        """)
                .build();
    }

    private void updateHiddenRowsCounter() {
        int numberHiddenRows = Symbols.getAll().size() - dataView.getItemCount();
        setHiddenRowCount(numberHiddenRows);
    }

    private void setHiddenRowCount(int count) {
        hiddenRowsCounterField.setText(String.format("(≈%d)", count));
    }

    private void resetGridFilteredItems() {
        dataView.setFilter(a -> true);
        searchField.setValue("");
        hideAssetsCheckbox.setValue(false);
        updateHiddenRowsCounter();
    }

    private List<AssetGridItem> getConvertedGridItems() {
        return instrumentsFacadeService.getAllAssets()
                .stream()
                .map(asset -> AssetGridItem.builder()
                        .name(asset.getSymbol())
                        .symbol(asset.getSymbol())
                        .imageUrl(instrumentsFacadeService.getAssetImgUrl(asset))
                        .price(instrumentsFacadeService.getAssetPrice(asset))
                        .tokenAmount(instrumentsFacadeService.getAmountOfTokens(asset))
                        .priceChangesPercentage24h(instrumentsFacadeService.getAsset24HourChangePercentage(asset))
                        // TODO: Add volume column
                        // Performance
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
        private double nextBuy;
        private double nextSell;
    }

    private static class ColumnToggleContextMenu extends ContextMenu {
        public ColumnToggleContextMenu(Component target) {
            super(target);
            setOpenOnClick(true);
        }

        void addColumnToggleItem(String label, Grid.Column<AssetGridItem> column) {
            MenuItem menuItem = this.addItem(label, e -> column.setVisible(e.getSource().isChecked()));
            menuItem.setCheckable(true);
            menuItem.setChecked(column.isVisible());
        }
    }

}
