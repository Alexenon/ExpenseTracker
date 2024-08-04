package com.example.application.views.components;

import com.example.application.data.models.crypto.AssetData;
import com.example.application.views.pages.AssetDetailsView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Div;
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
    | Name | Price  | 7d Change | Amount | Avg buy | Avg sell | All-time low | All-time high | Total | Invested | Realized  |
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
        Grid.Column<AssetData> nameCol = grid.addColumn(a -> a.getAssetInfo().getSymbol()).setKey("Name").setHeader("Name");
        Grid.Column<AssetData> priceCol = grid.addColumn(priceRenderer(a -> a.getAssetInfo().getPriceUsd())).setKey("Price").setHeader("Price");
//        Grid.Column<AssetData> avgBuyCol = grid.addColumn(priceRenderer(Asset::getAveragePrice)).setKey("Avg Buy").setHeader("Avg Buy");
//        Grid.Column<AssetData> amountCol = grid.addColumn(Asset::getTotalAmount).setKey("Amount").setHeader("Amount");
//        Grid.Column<AssetData> profitChangesCol = grid.addColumn(new ComponentRenderer<>(this::renderProfitChanges)).setKey("Profit Changes").setHeader("Profit Changes");
        grid.addColumn(new ComponentRenderer<>(this::threeDotsBtn)).setHeader(columnSelector);
        listColumnsToSelect = List.of(nameCol, priceCol);

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
            searchField.setValue(e.getValue().toUpperCase()); // TODO: Make uppercase field more intuitive
            dataView.refreshAll();
        });

        dataView.setFilter(asset -> {
            String searchTerm = searchField.getValue().trim();
            return searchTerm.isEmpty() || asset.getAsset().getSymbol().contains(searchTerm);
        });
    }

    private NumberRenderer<AssetData> priceRenderer(ValueProvider<AssetData, Number> priceProvider) {
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
