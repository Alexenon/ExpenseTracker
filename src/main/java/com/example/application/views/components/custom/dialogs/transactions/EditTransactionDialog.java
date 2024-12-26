package com.example.application.views.components.custom.dialogs.transactions;

import com.example.application.entities.crypto.CryptoTransaction;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.views.components.core.Container;
import com.example.application.views.components.custom.fields.AmountField;
import com.example.application.views.components.custom.fields.AssetComboBox;
import com.example.application.views.components.custom.fields.CurrencyField;
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
import com.vaadin.flow.data.value.ValueChangeMode;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.function.Consumer;

public class EditTransactionDialog extends Dialog {

    private final CryptoTransaction transaction;
    private final InstrumentsFacadeService instrumentsFacadeService;
    private final Binder<CryptoTransaction> binder = new Binder<>(CryptoTransaction.class);

    private final AssetComboBox assetSymbolField;
    private final Select<CryptoTransaction.TransactionType> typeField = new Select<>();
    private final AmountField amountField = new AmountField("Amount");
    private final CurrencyField marketPriceField = new CurrencyField("Price");
    private final CurrencyField totalCostField = new CurrencyField("Total");
    private final DatePicker datePicker = new DatePicker("Date");
    private final TextArea notesField = new TextArea("Notes");

    private final Button saveButton = new Button("Save");
    private final Button cancelButton = new Button("Cancel");
    private final Span symbolSuffix = new Span();

    @Autowired
    public EditTransactionDialog(CryptoTransaction transaction,
                                 InstrumentsFacadeService instrumentsFacadeService) {
        this.transaction = transaction;
        this.instrumentsFacadeService = instrumentsFacadeService;
        this.assetSymbolField = new AssetComboBox(instrumentsFacadeService);

        buildForm();
    }

    private void buildForm() {
        setHeaderTitle("Transaction");
        initializeFields();
        initializeBinder();

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
            CryptoTransaction savedTransaction = instrumentsFacadeService.saveTransaction(binder.getBean());
            this.close();
            System.out.printf("Saved -> %s\n", savedTransaction);
        });

        cancelButton.addClickShortcut(Key.ESCAPE);
        cancelButton.addClickListener(e -> this.close());

        getFooter().add(saveButton, cancelButton);
    }

    private void initializeFields() {
        assetSymbolField.addValueChangeListener(l -> marketPriceField.setValue(assetSymbolField.getMarketPrice()));

        typeField.setLabel("Transaction Type");
        typeField.setItems(CryptoTransaction.TransactionType.values());

        // Initialize with values
        assetSymbolField.setValue(transaction.getAsset());
        typeField.setValue(transaction.getType());
        amountField.setValue(transaction.getOrderQuantity());
        totalCostField.setValue(transaction.getOrderTotalCost());
        marketPriceField.setValue(transaction.getMarketPrice());
        datePicker.setValue(transaction.getDate());
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

        binder.forField(totalCostField)
                .asRequired("Please fill this field")
                .withConverter(new FlexiblePriceConvertor())
                .withValidator(price -> price >= 1, "Total price should be at least one dollar")
                .bind(CryptoTransaction::getOrderTotalCost, CryptoTransaction::setOrderTotalCost);

        binder.forField(notesField)
                .bind(CryptoTransaction::getNotes, CryptoTransaction::setNotes);

        binder.forField(datePicker)
                .bind(CryptoTransaction::getDate, CryptoTransaction::setDate);
    }

    public void addSaveListener(Consumer<?> listener) {
        saveButton.addClickListener(e -> {
            if (binder.validate().isOk()) {
                listener.accept(null);
            }
        });
    }

    public void addCancelListener(Consumer<?> listener) {
        cancelButton.addClickListener(e -> listener.accept(null));
    }

    public CryptoTransaction getTransaction() {
        return transaction;
    }

}
