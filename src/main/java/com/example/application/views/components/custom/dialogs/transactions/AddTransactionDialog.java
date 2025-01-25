package com.example.application.views.components.custom.dialogs.transactions;

import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.CryptoTransaction;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.views.components.core.Container;
import com.example.application.views.components.custom.fields.input.AmountField;
import com.example.application.views.components.custom.fields.input.AssetComboBox;
import com.example.application.views.components.custom.fields.input.CurrencyField;
import com.example.application.views.components.custom.fields.input.PriceField;
import com.example.application.views.components.utils.HasNotifications;
import com.example.application.views.components.utils.convertors.FlexibleAmountConvertor;
import com.example.application.views.components.utils.convertors.FlexiblePriceConvertor;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.DoubleRangeValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.function.Consumer;

/*
    TODO:
        [!] subtract from asset on BUYING/SELLING -> checkbox
        [!] automatic add USDT or any other currency on SELLING (on coinstats this is not supported)
        [?] Add slider for percentage buy/transfer
* */
public class AddTransactionDialog extends Dialog implements HasNotifications {

    private final CryptoTransaction transaction;
    private final InstrumentsFacadeService instrumentsFacadeService;
    private final Binder<CryptoTransaction> binder = new Binder<>(CryptoTransaction.class);

    private final AssetComboBox assetSymbolField;
    private final Select<CryptoTransaction.Type> typeField = new Select<>();
    private final AmountField amountField = new AmountField("Amount");
    private final PriceField marketPriceField = new PriceField("Price");
    private final CurrencyField totalCostField = new CurrencyField("Total");
    private final DatePicker datePicker = new DatePicker("Date");
    private final TextArea notesField = new TextArea("Notes");

    private final Button saveButton = new Button("Save");
    private final Button cancelButton = new Button("Cancel");
    private final Span symbolSuffix = new Span();

    @Autowired
    public AddTransactionDialog(InstrumentsFacadeService instrumentsFacadeService) {
        this.instrumentsFacadeService = instrumentsFacadeService;
        this.assetSymbolField = new AssetComboBox(instrumentsFacadeService);
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
                .addComponent(totalCostField)
                .addComponent(notesField)
                .addComponent(datePicker)
                .build();
        add(formBody);

        assetSymbolField.addValueChangeListener(l -> {
            binder.setValidatorsDisabled(false);
            marketPriceField.setValue(assetSymbolField.getMarketPrice());
            symbolSuffix.setText(assetSymbolField.getSymbol());
        });

        amountField.setSuffixComponent(symbolSuffix);
        amountField.setValueChangeMode(ValueChangeMode.EAGER);
        amountField.addKeyUpListener(e -> {
            double totalPrice = amountField.doubleValue() * marketPriceField.doubleValue();
            totalCostField.setValue(totalPrice);
            binder.validate();
        });

        marketPriceField.setValueChangeMode(ValueChangeMode.EAGER);
        marketPriceField.addKeyUpListener(e -> {
            double totalPrice = amountField.doubleValue() * marketPriceField.doubleValue();
            totalCostField.setValue(totalPrice);
            binder.validate();
        });

        totalCostField.setValueChangeMode(ValueChangeMode.EAGER);
        totalCostField.addKeyUpListener(e -> {
            double amount = 0;
            if (marketPriceField.doubleValue() != 0) {
                String textPrice = totalCostField.getValue().replaceAll(",", "");
                double totalPrice = Double.parseDouble(textPrice.isEmpty() ? "0" : textPrice);
                amount = totalPrice / marketPriceField.doubleValue();
            }

            amountField.setValue(amount);
            binder.validate();
        });

        saveButton.addClickShortcut(Key.ENTER);
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        saveButton.addClickListener(e -> {
            if (binder.validate().isOk()) {
                instrumentsFacadeService.saveTransaction(binder.getBean());
                showSuccessfulNotification("The transaction was saved succesfully");
                this.close();
            } else {
                showErrorNotification("Error! Please fill the fields with as required");
            }
        });

        cancelButton.addClickShortcut(Key.ESCAPE);
        cancelButton.addClickListener(e -> this.close());

        getFooter().add(saveButton, cancelButton);
    }

    private void initializeFields() {
        assetSymbolField.addValueChangeListener(l -> marketPriceField.setValue(assetSymbolField.getMarketPrice()));

        typeField.setLabel("Transaction Type");
        typeField.setItems(CryptoTransaction.Type.values());
        marketPriceField.setPaymentItems(instrumentsFacadeService.getPaymentAssets());

        // Initialize with values
        assetSymbolField.setValue(null);
        typeField.setValue(CryptoTransaction.Type.BUY);
        amountField.setValue("");
        marketPriceField.setValue(assetSymbolField.getMarketPrice());
        totalCostField.setValue(0);
        datePicker.setValue(LocalDate.now());
    }

    private void initializeBinder() {
        binder.setBean(transaction);

        binder.forField(assetSymbolField)
                .asRequired("Please fill this field")
                .bind(CryptoTransaction::getTradedAsset, CryptoTransaction::setTradedAsset);

        binder.forField(marketPriceField.getPaymentSelector())
                .asRequired("Please fill this field")
                .bind(CryptoTransaction::getPaymentAsset, CryptoTransaction::setPaymentAsset);

        binder.forField(typeField)
                .asRequired("Please fill this field")
                .bind(CryptoTransaction::getType, CryptoTransaction::setType);

        binder.forField(amountField)
                .asRequired("Please fill this field")
                .withConverter(new FlexibleAmountConvertor())
                .withValidator(new DoubleRangeValidator("Invalid decimal value", (double) 0, Double.MAX_VALUE))
                .withValidator(amount -> amount > 0, "Amount should be bigger than 0")
                .bind(CryptoTransaction::getOrderQuantity, CryptoTransaction::setOrderQuantity);

        binder.forField(marketPriceField)
                .asRequired("Please fill this field")
                .withConverter(new FlexiblePriceConvertor())
                .withValidator(new DoubleRangeValidator("Invalid decimal value", (double) 0, Double.MAX_VALUE))
                .withValidator(price -> price > 0, "Market price should be bigger than 0")
                .bind(CryptoTransaction::getMarketPrice, CryptoTransaction::setMarketPrice);

        binder.forField(totalCostField)
                .asRequired("Please fill this field")
                .withConverter(new FlexiblePriceConvertor())
                .withValidator(new DoubleRangeValidator("Invalid decimal value", (double) 0, Double.MAX_VALUE))
                .withValidator(price -> price >= 1, "Total price should be at least one dollar")
                .bind(CryptoTransaction::getOrderTotalCost, CryptoTransaction::setOrderTotalCost);

        binder.forField(notesField)
                .bind(CryptoTransaction::getNotes, CryptoTransaction::setNotes);

        binder.forField(datePicker)
                .bind(CryptoTransaction::getDate, CryptoTransaction::setDate);
    }

    public void addSaveBtnClickListener(Consumer<?> listener) {
        saveButton.addClickListener(e -> listener.accept(null));
    }

    public void setAsset(Asset asset) {
        assetSymbolField.setValue(asset);
    }

}
