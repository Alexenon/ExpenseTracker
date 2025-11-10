package com.example.application.views.components.custom.dialogs.transactions;

import com.example.application.entities.common.TransactionType;
import com.example.application.entities.crypto.Transaction;
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
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.DoubleRangeValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;
import java.util.function.Consumer;

public class EditTransactionDialog extends Dialog implements HasNotifications {

    private final Transaction transaction;
    private final Transaction initialTransaction;
    private final InstrumentsFacadeService instrumentsFacadeService;
    private final Binder<Transaction> binder = new Binder<>(Transaction.class);

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
    public EditTransactionDialog(Transaction transaction, InstrumentsFacadeService instrumentsFacadeService) {
        this.transaction = transaction;
        this.initialTransaction = new Transaction(transaction);
        this.instrumentsFacadeService = instrumentsFacadeService;
        this.assetSymbolField = new AssetComboBox(instrumentsFacadeService);

        buildForm();
    }

    private void buildForm() {
        setHeaderTitle("Edit Transaction");
        initializeFields();
        initializeFieldsValues();
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
		displayHintMarketPrice();
		displayHintAmountOfTokens();
    }

    private void initializeFieldsValues() {
        assetSymbolField.setValue(initialTransaction.getAsset());
        typeField.setValue(initialTransaction.getType());
        amountField.setValue(initialTransaction.getOrderQuantity());
        totalCostField.setValue(initialTransaction.getOrderTotalCost());
        marketPriceField.setValue(initialTransaction.getMarketPrice());
        datePicker.setValue(initialTransaction.getDateTime());
    }

    private void initializeFieldListeners() {
        assetSymbolField.addValueChangeListener(l -> {
            binder.setValidatorsDisabled(false);
            marketPriceField.setValue(assetSymbolField.getMarketPrice());
            symbolSuffix.setText(assetSymbolField.getSymbol());
            displayHintMarketPrice();
			displayHintAmountOfTokens();
        });

		typeField.addValueChangeListener(e -> displayHintAmountOfTokens());

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

        cancelButton.addClickShortcut(Key.ESCAPE);
        cancelButton.addClickListener(e -> {
            initializeFieldsValues();
            this.close();
        });
    }

    private void initializeBinder() {
        binder.setBean(transaction);

        binder.forField(assetSymbolField)
                .asRequired("Please fill this field")
                .bind(Transaction::getAsset, Transaction::setAsset);

        binder.forField(typeField)
                .asRequired("Please fill this field")
                .bind(Transaction::getType, Transaction::setType);

        binder.forField(amountField)
                .asRequired("Please fill this field")
                .withConverter(new FlexibleAmountConvertor())
                .withValidator(new DoubleRangeValidator("Invalid decimal value", 0.0, Double.MAX_VALUE))
                .withValidator(amount -> amount > 0, "Amount should be bigger than 0")
                .bind(Transaction::getOrderQuantity, Transaction::setOrderQuantity);

        binder.forField(marketPriceField)
                .asRequired("Please fill this field")
                .withConverter(new FlexiblePriceConvertor())
                .withValidator(new DoubleRangeValidator("Invalid decimal value", 0.0, Double.MAX_VALUE))
                .withValidator(amount -> amount > 0, "Market price should be bigger than 0")
                .bind(Transaction::getMarketPrice, Transaction::setMarketPrice);

        binder.forField(totalCostField)
                .asRequired("Please fill this field")
                .withConverter(new FlexiblePriceConvertor())
                .withValidator(new DoubleRangeValidator("Invalid decimal value", 0.0, Double.MAX_VALUE))
                .withValidator(price -> price >= 1, "Total price should be at least one dollar")
                .bind(Transaction::getOrderTotalCost, Transaction::setOrderTotalCost);

        binder.forField(notesField)
                .bind(Transaction::getNote, Transaction::setNote);

        binder.forField(datePicker)
                .bind(Transaction::getDateTime, Transaction::setDateTime);
    }

    public void addSaveListener(Consumer<?> listener) {
        saveButton.addClickListener(e -> handleTransactionSave(listener));
    }

    private void handleTransactionSave(Consumer<?> listener) {
        if (!binder.validate().isOk()) {
            showErrorNotification("Error! Please fill the fields correctly");
            return;
        }

        if (hasAssetChanged()) {
            ConfirmDialog confirmDialog = new ConfirmDialog();
            confirmDialog.setHeader("Warning: Changing asset for completed transaction");
            confirmDialog.setText("Modifying the asset for a completed transaction may cause calculation errors and compromise record accuracy. Proceed with this change?");
            confirmDialog.setConfirmText("Save");
            confirmDialog.setCancelable(true);
            confirmDialog.addConfirmListener(l -> saveTransaction(listener));
            confirmDialog.open();
        } else {
            saveTransaction(listener);
        }
    }

    private boolean hasAssetChanged() {
        return !Objects.equals(initialTransaction.getAsset(), binder.getBean().getAsset());
    }

    private void saveTransaction(Consumer<?> listener) {
        try {
            instrumentsFacadeService.saveTransaction(binder.getBean());
            showSuccessfulNotification("The transaction was saved succesfully");
            this.close();
            listener.accept(null);
        } catch (Exception e) {
            showErrorNotification(e.getLocalizedMessage());
        }
    }

    public Transaction getTransaction() {
        return binder.getBean();
    }

	private void displayHintMarketPrice() {
		String formatedPrice = CommonFormatters.CURRENCY.format(assetSymbolField.getMarketPrice());
		marketPriceField.setHelperText("Current price: %s".formatted(formatedPrice));
	}

	private void displayHintAmountOfTokens() {
		double amountTokens = assetSymbolField.getAmountTokens(transaction.getPortfolio());
		String formatedAmount = CommonFormatters.AMOUNT.format(amountTokens);
		String helperText = typeField.getValue().isBuyTransaction()
				? null
				: "Currently you have %s %s".formatted(formatedAmount, assetSymbolField.getSymbol());

		amountField.setHelperText(helperText);
	}

}