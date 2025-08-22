package com.example.application.views.components.custom.dialogs.transactions;

import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.CryptoTransaction;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.utils.common.formatters.number.AmountFormatter;
import com.example.application.utils.common.formatters.number.CurrencyFormatter;
import com.example.application.utils.investment.ProfitUtils;
import com.example.application.views.components.core.Container;
import com.example.application.views.components.custom.fields.PricePercentageWrapper;
import com.example.application.views.components.custom.fields.stats.ProfitStatsDisplay;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.theme.lumo.LumoIcon;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.function.Consumer;

// TODO:
//  - [!] ICONS: vaadin:trending-down | vaadin:trending-up
//  - [!] Notes are missing
//  - [!] Edit btn looks very ugly position
public class TransactionDetailsDialog extends Dialog {

    private static final AmountFormatter amountFormatter = AmountFormatter.withDefaults();
    private static final CurrencyFormatter currencyFormatter = CurrencyFormatter.withDefaults();

    private CryptoTransaction transaction;
    private final InstrumentsFacadeService instrumentsFacadeService;

    private final Paragraph editBtn = new Paragraph("Edit");
    private final Button closeBtn = new Button(LumoIcon.CROSS.create(), e -> this.close());

    @Autowired
    public TransactionDetailsDialog(CryptoTransaction transaction, InstrumentsFacadeService instrumentsFacadeService) {
        this.transaction = transaction;
        this.instrumentsFacadeService = instrumentsFacadeService;

        initializeForm();
        buildForm();
    }

    private void initializeForm() {
        setClassName("transaction-details-modal");
        setHeaderTitle("Transaction");
        initializeFields();
        getHeader().add(closeBtn);
    }

    private void buildForm() {
        add(
                detailsTransaction(),
                detailsProfitLoss(),
                createInfoItem("Date", formatDate(transaction.getDate())),
                createInfoItem("Notes", transaction.getNotes())
        );
    }

    private void initializeFields() {
        closeBtn.addClickShortcut(Key.ESCAPE);
        closeBtn.addClassName("modal-close-btn");
        editBtn.addClassName("edit-btn");
    }

    private Div detailsTransaction() {
        Asset asset = transaction.getAsset();
        String symbol = asset.getSymbol();
        String formattedPrice = currencyFormatter.format(transaction.getMarketPrice());
        String formattedAmount = amountFormatter.format(transaction.getOrderQuantity(), symbol);
        Paragraph pricePerTokenField = new Paragraph(String.format("(1 %s = %s)", symbol, formattedPrice));
        Paragraph totalCostField = new Paragraph(currencyFormatter.format(transaction.getOrderTotalCost()));

        Div priceDetails = Container.builder()
                .addComponent(new H4(formattedAmount))
                .addComponent(new HorizontalLayout(totalCostField, pricePerTokenField))
                .build();

        Image symbolImage = new Image(asset.getImageUrl(), symbol);
        symbolImage.setClassName("coin-overview-image");

        Div body = Container.builder()
                .addComponent(() -> {
                    String text = transaction.isBuyTransaction() ? "Bought" : "Sold";
                    Span typeField = new Span(text);
                    typeField.addClassName("transaction-profit-loss-badge-label");
                    return typeField;
                })
                .addComponent(editBtn)
                .addComponent(new Container("price-profit-wrapper", symbolImage, priceDetails))
                .build();

        return new Container("transaction-details-card", body);
    }

    private Div detailsProfitLoss() {
        Asset asset = transaction.getAsset();
        double buyPrice = transaction.getMarketPrice();
        double sellPrice = asset.getMarketPrice();
        double totalCost = transaction.getOrderTotalCost();

        double usdProfit = ProfitUtils.netProfit(buyPrice, sellPrice, totalCost);
        double percentageProfit = ProfitUtils.growthPercentage(buyPrice, sellPrice) - 100;

        Div profitLossContainer = Container.builder()
                .addComponent(() -> {
                    Paragraph p = new Paragraph("Profit/Loss");
                    p.addClassName("transaction-profit-loss-badge-label");
                    p.getStyle().set("margin-bottom", "5px");
                    return p;
                })
                .addComponent(new PricePercentageWrapper(usdProfit, percentageProfit))
                .build();

        double tokensAmount = instrumentsFacadeService.getAmountOfTokens(asset);

        return Container.builder("transaction-details-card")
                .addComponents(profitLossContainer)
                .addElement(new Element("hr"))
                .addComponent(new ProfitStatsDisplay("Current Price", currencyFormatter.format(asset.getMarketPrice())))
                .addElement(new Element("hr"))
                .addComponent(new ProfitStatsDisplay("Current Amount", amountFormatter.format(tokensAmount, asset)))
                .build();
    }

    private Div createInfoItem(String title, String value) {
        return Container.builder()
                .addClassNames("info-value-item")
                .addComponent(new Span(title))
                .addComponent(new Paragraph(value))
                .build();
    }

    // TODO: Add separate class -> DateFormatter
    private String formatDate(LocalDate date) {
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendText(ChronoField.MONTH_OF_YEAR)
                .appendLiteral(" ")
                .appendValue(ChronoField.DAY_OF_MONTH)
                .appendLiteral(", ")
                .appendValue(ChronoField.YEAR)
                .toFormatter();

        return date.format(formatter);
    }

    private void rebuildForm() {
        removeAll();
        buildForm();
    }

    public void addUpdateTransactionListener(Consumer<?> listener) {
        editBtn.addClickListener(e -> {
            EditTransactionDialog editTransactionDialog = new EditTransactionDialog(transaction, instrumentsFacadeService);
            editTransactionDialog.addSaveListener(dialog -> {
                transaction = editTransactionDialog.getTransaction();
                rebuildForm();
                listener.accept(null);
            });
            editTransactionDialog.open();
        });
    }

}
