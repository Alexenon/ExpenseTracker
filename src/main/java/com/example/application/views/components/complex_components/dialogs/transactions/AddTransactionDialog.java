package com.example.application.views.components.complex_components.dialogs.transactions;

import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.CryptoTransaction;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.views.components.fields.AmountField;
import com.example.application.views.components.fields.CurrencyField;
import com.example.application.views.components.native_components.Container;
import com.example.application.views.components.utils.convertors.FlexibleAmountConvertor;
import com.example.application.views.components.utils.convertors.FlexiblePriceConvertor;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

// TODO:
//  - boolean subtractFromGivenAsset
//  - Add slider for percentage buy/transfer
public class AddTransactionDialog extends Dialog {

    private final Asset asset;
    private final CryptoTransaction transaction;
    private final InstrumentsFacadeService instrumentsFacadeService;
    private final Binder<CryptoTransaction> binder = new Binder<>(CryptoTransaction.class);

    private final ComboBox<Asset> assetSymbolField = new ComboBox<>("Asset");
    private final Select<CryptoTransaction.TransactionType> typeField = new Select<>();
    private final AmountField amountField = new AmountField("Amount");
    private final CurrencyField marketPriceField = new CurrencyField("Price");
    private final CurrencyField totalPriceField = new CurrencyField("Total");
    private final DatePicker datePicker = new DatePicker("Date");
    private final TextArea notesField = new TextArea("Notes");

    private final Button saveButton = new Button("Save");
    private final Button cancelButton = new Button("Cancel");

    public AddTransactionDialog(InstrumentsFacadeService instrumentsFacadeService) {
        this(null, instrumentsFacadeService);
    }

    @Autowired
    public AddTransactionDialog(Asset asset, InstrumentsFacadeService instrumentsFacadeService) {
        this.asset = asset;
        this.instrumentsFacadeService = instrumentsFacadeService;
        this.transaction = new CryptoTransaction();

        buildForm();
    }

    private void buildForm() {
        setHeaderTitle("Transaction");
        initializeBinder();
        initializeFields();

        Container formBody = Container.builder("transaction-modal")
                .addComponent(assetSymbolField)
                .addComponent(typeField)
                .addComponent(amountField)
                .addComponent(marketPriceField)
                .addComponent(totalPriceField)
                .addComponent(notesField)
                .addComponent(datePicker)
                .build();
        add(formBody);

        assetSymbolField.addValueChangeListener(l -> {
            binder.setValidatorsDisabled(false);
            marketPriceField.setValue(getMarketPriceBySelectedAsset());
            // TODO: Can be improved by not creating a new span each time
            amountField.setSuffixComponent(new Span(getSelectedAssetSymbol()));
        });

        amountField.setValueChangeMode(ValueChangeMode.EAGER);
        amountField.addKeyUpListener(e -> {
            double totalPrice = amountField.doubleValue() * marketPriceField.doubleValue();
            totalPriceField.setValue(totalPrice);
            binder.validate();
        });

        marketPriceField.setValueChangeMode(ValueChangeMode.EAGER);
        marketPriceField.addKeyUpListener(e -> {
            double totalPrice = amountField.doubleValue() * marketPriceField.doubleValue();
            totalPriceField.setValue(totalPrice);
            binder.validate();
        });

        totalPriceField.setValueChangeMode(ValueChangeMode.EAGER);
        totalPriceField.addKeyUpListener(e -> {
            double amount = 0;
            if (marketPriceField.doubleValue() != 0) {
                String textPrice = totalPriceField.getValue().replaceAll(",", "");
                double totalPrice = Double.parseDouble(textPrice.isEmpty() ? "0" : textPrice);
                amount = totalPrice / marketPriceField.doubleValue();
            }

            amountField.setValue(amount);
            binder.validate();
        });

        saveButton.addClickShortcut(Key.ENTER);
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        saveButton.addClickListener(e -> {
            CryptoTransaction savedTransaction = instrumentsFacadeService.saveTransaction(binder.getBean());
            this.close();
            System.out.printf("Saved -> %s\n", savedTransaction);
        });

        cancelButton.addClickShortcut(Key.ESCAPE);
        cancelButton.addClickListener(e -> this.close());

        getFooter().add(saveButton, cancelButton);
    }

    private void initializeFields() {
        assetSymbolField.setItems(instrumentsFacadeService.getAllAssets());
        assetSymbolField.setItemLabelGenerator(instrumentsFacadeService::getAssetFullName);
        assetSymbolField.setRenderer(assetSymbolRenderer());
        assetSymbolField.addValueChangeListener(l -> marketPriceField.setValue(getMarketPriceBySelectedAsset()));

        typeField.setLabel("Transaction Type");
        typeField.setItems(CryptoTransaction.TransactionType.values());

        // Initialize with values
        assetSymbolField.setValue(asset);
        typeField.setValue(CryptoTransaction.TransactionType.BUY);
        amountField.setValue("");
        marketPriceField.setValue(getMarketPriceBySelectedAsset());
        totalPriceField.setValue(0);
        datePicker.setValue(LocalDate.now());
    }

    private LitRenderer<Asset> assetSymbolRenderer() {
        return LitRenderer.<Asset>of(
                        "<div class='coin-overview-name-container'>" +
                        "  <img class='rounded coin-overview-image' src='${item.imgUrl}' alt='${item.fullName}'/>" +
                        "  <span>${item.symbol}</span>" +
                        "  <p>${item.fullName}</p>" +
                        "</div>")
                .withProperty("imgUrl", instrumentsFacadeService::getAssetImgUrl)
                .withProperty("symbol", Asset::getSymbol)
                .withProperty("fullName", instrumentsFacadeService::getAssetFullName);
    }

    private void initializeBinder() {
        binder.setBean(transaction);

        binder.forField(assetSymbolField)
                .asRequired("Please fill this field")
                .bind(CryptoTransaction::getAsset, CryptoTransaction::setAsset);

        binder.forField(typeField)
                .asRequired("Please fill this field")
                .bind(CryptoTransaction::getType, CryptoTransaction::setType);

        binder.forField(amountField)
                .asRequired("Please fill this field")
                .withConverter(new FlexibleAmountConvertor())
                .withValidator(amount -> amount > 0, "Price should be bigger than 0")
                .bind(CryptoTransaction::getOrderQuantity, CryptoTransaction::setOrderQuantity);

        binder.forField(marketPriceField)
                .asRequired("Please fill this field")
                .withConverter(new FlexiblePriceConvertor())
                .withValidator(price -> price > 0, "Price should be bigger than 0")
                .bind(CryptoTransaction::getMarketPrice, CryptoTransaction::setMarketPrice);

        binder.forField(totalPriceField)
                .asRequired("Please fill this field")
                .withConverter(new FlexiblePriceConvertor())
                .withValidator(price -> price >= 1, "Total price should be at least one dollar")
                .bind(CryptoTransaction::getOrderTotalCost, CryptoTransaction::setOrderTotalCost);

        binder.forField(notesField)
                .bind(CryptoTransaction::getNotes, CryptoTransaction::setNotes);

        binder.forField(datePicker)
                .bind(CryptoTransaction::getDate, CryptoTransaction::setDate);
    }

    private double getMarketPriceBySelectedAsset() {
        Asset selectedAsset = assetSymbolField.getValue();

        if (selectedAsset == null)
            return 0;

        return instrumentsFacadeService.getAssetPrice(selectedAsset);
    }

    private String getSelectedAssetSymbol() {
        Asset selectedAsset = assetSymbolField.getValue();

        if (selectedAsset == null)
            return "";

        return selectedAsset.getSymbol();
    }


}
