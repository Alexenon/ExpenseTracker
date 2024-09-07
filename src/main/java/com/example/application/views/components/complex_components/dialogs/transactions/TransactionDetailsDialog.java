package com.example.application.views.components.complex_components.dialogs.transactions;

import com.example.application.data.models.InstrumentsProvider;
import com.example.application.data.models.NumberType;
import com.example.application.data.models.crypto.AssetData;
import com.example.application.data.models.crypto.CryptoTransaction;
import com.example.application.services.crypto.InstrumentsService;
import com.example.application.views.components.complex_components.PriceBadge;
import com.example.application.views.components.native_components.Container;
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

public class TransactionDetailsDialog extends Dialog {

    private final AssetData assetData;
    private final CryptoTransaction transaction;
    private final InstrumentsService instrumentsService;
    private final InstrumentsProvider instrumentsProvider;

    private final Paragraph editBtn = new Paragraph("Edit");
    private final Button closeBtn = new Button(LumoIcon.CROSS.create(), e -> this.close());

    @Autowired
    public TransactionDetailsDialog(CryptoTransaction transaction,
                                    InstrumentsService instrumentsService,
                                    InstrumentsProvider instrumentsProvider) {
        this.transaction = transaction;
        this.instrumentsService = instrumentsService;
        this.instrumentsProvider = instrumentsProvider;
        this.assetData = instrumentsProvider.getAssetDataBySymbol(transaction.getAsset().getSymbol());

        buildForm();
    }

    private void buildForm() {
        setClassName("transaction-details-modal");
        setHeaderTitle("Transaction");

        initializeFields();

        add(
                detailsTransaction(),
                detailsProfitLoss(),
                createInfoItem("Date", formatDate(transaction.getDate())),
                createInfoItem("Notes", transaction.getNotes())
        );

        getHeader().add(closeBtn);
    }

    private void initializeFields() {
        closeBtn.addClickShortcut(Key.ESCAPE);
        closeBtn.addClassName("modal-close-btn");

        editBtn.addClassName("edit-btn");
        editBtn.addClickListener(e -> {
            EditTransactionDialog editTransactionDialog = new EditTransactionDialog(assetData,
                    transaction, instrumentsService, instrumentsProvider);

            editTransactionDialog.addClickSaveBtnListener(dialog -> close());
            editTransactionDialog.addClickCancelBtnListener(dialog -> {
                this.close();
                CryptoTransaction newTransaction = editTransactionDialog.getTransaction();
                TransactionDetailsDialog newDetailsDialog = new TransactionDetailsDialog(newTransaction,
                        instrumentsService, instrumentsProvider);
                newDetailsDialog.open();
            });
            editTransactionDialog.open();
        });
    }

    private Div detailsTransaction() {
        String formattedPrice = NumberType.CURRENCY.parse(transaction.getMarketPrice());
        String formattedAmount = NumberType.AMOUNT.parse(transaction.getOrderQuantity())
                                 + " " + transaction.getAsset().getSymbol();
        Paragraph pricePerTokenField = new Paragraph(String.format("(1 %s = %s)", assetData.getSymbol(), formattedPrice));
        Paragraph totalPriceField = new Paragraph(NumberType.CURRENCY.parse(transaction.getOrderTotalCost()));

        Div priceDetails = Container.builder()
                .addComponent(new H4(formattedAmount))
                .addComponent(new HorizontalLayout(totalPriceField, pricePerTokenField))
                .build();

        Image symbolImage = new Image(assetData.getAssetInfo().getLogoUrl(), assetData.getName());
        symbolImage.setClassName("coin-overview-image");

        Div body = Container.builder()
                .addComponent(() -> {
                    String text = transaction.getType().isBuyTransaction() ? "Bought" : "Sold";
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
        double usdProfit = 123.34;
        double percentageProfit = 17.23;

        Div profitLossContainer = Container.builder()
                .addComponent(() -> {
                    Paragraph p = new Paragraph("Profit/Loss");
                    p.addClassName("transaction-profit-loss-badge-label");
                    p.getStyle().set("margin-bottom", "5px");
                    return p;
                })
                .addComponent(() -> Container.builder()
                        .addClassName("price-profit-wrapper")
                        .addComponent(new PriceBadge(usdProfit, NumberType.CURRENCY, true, false, false))
                        .addComponent(new PriceBadge(percentageProfit, NumberType.PERCENT))
                        .build())
                .build();

        return Container.builder("transaction-details-card")
                .addComponents(profitLossContainer)
                .addElement(new Element("hr"))
                .addComponent(() -> Container.builder()
                        .addClassName("transaction-profit-loss-badge-item")
                        .addComponent(new Paragraph("Current Value"))
                        .addComponent(new Paragraph(NumberType.CURRENCY.parse(assetData.getPrice())))
                        .build())
                .build();
    }

    private Div createInfoItem(String title, String value) {
        return Container.builder()
                .addClassNames("info-value-item")
                .addComponent(new Span(title))
                .addComponent(new Paragraph(value))
                .build();
    }

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

}
