package com.example.application.views.components.custom.dialogs.transactions.export;

import com.example.application.data.dtos.migration.TransactionModel;
import com.example.application.entities.common.TransactionType;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.views.components.core.Container;
import com.example.application.views.components.custom.fields.AmountField;
import com.example.application.views.components.custom.fields.AssetComboBox;
import com.example.application.views.components.custom.fields.CurrencyField;
import com.example.application.views.components.utils.HasNotifications;
import com.example.application.views.components.utils.convertors.FlexibleAmountConvertor;
import com.example.application.views.components.utils.convertors.FlexiblePriceConvertor;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.DoubleRangeValidator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.function.Consumer;

public class EditTransactionModelDialog extends Dialog implements HasNotifications {

    private final TransactionModel transaction;
    private final InstrumentsFacadeService instrumentsFacadeService;
    private final Binder<TransactionModel> binder = new Binder<>(TransactionModel.class);

    private final AssetComboBox assetSymbolField;
    private final Select<TransactionType> typeField = new Select<>();
    private final AmountField amountField = new AmountField("Amount");
    private final CurrencyField marketPriceField = new CurrencyField("Price");
    private final DateTimePicker dateTimePicker = new DateTimePicker("Date");
    private final TextArea notesField = new TextArea("Notes");

    private final Button saveButton = new Button("Save");
    private final Button cancelButton = new Button("Cancel");
    private final Span symbolSuffix = new Span();

    @Autowired
    public EditTransactionModelDialog(TransactionModel transaction, InstrumentsFacadeService instrumentsFacadeService) {
        this.transaction = transaction;
        this.instrumentsFacadeService = instrumentsFacadeService;
        this.assetSymbolField = new AssetComboBox(instrumentsFacadeService);

        buildForm();
    }

    private void buildForm() {
        setHeaderTitle("Transaction");
        initializeFields();
        initializeFieldsValues();
        initializeBinder();

        Container formBody = Container.builder("transaction-modal")
                .addComponent(assetSymbolField)
                .addComponent(typeField)
                .addComponent(amountField)
                .addComponent(marketPriceField)
                .addComponent(notesField)
                .addComponent(dateTimePicker)
                .build();
        add(formBody);

        assetSymbolField.addValueChangeListener(l -> {
            binder.setValidatorsDisabled(false);
            marketPriceField.setValue(assetSymbolField.getMarketPrice());
            symbolSuffix.setText(assetSymbolField.getSymbol());
        });

        amountField.setSuffixComponent(symbolSuffix);

        saveButton.addClickShortcut(Key.ENTER);
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);

        cancelButton.addClickShortcut(Key.ESCAPE);
        cancelButton.addClickListener(e -> {
            initializeFieldsValues();
            this.close();
        });

        getFooter().add(saveButton, cancelButton);
    }

    private void initializeFields() {
        assetSymbolField.addValueChangeListener(l -> marketPriceField.setValue(assetSymbolField.getMarketPrice()));
        typeField.setLabel("Transaction Type");
        typeField.setItems(TransactionType.values());
    }

    private void initializeFieldsValues() {
        assetSymbolField.setValue(transaction.getSymbol());
        typeField.setValue(transaction.getType());
        amountField.setValue(transaction.getAmount());
        marketPriceField.setValue(transaction.getPrice());
        dateTimePicker.setValue(transaction.getDateTime());
    }

    private void initializeBinder() {
        binder.setBean(transaction);

        binder.forField(assetSymbolField)
                .asRequired("Please fill this field")
                .withConverter(
                        asset -> asset == null ? null : asset.getSymbol(),
                        symbol -> symbol == null ? null : instrumentsFacadeService.getAssetBySymbol(symbol).orElseThrow(),
                        "Invalid asset symbol"
                )
                .bind(TransactionModel::getSymbol, TransactionModel::setSymbol);

        binder.forField(typeField)
                .asRequired("Please fill this field")
                .bind(TransactionModel::getType, TransactionModel::setType);

        binder.forField(amountField)
                .asRequired("Please fill this field")
                .withConverter(new FlexibleAmountConvertor())
                .withValidator(new DoubleRangeValidator("Invalid decimal value", (double) 0, Double.MAX_VALUE))
                .withValidator(amount -> amount > 0, "Amount should be bigger than 0")
                .bind(TransactionModel::getAmount, TransactionModel::setAmount);

        binder.forField(marketPriceField)
                .asRequired("Please fill this field")
                .withConverter(new FlexiblePriceConvertor())
                .withValidator(new DoubleRangeValidator("Invalid decimal value", (double) 0, Double.MAX_VALUE))
                .withValidator(amount -> amount > 0, "Market price should be bigger than 0")
                .bind(TransactionModel::getPrice, TransactionModel::setPrice);

        binder.forField(notesField)
                .bind(TransactionModel::getNote, TransactionModel::setNote);

        binder.forField(dateTimePicker)
                .bind(TransactionModel::getDateTime, TransactionModel::setDateTime);
    }

    public void addSaveListener(Consumer<?> listener) {
        saveButton.addClickListener(e -> handleTransactionSave(listener));
    }

    private void handleTransactionSave(Consumer<?> listener) {
        if (binder.validate().isOk()) {
            saveTransaction(listener);
        } else {
            showErrorNotification("Error! Please fill the fields correctly");
        }
    }

    private void saveTransaction(Consumer<?> listener) {
        try {
            showSuccessfulNotification("The transaction was updated succesfully");
            this.close();
            listener.accept(null);
        } catch (Exception e) {
            showErrorNotification(e.getLocalizedMessage());
        }
    }

    public TransactionModel getTransaction() {
        return binder.getBean();
    }

}
