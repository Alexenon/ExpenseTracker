package com.example.application.views.components;

import com.example.application.data.models.NumberType;
import com.example.application.data.models.crypto.AssetData;
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
     - Think about column header style(maybe align item center)
     - Add sync button functionality(don't forget about checkbox value)

    _______________________________________________________________________________________________________________________________________
    | Name | Price  | 24h Changes | Amount | Avg buy | Avg sell | All-time low | All-time high | Total Worth | Total Invested | Realized  |
    | BTC  | $64000 | 2%          | 0.0034 | $60000  |    -     | $10          | $73000        | $230        | $200           | $30 / 10% |
    _______________________________________________________________________________________________________________________________________
*/
public class AssetsGrid extends Div {

    private final Grid<AssetData> grid = new Grid<>();
    private final TextField searchField = new TextField();
    private final Checkbox checkbox = new Checkbox("Hide 0 amount assets");
    private final Button syncButton = new Button("Sync", LumoIcon.RELOAD.create());
    private final GridListDataView<AssetData> dataView = grid.setItems();

    private MultiSelectComboBox<String> columnSelector;
    private List<Grid.Column<AssetData>> listColumnsToSelect;

    public AssetsGrid() {
        initializeGrid();
        initializeColumnSelector();
        initializeFilteringBySearch();
        initializeFilteringNonZeroValues();
        initializeSyncButton();

        add(
                gridHeader(),
                grid
        );
    }

    private Div gridHeader() {
        Div syncContainer = new Div(syncButton, checkbox);
        syncContainer.addClassName("sync-container");

        Div header = new Div(searchField, syncContainer);
        header.addClassName("assets-grid-header");
        return header;
    }

    private void initializeGrid() {
        columnSelector = new MultiSelectComboBox<>();

        Grid.Column<AssetData> nameCol = grid.addColumn(columnNameRenderer()).setKey("Name").setHeader("Name");
        Grid.Column<AssetData> priceCol = grid.addColumn(columnPriceRenderer(AssetData::getPrice)).setKey("Price").setHeader("Price");
        Grid.Column<AssetData> changes24hCol = grid.addColumn(columnChanges24hRenderer()).setKey("Changes 24h").setHeader("Changes 24h");
        Grid.Column<AssetData> avgBuyCol = grid.addColumn(columnPriceRenderer(AssetData::getAverageBuyPrice)).setKey("Avg Buy").setHeader("Avg Buy");
        Grid.Column<AssetData> avgSellCol = grid.addColumn(columnPriceRenderer(AssetData::getAverageSellPrice)).setKey("Avg Sell").setHeader("Avg Sell");
        Grid.Column<AssetData> amountCol = grid.addColumn(a -> a.getAsset().getAmount()).setKey("Amount").setHeader("Amount");
//        Grid.Column<AssetData> profitChangesCol = grid.addColumn(new ComponentRenderer<>(this::renderProfitChanges)).setKey("Profit Changes").setHeader("Profit Changes");
        grid.addColumn(new ComponentRenderer<>(this::threeDotsBtn)).setHeader(columnSelector);
        listColumnsToSelect = List.of(nameCol, priceCol, changes24hCol, avgBuyCol, avgSellCol, amountCol);

        grid.getColumns().forEach(c -> {
            c.setSortable(true);
            c.setAutoWidth(true);
        });
        grid.setColumnReorderingAllowed(true);
        grid.addItemClickListener(row -> {
            System.out.println(row.getItem());
            UI.getCurrent().navigate(AssetDetailsView.class, row.getItem().getAsset().getSymbol().toUpperCase());
        });
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

        searchField.addValueChangeListener(e -> {
            String searchTerm = searchField.getValue().trim().toLowerCase();

            dataView.setFilter(assetData -> {
                String symbol = assetData.getAsset().getSymbol().toLowerCase();
                String name = assetData.getAssetInfo().getName().toLowerCase();
                return searchTerm.isEmpty() || symbol.contains(searchTerm) || name.contains(searchTerm);
            });
        });
    }

    private void initializeFilteringNonZeroValues() {
        checkbox.addClickListener(e -> {
            dataView.setFilter(checkbox.getValue().equals(true)
                    ? (assetData -> assetData.getAsset().getAmount() > 0)
                    : (assetData -> true)); // resets the default grid data
        });
    }

    // TODO: Add sync functionality
    private void initializeSyncButton() {
        syncButton.addClickListener(event -> {
            animateSyncButtonIcon();
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

    private ComponentRenderer<Container, AssetData> columnNameRenderer() {
        return new ComponentRenderer<>(a ->
                Container.builder("coin-overview-name-container")
                        .addComponent(() -> {
                            Image image = new Image(a.getAssetInfo().getLogoUrl(), a.getAssetInfo().getName());
                            image.setClassName("coin-overview-image");
                            return image;
                        })
                        .addComponent(new Paragraph(a.getAssetInfo().getName()))
                        .addComponent(() -> {
                            Span dot = new Span("•");
                            dot.setClassName("dot");
                            return dot;
                        })
                        .addComponent(new Span(a.getAssetInfo().getSymbol()))
                        .build()
        );
    }

    private ComponentRenderer<Component, AssetData> columnChanges24hRenderer() {
        return new ComponentRenderer<>(a -> {
            PriceBadge percentageBadge = new PriceBadge(a.getChangesLast24hPercentage(), NumberType.PERCENT);
            percentageBadge.getStyle().set("margin-bottom", "0px");
            percentageBadge.setBackgroundColor(PriceBadge.Color.DEFAULT_BACKGROUND_COLOR);
            return percentageBadge;
        });
    }

    private NumberRenderer<AssetData> columnPriceRenderer(ValueProvider<AssetData, Number> priceProvider) {
        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
        return new NumberRenderer<>(priceProvider, nf, "$0.00");
    }

    private Button threeDotsBtn() {
        return new Button();
    }

    public void setItems(List<AssetData> assets) {
        Objects.requireNonNull(assets, "Grid items cannot be null");
        grid.setItems(assets);
    }

}
