package com.example.application.views.components;

import com.example.application.data.models.Asset;
import com.example.application.views.pages.AssetDetailsView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.ValueProvider;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Set;

/*
    // TODO: Better display first columns
    __________________________________________________________________________________________________________________________
    | Name | Price  | 7d Change | Amount  | Avg buy | Avg sell | All-time low | All-time high | Total | Invested | Realized  |
    | BTC  | $64000 | 2%        | 0.0034 | $60000   |    -     | $10          | $73000        | $230  | $200     | $30 / 10% |
    __________________________________________________________________________________________________________________________
*/
public class AssetsGrid extends Div {

    private final Grid<Asset> grid = new Grid<>();
    private final TextField searchField = new TextField();
    private final GridListDataView<Asset> dataView = grid.setItems();

    private MultiSelectComboBox<String> columnSelector;
    private List<Grid.Column<Asset>> listColumnsToSelect;

    public AssetsGrid() {
        initializeGrid();
        initializeColumnSelector();
        initializeFilteringBySearch();
        add(searchField, grid);
    }

    private void initializeGrid() {
        columnSelector = new MultiSelectComboBox<>();
        Grid.Column<Asset> nameCol = grid.addColumn(a -> a.getAssetData().getName()).setKey("Name").setHeader("Name");
        Grid.Column<Asset> priceCol = grid.addColumn(priceRenderer(a -> a.getAssetData().getPriceUsd())).setKey("Price").setHeader("Price");
        Grid.Column<Asset> avgBuyCol = grid.addColumn(priceRenderer(Asset::getAveragePrice)).setKey("Avg Buy").setHeader("Avg Buy");
        Grid.Column<Asset> amountCol = grid.addColumn(Asset::getTotalAmount).setKey("Amount").setHeader("Amount");
        Grid.Column<Asset> profitChangesCol = grid.addColumn(new ComponentRenderer<>(this::renderProfitChanges)).setKey("Profit Changes").setHeader("Profit Changes");
        grid.addColumn(new ComponentRenderer<>(this::threeDotsBtn)).setHeader(columnSelector);
        listColumnsToSelect = List.of(nameCol, priceCol, avgBuyCol, amountCol, profitChangesCol);

        grid.getColumns().forEach(c -> {
            c.setSortable(true);
            c.setAutoWidth(true);
        });
        grid.setColumnReorderingAllowed(true);
        grid.addItemClickListener(row -> {
            System.out.println(row.getItem());
            String symbol = row.getItem().getAssetData().getSymbol().toLowerCase();
            UI.getCurrent().navigate(AssetDetailsView.class, symbol);
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
            return searchTerm.isEmpty() || asset.getAssetData().getName().contains(searchTerm);
        });
    }

    private NumberRenderer<Asset> priceRenderer(ValueProvider<Asset, Number> priceProvider) {
        DecimalFormat priceFormat = new DecimalFormat("$#,##0.00###");
        String nullPriceRepresentation = "$0.00";
        return new NumberRenderer<>(priceProvider, priceFormat, nullPriceRepresentation);
    }

    /*
        display: flex;
        align-items: center;
        justify-content: center;
        height: 30px;
        width: 50px;
        border-radius: 8px;
        background-color: rgba(240, 41, 52, 0.1); #red
        background: rgba(52, 179, 73, 0.1); # green

            --RedLightColor: #f02934;
            --GreenLightColor: #34b349;
            --RedDarkColor: #ff4d4d;
            --GreenDarkColor: #6ccf59;

        # arrow-size 16x16
    * */
    private Div renderProfitChanges(Asset asset) {
        double value = asset.getAssetData().getSpotMoving24HourChangePercentageUsd();
        Paragraph changes = new Paragraph(String.valueOf(value));
        Icon direction = new Icon();
        Div div = new Div(direction, changes);

        if (value > 0) {
            direction = VaadinIcon.ARROW_UP.create();
            direction.setColor("Green");
            changes.getStyle().set("color", "green");
        } else if (value < 0) {
            direction = VaadinIcon.ARROW_DOWN.create();
            direction.setColor("Red");
            changes.getStyle().set("color", "green");
        } else {
            direction = VaadinIcon.MINUS.create();
            direction.setColor("Gray");
            changes.getStyle().set("color", "gray");
        }

        return div;
    }

    private Button threeDotsBtn() {
        return new Button();
    }

    public void setItems(List<Asset> assets) {
        grid.setItems(assets);
    }

}
