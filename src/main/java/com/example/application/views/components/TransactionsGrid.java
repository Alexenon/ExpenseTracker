package com.example.application.views.components;


/*
    TODO:
     - Think about column header style(maybe align item center)
     - Add sync button functionality(don't forget about checkbox value)
    
    _______________________________________________________________________________________________________________________________________
    | Name | Price  | Total Cost  | Amount | Edit | Delete |
    | BTC  | $64000 | $450        | 0.0034 | [⚒]  |  [❌]  |
    _______________________________________________________________________________________________________________________________________
*/

import com.example.application.data.models.crypto.CryptoTransaction;
import com.example.application.services.crypto.InstrumentsService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.ValueProvider;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.NumberFormat;
import java.util.Locale;

public class TransactionsGrid extends Div {

    private final InstrumentsService instrumentsService;

    private final Grid<CryptoTransaction> grid = new Grid<>();
    private final TextField searchField = new TextField();
    private final GridListDataView<CryptoTransaction> dataView = grid.setItems();

    @Autowired
    public TransactionsGrid(InstrumentsService instrumentsService) {
        this.instrumentsService = instrumentsService;
        initializeGrid();
        initializeFilteringBySearch();
        add(gridHeader(), grid);
    }

    private Div gridHeader() {
        Div header = new Div(searchField);
        header.addClassName("assets-grid-header");
        return header;
    }

    private void initializeGrid() {
        grid.setItems(instrumentsService.getTransactions());
        grid.addColumn(t -> t.getAsset().getSymbol()).setKey("Name").setHeader("Name");
        grid.addColumn(columnPriceRenderer(CryptoTransaction::getMarketPrice)).setKey("Price").setHeader("Price");
        grid.addColumn(CryptoTransaction::getOrderQuantity).setKey("Amount").setHeader("Amount");

        grid.getColumns().forEach(c -> {
            c.setSortable(true);
            c.setAutoWidth(true);
        });
        grid.setColumnReorderingAllowed(true);
//        grid.addItemClickListener(row -> {
//            System.out.println(row.getItem());
//            UI.getCurrent().navigate(AssetDetailsView.class, row.getItem().getAsset().getSymbol().toUpperCase());
//        });
    }

    private void initializeFilteringBySearch() {
        searchField.addClassName("asset-search-field");
        searchField.setPlaceholder("Search transaction");
        searchField.setSuffixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);

        searchField.addValueChangeListener(e -> {
            String searchTerm = searchField.getValue().trim().toLowerCase();

            // TODO: THINK HERE
//            dataView.setFilter(transaction -> {
//                String symbol = transaction.getAsset().getSymbol().toLowerCase();
//                return searchTerm.isEmpty() || symbol.contains(searchTerm) || name.contains(searchTerm);
//            });
        });
    }

    private NumberRenderer<CryptoTransaction> columnPriceRenderer(ValueProvider<CryptoTransaction, Number> priceProvider) {
        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
        return new NumberRenderer<>(priceProvider, nf, "$0.00");
    }

}
