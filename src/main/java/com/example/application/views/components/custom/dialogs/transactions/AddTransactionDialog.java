package com.example.application.views.components.custom.dialogs.transactions;

import com.example.application.entities.common.TransactionType;
import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.CryptoTransaction;
import com.example.application.entities.crypto.Portfolio;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.utils.common.formatters.CommonFormatters;
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
import com.vaadin.flow.data.value.ValueChangeMode;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.function.Consumer;

// TODO: [LONG TERM]
//  - Add slider for percentage buy/transfer
//  - Ideally should be revisted this form design, with a more complex solution -> sliders, % and $, deposit...
public class AddTransactionDialog extends Dialog implements HasNotifications {

    private final Portfolio portfolio;
    private final CryptoTransaction transaction;
    private final InstrumentsFacadeService instrumentsFacadeService;
    private final Binder<CryptoTransaction> binder = new Binder<>(CryptoTransaction.class);

    private final AssetComboBox assetSymbolField;
    private final Select<TransactionType> typeField = new Select<>();
    private final AmountField amountField = new AmountField("Amount");
    private final CurrencyField marketPriceField = new CurrencyField("Price");
    private final CurrencyField totalCostField = new CurrencyField("Total");
    private final DateTimePicker datePicker = new DateTimePicker("Date & Time");
    private final TextArea notesField = new TextArea("Notes");

    private final Button saveButton = new Button("Save");
    private final Button cancelButton = new Button("Cancel");
    private final Span symbolSuffix = new Span();

    @Autowired
    public AddTransactionDialog(Portfolio portfolio, InstrumentsFacadeService instrumentsFacadeService) {
        this.portfolio = portfolio;
        this.instrumentsFacadeService = instrumentsFacadeService;
        this.assetSymbolField = new AssetComboBox(instrumentsFacadeService);
        this.transaction = defaultTransaction();
        buildForm();
    }

    private void buildForm() {
        setHeaderTitle("Add Transaction");
        initializeFields();
        initializeFieldValues();
        initializeFieldListeners();
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


        getFooter().add(saveButton, cancelButton);
    }

    private void initializeFields() {
        typeField.setLabel("Transaction Type");
        typeField.setItems(TransactionType.values());
    }

    private void initializeFieldValues() {
        assetSymbolField.setValue("");
        typeField.setValue(TransactionType.BUY);
        amountField.setValue("");
        marketPriceField.setValue(assetSymbolField.getMarketPrice());
        totalCostField.setValue(0);
        datePicker.setValue(LocalDateTime.now());
    }

    private void initializeFieldListeners() {
        assetSymbolField.addValueChangeListener(l -> {
            binder.setValidatorsDisabled(false);
            marketPriceField.setValue(assetSymbolField.getMarketPrice());
            symbolSuffix.setText(assetSymbolField.getSymbol());
            updateFieldHelperTexts();
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
                .withValidator(new DoubleRangeValidator("Invalid decimal value", 0.0, Double.MAX_VALUE))
                .withValidator(amount -> amount > 0, "Amount should be bigger than 0")
                .bind(CryptoTransaction::getOrderQuantity, CryptoTransaction::setOrderQuantity);

        binder.forField(marketPriceField)
                .asRequired("Please fill this field")
                .withConverter(new FlexiblePriceConvertor())
                .withValidator(new DoubleRangeValidator("Invalid decimal value", 0.0, Double.MAX_VALUE))
                .withValidator(price -> price > 0, "Market price should be bigger than 0")
                .bind(CryptoTransaction::getMarketPrice, CryptoTransaction::setMarketPrice);

        binder.forField(totalCostField)
                .asRequired("Please fill this field")
                .withConverter(new FlexiblePriceConvertor())
                .withValidator(new DoubleRangeValidator("Invalid decimal value", 0.0, Double.MAX_VALUE))
                .withValidator(price -> price >= 1, "Total price should be at least one dollar")
                .bind(CryptoTransaction::getOrderTotalCost, CryptoTransaction::setOrderTotalCost);

        binder.forField(notesField)
                .bind(CryptoTransaction::getNotes, CryptoTransaction::setNotes);

        binder.forField(datePicker)
                .bind(CryptoTransaction::getDateTime, CryptoTransaction::setDateTime);
    }

    public void addSaveBtnClickListener(Consumer<?> listener) {
        saveButton.addClickListener(e -> listener.accept(null));
    }

    public void setAsset(Asset asset) {
        assetSymbolField.setValue(asset);
    }

    private void updateFieldHelperTexts() {
        String formatedPrice = CommonFormatters.CURRENCY.format(assetSymbolField.getMarketPrice());
        String formatedAmount = CommonFormatters.AMOUNT.format(assetSymbolField.getAmountTokens(portfolio));
        marketPriceField.setHelperText("Current price: %s".formatted(formatedPrice));
        amountField.setHelperText("Currently you have %s %s".formatted(formatedAmount, assetSymbolField.getSymbol()));
    }

    private CryptoTransaction defaultTransaction() {
        CryptoTransaction newTransaction = new CryptoTransaction();
        newTransaction.setPortfolio(portfolio);
        return newTransaction;
    }

}
