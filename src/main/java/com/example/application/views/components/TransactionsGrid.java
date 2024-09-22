package com.example.application.views.components;


/*
    TODO:
     - [?] Add sync button functionality(don't forget about checkbox value)
    _______________________________________________________________________________________________________________________________________
    | Name | Price  | Total Cost  | Amount | Edit | Delete |
    | BTC  | $64000 | $450        | 0.0034 | [⚒]  |  [❌]  |
    _______________________________________________________________________________________________________________________________________
*/

import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.CryptoTransaction;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.views.components.complex_components.dialogs.transactions.TransactionDetailsDialog;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.function.ValueProvider;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

public class TransactionsGrid extends Div {

    private final InstrumentsFacadeService instrumentsFacadeService;

    private final ComboBox<Asset> nameSearchField = new ComboBox<>();
    private final MultiSelectComboBox<CryptoTransaction.TransactionType> typeSearchField = new MultiSelectComboBox<>("Transaction Type");
    private final Grid<CryptoTransaction> grid = new Grid<>();
    private final GridListDataView<CryptoTransaction> dataView = grid.setItems();

    @Autowired
    public TransactionsGrid(InstrumentsFacadeService instrumentsFacadeService) {
        this.instrumentsFacadeService = instrumentsFacadeService;
        initializeGrid();
        initializeFilteringBySearch();
        add(gridHeader(), grid);
    }

    private Div gridHeader() {
        Div header = new Div(nameSearchField, typeSearchField);
        header.addClassName("assets-grid-header");
        return header;
    }

    private void initializeGrid() {
        grid.addColumn(t -> t.getAsset().getSymbol()).setKey("Name").setHeader("Name");
        grid.addColumn(columnPriceRenderer(CryptoTransaction::getMarketPrice)).setKey("Price").setHeader("Price");
        grid.addColumn(CryptoTransaction::getOrderQuantity).setKey("Amount").setHeader("Amount");

        grid.setColumnReorderingAllowed(true);
        grid.getColumns().forEach(column -> {
            column.setSortable(true);
            column.setAutoWidth(true);
        });

        grid.addItemClickListener(row -> {
            TransactionDetailsDialog detailsDialog =
                    new TransactionDetailsDialog(row.getItem(), instrumentsFacadeService);
            detailsDialog.open();
        });
    }

    private void initializeFilteringBySearch() {
        nameSearchField.addClassName("asset-search-field");
        nameSearchField.setPlaceholder("Search");
        nameSearchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        nameSearchField.setAllowCustomValue(false);

        ComboBox.ItemFilter<Asset> nameSearchFilter = (asset, filterString) -> {
            String lowercaseInput = filterString.toLowerCase();
            String lowercaseSymbol = asset.getSymbol().toLowerCase();
            String lowercaseName = instrumentsFacadeService.getAssetFullName(asset).toLowerCase();

            return lowercaseSymbol.startsWith(lowercaseInput) || lowercaseName.startsWith(lowercaseInput);
        };

        nameSearchField.setItems(nameSearchFilter, instrumentsFacadeService.getAllAssets());
        nameSearchField.setItemLabelGenerator(instrumentsFacadeService::getAssetFullName); // TODO: Icon + Full Name
        nameSearchField.addValueChangeListener(e -> applyFilter());

        typeSearchField.setItems(CryptoTransaction.TransactionType.values());
        typeSearchField.setClearButtonVisible(true);
        typeSearchField.addValueChangeListener(e -> applyFilter());
    }

    private void applyFilter() {
        Asset selectedAsset = nameSearchField.getValue();
        Set<CryptoTransaction.TransactionType> selectedTypes = typeSearchField.getSelectedItems();

        dataView.setFilter(transaction -> {
            boolean nameFilter = selectedAsset == null
                                 || transaction.getAsset().equals(selectedAsset);

            boolean typeFilter = selectedTypes.isEmpty()
                                 || selectedTypes.contains(transaction.getType());

            return nameFilter && typeFilter;
        });
    }

    private NumberRenderer<CryptoTransaction> columnPriceRenderer(ValueProvider<CryptoTransaction, Number> priceProvider) {
        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
        return new NumberRenderer<>(priceProvider, nf, "$0.00");
    }

    public void setItems(List<CryptoTransaction> transactions) {
        grid.setItems(Objects.requireNonNull(transactions));
    }

    public void setPageSize(int size) {
        grid.setPageSize(size);
    }

}
