package com.example.application.views.components;

import com.example.application.data.enums.Symbols;
import com.example.application.data.models.NumberType;
import com.example.application.entities.crypto.Asset;
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

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/*
    TODO:
     - [!!!] Optimize sorting columns, by using https://vaadin.com/blog/using-the-right-r
     - [!!!] Optimize hiding certain columns with ColumnSelector
     - [!!] Fix sortable criteria for certain columns, make other columns not movable(left/right)
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

    private final Grid<Asset> grid = new Grid<>();
    private final GridListDataView<Asset> dataView = grid.setItems();

    private final Span hiddenRowsCounterField = new Span();

    public AssetsGrid(InstrumentsFacadeService instrumentsFacadeService,
                      PortfolioPerformanceTracker portfolioPerformanceTracker) {
        this.instrumentsFacadeService = instrumentsFacadeService;
        this.portfolioPerformanceTracker = portfolioPerformanceTracker;

        initializeGrid();
        initializeFilteringBySearch();
        initializeFilteringNonZeroValues();
        initializeSyncButton();

        grid.setItems(instrumentsFacadeService.getAllAssets());
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
        Grid.Column<Asset> nameCol = grid.addColumn(columnNameRenderer())
                .setKey("Name")
                .setHeader("Name")
                .setSortable(true)
                .setFrozen(true)
                .setComparator(Asset::getSymbol);

        Grid.Column<Asset> priceCol = grid.addColumn(columnCurrencyRenderer(instrumentsFacadeService::getAssetPrice))
                .setKey("Price")
                .setHeader("Price")
                .setSortable(true)
                .setComparator(instrumentsFacadeService::getAssetPrice);

        Grid.Column<Asset> changes24hCol = grid.addColumn(columnChanges24hRenderer())
                .setKey("Changes 24h")
                .setHeader("Changes 24h")
                .setSortable(true)
                .setComparator(instrumentsFacadeService::getAsset24HourVolume);

        Grid.Column<Asset> avgBuyCol = grid.addColumn(columnCurrencyRenderer(portfolioPerformanceTracker::getAverageBuyPrice))
                .setKey("Avg Buy")
                .setHeader("Avg Buy");

        Grid.Column<Asset> avgSellCol = grid.addColumn(columnCurrencyRenderer(portfolioPerformanceTracker::getAverageSellPrice))
                .setKey("Avg Sell")
                .setHeader("Avg Sell");

        Grid.Column<Asset> amountCol = grid.addColumn(columnAmountRenderer(instrumentsFacadeService::getAmountOfTokens))
                .setKey("Amount")
                .setHeader("Amount")
                .setSortable(true)
                .setComparator(instrumentsFacadeService::getAmountOfTokens);

        Grid.Column<Asset> totalWorthCol = grid.addColumn(columnCurrencyRenderer(portfolioPerformanceTracker::getAssetWorth))
                .setKey("Total Worth")
                .setHeader("Total Worth")
                .setSortable(true)
                .setComparator(portfolioPerformanceTracker::getAssetWorth)
                .setTooltipGenerator(a -> "Total value of your %s holdings based on the latest price.".formatted(a.getSymbol()));

        Grid.Column<Asset> totalCostCol = grid.addColumn(columnCurrencyRenderer(portfolioPerformanceTracker::getAssetCost))
                .setKey("Total Cost")
                .setHeader("Total Cost")
                .setSortable(true)
                .setComparator(portfolioPerformanceTracker::getAssetCost)
                .setTooltipGenerator(a -> "Total cost of your %s holdings based on the latest price.".formatted(a.getSymbol()));

        Grid.Column<Asset> diversityCol = grid.addColumn(portfolioPerformanceTracker::getAssetDiversityPercentage)
                .setKey("Diversity")
                .setHeader("Diversity")
                .setSortable(true)
                .setComparator(portfolioPerformanceTracker::getAssetProfit)
                .setTooltipGenerator(a -> "The percentage contribution of %s to your portfolio's total value.".formatted(a.getSymbol()));

        Grid.Column<Asset> realizedCol = grid.addColumn(columnCurrencyRenderer(portfolioPerformanceTracker::getAssetProfit))
                .setKey("Realized")
                .setHeader("Realized")
                .setSortable(true)
                .setComparator(portfolioPerformanceTracker::getAssetProfit)
                .setTooltipGenerator(a -> "Profit or loss from your sold %s holdings.".formatted(a.getSymbol()));

        Grid.Column<Asset> unrealizedCol = grid.addColumn(columnCurrencyRenderer(instrumentsFacadeService::getAmountOfTokens))
                .setKey("Unrealized")
                .setHeader("Unrealized")
                .setSortable(true)
                .setComparator(portfolioPerformanceTracker::getAssetProfit)
                .setTooltipGenerator(a -> "Potential profit or loss if you were to sell %s now.".formatted(a.getSymbol()));

        Grid.Column<Asset> nextBuyCol = grid.addColumn(columnCurrencyRenderer(instrumentsFacadeService::getAmountOfTokens))
                .setKey("Next Buy")
                .setHeader("Next Buy");
        //.setTooltipGenerator(a -> "The next price point for purchasing more %s is $%s".formatted(a.getSymbol()));

        Grid.Column<Asset> nextSellCol = grid.addColumn(columnCurrencyRenderer(instrumentsFacadeService::getAmountOfTokens))
                .setKey("Next Sell")
                .setHeader("Next Sell");
        //.setTooltipGenerator(a -> "The next price point for purchasing more %s is $%s".formatted(a.getSymbol()));


        List<Grid.Column<Asset>> listColumnsToSelect = List.of(nameCol, priceCol, changes24hCol, avgBuyCol, avgSellCol, amountCol,
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

    private LitRenderer<Asset> columnNameRenderer() {
        return LitRenderer.<Asset>of(
                        "<div class='coin-overview-name-container'>" +
                        "  <img class='rounded coin-overview-image' src='${item.imgUrl}' alt='${item.fullName}'/>" +
                        "  <p>${item.fullName}</p>" +
                        "  <span class='dot'>•</span>" +
                        "  <span>${item.symbol}</span>" +
                        "</div>")
                .withProperty("imgUrl", instrumentsFacadeService::getAssetImgUrl)
                .withProperty("fullName", instrumentsFacadeService::getAssetFullName)
                .withProperty("symbol", Asset::getSymbol);
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
                String lowercaseFullName = instrumentsFacadeService.getAssetFullName(assetProvided).toLowerCase();

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
        dataView.setFilter(asset -> instrumentsFacadeService.getAmountOfTokens(asset) > 0);
    }

    private ComponentRenderer<Component, Asset> columnChanges24hRenderer() {
        return new ComponentRenderer<>(a -> {
            PriceBadge percentageBadge = new PriceBadge(instrumentsFacadeService.getAsset24HourChangePercentage(a), NumberType.PERCENT);
            percentageBadge.getStyle().set("margin-bottom", "0px");
            percentageBadge.setBackgroundColor(PriceBadge.Color.DEFAULT_BACKGROUND_COLOR);
            return percentageBadge;
        });
    }

    private LitRenderer<Asset> columnAmountRenderer(ValueProvider<Asset, Number> priceProvider) {
        return LitRenderer.<Asset>of("${item.amount}")
                .withProperty("amount", asset -> {
                    NumberFormat nf = NumberFormat.getNumberInstance();
                    nf.setMaximumFractionDigits(6);
                    return nf.format(priceProvider.apply(asset));
                });
    }

    private LitRenderer<Asset> columnCurrencyRenderer(ValueProvider<Asset, Number> currencyProvider) {
        return LitRenderer.<Asset>of("${item.currency}")
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

    public void setItems(List<Asset> assets) {
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

    private static class ColumnToggleContextMenu extends ContextMenu {
        public ColumnToggleContextMenu(Component target) {
            super(target);
            setOpenOnClick(true);
        }

        void addColumnToggleItem(String label, Grid.Column<Asset> column) {
            MenuItem menuItem = this.addItem(label, e -> column.setVisible(e.getSource().isChecked()));
            menuItem.setCheckable(true);
            menuItem.setChecked(column.isVisible());
        }
    }

}
