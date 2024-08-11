package com.example.application.views.components;

import com.example.application.data.models.crypto.AssetData;
import com.example.application.views.components.native_components.Container;
import com.example.application.views.pages.AssetDetailsView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/*
    // TODO: Better display first columns
    _________________________________________________________________________________________________________________________
    | Name | Price  | 7d Change | Amount | Avg buy | Avg sell | All-time low | All-time high | Value | Invested | Realized  |
    | BTC  | $64000 | 2%        | 0.0034 | $60000  |    -     | $10          | $73000        | $230  | $200     | $30 / 10% |
    _________________________________________________________________________________________________________________________
*/
public class AssetsGrid extends Div {

    private final Grid<AssetData> grid = new Grid<>();
    private final TextField searchField = new TextField();
    private final GridListDataView<AssetData> dataView = grid.setItems();

    private MultiSelectComboBox<String> columnSelector;
    private List<Grid.Column<AssetData>> listColumnsToSelect;

    public AssetsGrid() {
        initializeGrid();
        initializeColumnSelector();
        initializeFilteringBySearch();
        add(searchField, grid);
    }

    private void initializeGrid() {
        columnSelector = new MultiSelectComboBox<>();

        Grid.Column<AssetData> nameCol = grid.addColumn(columnNameRenderer()).setKey("Name").setHeader("Name");
        Grid.Column<AssetData> priceCol = grid.addColumn(columnPriceRenderer(a -> a.getAssetInfo().getPriceUsd())).setKey("Price").setHeader("Price");
        Grid.Column<AssetData> avgBuyCol = grid.addColumn(columnPriceRenderer(AssetData::getAverageBuyPrice)).setKey("Avg Buy").setHeader("Avg Buy");
        Grid.Column<AssetData> avgSellCol = grid.addColumn(columnPriceRenderer(AssetData::getAverageSellPrice)).setKey("Avg Sell").setHeader("Avg Sell");
        Grid.Column<AssetData> amountCol = grid.addColumn(a -> a.getAsset().getAmount()).setKey("Amount").setHeader("Amount");
//        Grid.Column<AssetData> profitChangesCol = grid.addColumn(new ComponentRenderer<>(this::renderProfitChanges)).setKey("Profit Changes").setHeader("Profit Changes");
        grid.addColumn(new ComponentRenderer<>(this::threeDotsBtn)).setHeader(columnSelector);
        listColumnsToSelect = List.of(nameCol, priceCol, avgBuyCol, avgSellCol, amountCol);

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
        searchField.setWidth("50%");
        searchField.setPlaceholder("Search");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> {
            searchField.setValue(e.getValue().toUpperCase()); // TODO: Make uppercase transform to be more intuitive on web displaying
            dataView.refreshAll();
        });

        dataView.setFilter(asset -> {
            String searchTerm = searchField.getValue().trim();
            return searchTerm.isEmpty() || asset.getAsset().getSymbol().contains(searchTerm);
        });
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

    private NumberRenderer<AssetData> columnPriceRenderer(ValueProvider<AssetData, Number> priceProvider) {
        DecimalFormat priceFormat = new DecimalFormat("$#,##0.00###");
        String nullPriceRepresentation = "$0.00";
        return new NumberRenderer<>(priceProvider, priceFormat, nullPriceRepresentation);
    }

    private NumberRenderer<AssetData> priceRenderer2(ValueProvider<AssetData, Number> priceProvider) {
        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
        return new NumberRenderer<>(priceProvider, nf, "$0.00");
    }

    private Button threeDotsBtn() {
        return new Button();
    }

    public void setItems(List<AssetData> assets) {
        Objects.requireNonNull(assets, "Grid filled with null values");
        grid.setItems(assets);
    }

}
