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

import com.example.application.data.models.InstrumentsProvider;
import com.example.application.data.models.crypto.AssetData;
import com.example.application.data.models.crypto.CryptoTransaction;
import com.example.application.services.crypto.InstrumentsService;
import com.example.application.views.components.complex_components.dialogs.transactions.AddTransactionDialog;
import com.example.application.views.components.complex_components.dialogs.transactions.TransactionDetailsDialog;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.theme.lumo.LumoIcon;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

public class TransactionsGrid extends Div {

    private final InstrumentsService instrumentsService;
    private final InstrumentsProvider instrumentsProvider;

    private final ComboBox<AssetData> nameSearchField = new ComboBox<>();
    private final MultiSelectComboBox<CryptoTransaction.TransactionType> typeSearchField = new MultiSelectComboBox<>("Transaction Type");
    private final Grid<CryptoTransaction> grid = new Grid<>();
    private final GridListDataView<CryptoTransaction> dataView = grid.setItems();
    private final Button addTransactionBtn = new Button("Add Holding", LumoIcon.PLUS.create());

    @Autowired
    public TransactionsGrid(InstrumentsService instrumentsService, InstrumentsProvider instrumentsProvider) {
        this.instrumentsService = instrumentsService;
        this.instrumentsProvider = instrumentsProvider;
        initializeGrid();
        initializeFilteringBySearch();
        add(gridHeader(), grid);
    }

    private Div gridHeader() {
        addTransactionBtn.setIconAfterText(false);
        addTransactionBtn.addClickListener(e -> {
            AddTransactionDialog dialog = new AddTransactionDialog(instrumentsService, instrumentsProvider);
            dialog.open();
        });
        Div header = new Div(nameSearchField, typeSearchField, addTransactionBtn);
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
                    new TransactionDetailsDialog(row.getItem(), instrumentsService, instrumentsProvider);
            detailsDialog.open();
        });
    }

    private void initializeFilteringBySearch() {
        nameSearchField.addClassName("asset-search-field");
        nameSearchField.setPlaceholder("Search");
        nameSearchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        nameSearchField.setAllowCustomValue(false);

        ComboBox.ItemFilter<AssetData> nameSearchFilter = (assetData, filterString) -> {
            String lowercaseInput = filterString.toLowerCase();

            return assetData.getSymbol().toLowerCase().startsWith(lowercaseInput)
                   || assetData.getName().toLowerCase().startsWith(lowercaseInput);
        };

        nameSearchField.setItems(nameSearchFilter, instrumentsProvider.getListOfAssetData());
        nameSearchField.setItemLabelGenerator(AssetData::getName); // TODO: Icon + Full Name
        nameSearchField.addValueChangeListener(e -> applyFilter());

        typeSearchField.setItems(CryptoTransaction.TransactionType.values());
        typeSearchField.setClearButtonVisible(true);
        typeSearchField.addValueChangeListener(e -> applyFilter());
    }

    private void applyFilter() {
        AssetData selectedAssetData = nameSearchField.getValue();
        Set<CryptoTransaction.TransactionType> selectedTypes = typeSearchField.getSelectedItems();

        dataView.setFilter(transaction -> {
            boolean nameFilter = selectedAssetData == null
                                 || transaction.getAsset().equals(selectedAssetData.getAsset());

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
