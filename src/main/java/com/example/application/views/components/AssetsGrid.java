package com.example.application.views.components;

import com.example.application.data.enums.Symbols;
import com.example.application.data.models.NumberType;
import com.example.application.entities.crypto.Asset;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.views.components.complex_components.PriceBadge;
import com.example.application.views.components.native_components.Container;
import com.example.application.views.pages.AssetDetailsView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.theme.lumo.LumoIcon;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/*
    TODO:
     - [!] Fix sortable criteria for certain columns
     - [?] Think about column header style(maybe align item center)
     - [?] Display footer statistics details for certain columns
     - [?] Add column tooltipComponent -> .setTooltipGenerator(a -> "Name is " + a.getName());
    _______________________________________________________________________________________________________________________________________
    | Name | Price  | 24h Changes | Amount | Avg buy | Avg sell | All-time low | All-time high | Total Worth | Total Invested | Realized  |
    | BTC  | $64000 | 2%          | 0.0034 | $60000  |    -     | $10          | $73000        | $230        | $200           | $30 / 10% |
    _______________________________________________________________________________________________________________________________________
*/
public class AssetsGrid extends Div {

    private final InstrumentsFacadeService instrumentsFacadeService;

    private final TextField searchField = new TextField();
    private final Checkbox hideAssetsCheckbox = new Checkbox("Hide 0 amount assets");
    private final Button syncButton = new Button("Sync", LumoIcon.RELOAD.create());

    private final Grid<Asset> grid = new Grid<>();
    private final GridListDataView<Asset> dataView = grid.setItems();

    private final Span hiddenRowsCounterField = new Span();

    private MultiSelectComboBox<String> columnSelector;
    private List<Grid.Column<Asset>> listColumnsToSelect;

    public AssetsGrid(InstrumentsFacadeService instrumentsFacadeService) {
        this.instrumentsFacadeService = instrumentsFacadeService;

        initializeGrid();
        initializeColumnSelector();
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
        columnSelector = new MultiSelectComboBox<>();

        Grid.Column<Asset> nameCol = grid.addColumn(columnNameRenderer()).setKey("Name").setHeader("Name");
        Grid.Column<Asset> priceCol = grid.addColumn(columnPriceRenderer(instrumentsFacadeService::getAssetPrice)).setKey("Price").setHeader("Price");
        Grid.Column<Asset> changes24hCol = grid.addColumn(columnChanges24hRenderer()).setKey("Changes 24h").setHeader("Changes 24h");
        Grid.Column<Asset> avgBuyCol = grid.addColumn(columnPriceRenderer(instrumentsFacadeService::getAssetPrice)).setKey("Avg Buy").setHeader("Avg Buy");
        Grid.Column<Asset> avgSellCol = grid.addColumn(columnPriceRenderer(instrumentsFacadeService::getAssetPrice)).setKey("Avg Sell").setHeader("Avg Sell");
//        Grid.Column<Asset> amountCol = grid.addColumn(a -> a.getAsset().getAmount()).setKey("Amount").setHeader("Amount");
//        Grid.Column<Asset> profitChangesCol = grid.addColumn(new ComponentRenderer<>(this::renderProfitChanges)).setKey("Profit Changes").setHeader("Profit Changes");
        grid.addColumn(new ComponentRenderer<>(this::threeDotsBtn)).setHeader(columnSelector);

        // TODO: Columns -> Avg Buy Remaining Tokens,
        listColumnsToSelect = List.of(nameCol, priceCol, changes24hCol, avgBuyCol, avgSellCol);

        grid.getColumns().forEach(c -> {
            c.setSortable(true);
            c.setAutoWidth(true);
        });
        grid.setColumnReorderingAllowed(true);
        grid.addItemClickListener(row -> {
            System.out.println(row.getItem());
            UI.getCurrent().navigate(AssetDetailsView.class, row.getItem().getSymbol().toUpperCase());
        });

        grid.setAllRowsVisible(true);

        grid.getElement().executeJs("this.shadowRoot.querySelector('table').style.overflow = 'hidden';");

//        dataProvider.addDataProviderListener(changeEvent -> {
//            quantityColumn.setFooter("Total Quantity: " + calculateTotalQuantityOnGrid(dataProvider));
//            priceColumn.setFooter("Total Price: "+ calculateTotalPriceOnGrid(dataProvider));
//        });

        setHiddenRowCount(0);
    }

    private void initializeColumnSelector() {
        columnSelector.setItems(listColumnsToSelect.stream().map(Grid.Column::getKey).toList());
        columnSelector.select(listColumnsToSelect.stream().map(Grid.Column::getKey).toList());
        columnSelector.addValueChangeListener(e -> {
            Set<String> namesOfSelectedColumns = e.getValue();
            listColumnsToSelect.forEach(column -> column.setVisible(namesOfSelectedColumns.contains(column.getKey())));
        });
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
                hideZeroAmountItems();
            } else {
                resetGridFilteredItems();
            }

            updateHiddenRowsCounter();
        });
    }

    private void hideZeroAmountItems() {
        dataView.setFilter(asset -> instrumentsFacadeService.getAssetPrice(asset) > 0);
    }

    private ComponentRenderer<Container, Asset> columnNameRenderer() {
        return new ComponentRenderer<>(a ->
                Container.builder("coin-overview-name-container")
                        .addComponent(() -> {
                            Image image = new Image(instrumentsFacadeService.getAssetImgUrl(a), instrumentsFacadeService.getAssetFullName(a));
                            image.addClassNames("rounded", "coin-overview-image");
                            return image;
                        })
                        .addComponent(new Paragraph(instrumentsFacadeService.getAssetFullName(a)))
                        .addComponent(() -> {
                            Span dot = new Span("•");
                            dot.setClassName("dot");
                            return dot;
                        })
                        .addComponent(new Span(a.getSymbol()))
                        .build()
        );
    }

    private ComponentRenderer<Component, Asset> columnChanges24hRenderer() {
        return new ComponentRenderer<>(a -> {
            PriceBadge percentageBadge = new PriceBadge(instrumentsFacadeService.getAsset24HourChangePercentage(a), NumberType.PERCENT);
            percentageBadge.getStyle().set("margin-bottom", "0px");
            percentageBadge.setBackgroundColor(PriceBadge.Color.DEFAULT_BACKGROUND_COLOR);
            return percentageBadge;
        });
    }

    private NumberRenderer<Asset> columnPriceRenderer(ValueProvider<Asset, Number> priceProvider) {
        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
        return new NumberRenderer<>(priceProvider, nf, "$0.00");
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

}
