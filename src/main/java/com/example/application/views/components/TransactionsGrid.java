package com.example.application.views.components;

/*
    TODO:
     - [!] Add Edit/Delete btn, directly in the grid, and in the display itself
     - [?] Add sync button functionality(don't forget about checkbox value)
    _______________________________________________________________________________________________________________________________________
    | Name | Price  | Total Cost  | Amount | Edit | Delete |
    | BTC  | $64000 | $450        | 0.0034 | [⚒]  |  [❌]  |
    _______________________________________________________________________________________________________________________________________
*/

import com.example.application.entities.common.TransactionType;
import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.Transaction;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.utils.common.formatters.CommonFormatters;
import com.example.application.utils.common.formatters.number.AmountFormatter;
import com.example.application.utils.common.formatters.number.CurrencyFormatter;
import com.example.application.utils.common.formatters.number.PercentageFormatter;
import com.example.application.utils.investment.ProfitUtils;
import com.example.application.views.components.core.Container;
import com.example.application.views.components.custom.dialogs.transactions.TransactionCreatedOrUpdatedEvent;
import com.example.application.views.components.custom.dialogs.transactions.TransactionDetailsDialog;
import com.example.application.views.components.custom.fields.AssetComboBox;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.function.ValueProvider;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

public class TransactionsGrid extends Div {
	private final InstrumentsFacadeService instrumentsFacadeService;

	private final AssetComboBox nameSearchField;
	private final MultiSelectComboBox<TransactionType> typeSearchField = new MultiSelectComboBox<>("Transaction Type");
	private final Grid<Transaction> grid = new Grid<>();
    private final GridListDataView<Transaction> gridDataView = grid.setItems();

    public TransactionsGrid(InstrumentsFacadeService instrumentsFacadeService) {
		this.instrumentsFacadeService = Objects.requireNonNull(instrumentsFacadeService, "instrumentsFacadeService");

        this.nameSearchField = new AssetComboBox(instrumentsFacadeService);
        initializeGrid();
        initializeGridColumns();
        initializeFilteringBySearch();
        add(gridHeader(), grid);
    }

    private Div gridHeader() {
        return new Container("assets-grid-header", nameSearchField, typeSearchField);
    }

    private void initializeGrid() {
        grid.setColumnReorderingAllowed(true);
		grid.addItemClickListener(row -> new TransactionDetailsDialog(row.getItem(), instrumentsFacadeService).open());
		ComponentUtil.addListener(UI.getCurrent(), TransactionCreatedOrUpdatedEvent.class, event -> rebuildTable());
    }

    private void initializeGridColumns() {
        grid.addColumn(t -> t.getAsset().getSymbol()).setHeader("Name").setFrozen(true);
        grid.addColumn(quantityColumnRenderer()).setHeader("Quantity");
        grid.addColumn(priceColumnRenderer()).setHeader("Price");
        grid.addColumn(priceColumnRenderer(Transaction::getOrderTotalCost)).setHeader("Total");
        grid.addColumn(new LocalDateTimeRenderer<>(Transaction::getDateTime, CommonFormatters.DATE_FRIENDLY_FORMAT)).setHeader("Date");
        grid.addColumn(profitLossColumnRenderer()).setHeader("Profit/Loss").setFrozenToEnd(true);
        grid.getColumns().forEach(column -> {
            column.setSortable(true);
            column.setAutoWidth(true);
        });
    }

    private void initializeFilteringBySearch() {
        nameSearchField.addClassName("asset-search-field");
        nameSearchField.setPlaceholder("Search");
        nameSearchField.setPrefixComponent(seachIcon());
        nameSearchField.setAllowCustomValue(false);
        nameSearchField.addValueChangeListener(e -> applyFilter());

        typeSearchField.setItems(TransactionType.values());
        typeSearchField.setClearButtonVisible(true);
        typeSearchField.addValueChangeListener(e -> applyFilter());
    }

    private static Icon seachIcon() {
        Icon icon = new Icon(VaadinIcon.SEARCH);
        icon.addClassName("search-icon");
        return icon;
    }

    private void applyFilter() {
        Asset selectedAsset = nameSearchField.getValue();
        Set<TransactionType> selectedTypes = typeSearchField.getSelectedItems();

        gridDataView.setFilter(transaction -> {
            boolean nameFilter = selectedAsset == null || transaction.getAsset().equals(selectedAsset);
            boolean typeFilter = selectedTypes.isEmpty() || selectedTypes.contains(transaction.getType());

            return nameFilter && typeFilter;
        });
    }

    public void setItems(List<Transaction> transactions) {
        grid.setItems(Objects.requireNonNull(transactions));
    }

    public void setPageSize(int size) {
        grid.setPageSize(size);
    }

    private LitRenderer<Transaction> priceColumnRenderer() {
        return LitRenderer.<Transaction>of("<p class='asset-price'>${item.price}</p>")
                .withProperty("price", t -> CurrencyFormatter.withDefaults().format(t.getMarketPrice()));
    }

    private LitRenderer<Transaction> quantityColumnRenderer() {
        AmountFormatter amountFormatter = new AmountFormatter();
        return LitRenderer.<Transaction>of("<p class='${item.className}'> ${item.quantity} ${item.symbol}</p>")
                .withProperty("className", t -> t.isBuyTransaction() ? "value-increase" : "value-decrease")
                .withProperty("quantity", t -> {
                    double quantity = t.getOrderQuantity();
                    String sign = t.isBuyTransaction() ? "+" : "-";
                    return String.format("%s %s", sign, amountFormatter.format(quantity));
                })
                .withProperty("symbol", t -> t.getAsset().getSymbol());
    }

    private LitRenderer<Transaction> profitLossColumnRenderer() {
        return LitRenderer.<Transaction>of("<div class='transaction-profit-loss ${item.className}'>" +
                                           "  <p class='text-l'>${item.profit}</p>" +
                                           "  <p class='text-s'>${item.profitPercentage}</p>" +
                                           "</div>")
                .withProperty("className", this::getProfitLossClassName)
                .withProperty("profit", transaction -> {
                    double currentPrice = transaction.getAsset().getMarketPrice();
                    double profit = ProfitUtils.netProfit(transaction, currentPrice);
                    return CurrencyFormatter.withDefaults().format(profit);
                })
                .withProperty("profitPercentage", transaction -> {
                    double currentPrice = transaction.getAsset().getMarketPrice();
                    double percentage = ProfitUtils.growthPercentage(transaction.getMarketPrice(), currentPrice);
                    return PercentageFormatter.withDefaults().format(percentage);
                });
    }

    private NumberRenderer<Transaction> priceColumnRenderer(ValueProvider<Transaction, Number> priceProvider) {
        return new NumberRenderer<>(priceProvider, NumberFormat.getCurrencyInstance(Locale.US), "$0.00");
    }

    private String getProfitLossClassName(Transaction transaction) {
        double currentPrice = transaction.getAsset().getMarketPrice();
        double profit = ProfitUtils.netProfit(transaction, currentPrice);

        if (profit == 0)
            return "";

        // FIXME: EMMM??? -> REFACTOR
        return profit > 0 ? "value-increase" : "value-decrease";
    }

	public void rebuildTable() {
		grid.removeAllColumns();
		initializeGridColumns();
	}

}