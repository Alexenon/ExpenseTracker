package com.example.application.views.components.complex_components.dialogs.transactions;

import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.CryptoTransaction;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.views.components.CurrencyField;
import com.example.application.views.components.native_components.Container;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToDoubleConverter;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

// TODO:
//  - boolean subtractFromGivenAsset
//  - Add slider for percentage buy/transfer
//  - private final CurrencyField quantityField = new CurrencyField("Order Quantity");
public class AddTransactionDialog extends Dialog {

    private final Asset asset;
    private final CryptoTransaction transaction;
    private final InstrumentsFacadeService instrumentsFacadeService;
    private final Binder<CryptoTransaction> binder = new Binder<>(CryptoTransaction.class);

    private final ComboBox<Asset> assetSymbolField = new ComboBox<>("Asset");
    private final Select<CryptoTransaction.TransactionType> typeField = new Select<>();
    private final CurrencyField marketPriceField = new CurrencyField("Market Price");
    private final CurrencyField totalPriceField = new CurrencyField("Order Total Price");
    private final TextArea notesField = new TextArea("Notes");
    private final DatePicker datePicker = new DatePicker("Date");

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
        initializeBinder();
        initializeFields();
        setHeaderTitle("Transaction");

        Container formBody = Container.builder()
                .addComponent(assetSymbolField)
                .addComponent(typeField)
                .addComponent(marketPriceField)
                .addComponent(totalPriceField)
                .addComponent(notesField)
                .addComponent(datePicker)
                .build();
        add(formBody);

        saveButton.addClickShortcut(Key.ENTER);
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        saveButton.addClickListener(e -> {
            CryptoTransaction savedTransaction = instrumentsFacadeService.saveTransaction(binder.getBean());
            this.close();
            System.out.printf("Saved -> %s\n", savedTransaction);
        }); // TODO: HERE

        cancelButton.addClickShortcut(Key.ESCAPE);
        cancelButton.addClickListener(e -> this.close());

        getFooter().add(saveButton, cancelButton);
    }

    private void initializeFields() {
        assetSymbolField.setItems(instrumentsFacadeService.getAllAssets());
        assetSymbolField.setItemLabelGenerator(Asset::getSymbol); // TODO: Icon + Name + Symbol
        assetSymbolField.addValueChangeListener(l -> marketPriceField.setValue(getMarketPriceDefaultValue()));

        typeField.setLabel("Transaction Type");
        typeField.setItems(CryptoTransaction.TransactionType.values());

        // Initializing with default values
        assetSymbolField.setValue(asset);
        typeField.setValue(CryptoTransaction.TransactionType.BUY);
        totalPriceField.setValue(0);
        marketPriceField.setValue(getMarketPriceDefaultValue());
        datePicker.setValue(LocalDate.now());
    }

    private void initializeBinder() {
        binder.setBean(transaction);

        binder.forField(assetSymbolField)
                .asRequired("Please fill this field")
                .bind(CryptoTransaction::getAsset, CryptoTransaction::setAsset);

        binder.forField(typeField)
                .asRequired("Please fill this field")
                .bind(CryptoTransaction::getType, CryptoTransaction::setType);

        binder.forField(marketPriceField)
                .asRequired("Please fill this field")
                .withConverter(new StringToDoubleConverter(0.0, "Couldn't convert to double"))
                .withValidator(price -> price > 0, "Market price should be bigger than 0")
                .bind(CryptoTransaction::getMarketPrice, CryptoTransaction::setMarketPrice);

        binder.forField(totalPriceField)
                .asRequired("Please fill this field")
                .withConverter(new StringToDoubleConverter(0.0, "Couldn't convert to double"))
                .withValidator(price -> price > 0, "Market price should be bigger than 0")
                .bind(CryptoTransaction::getOrderTotalCost, CryptoTransaction::setOrderTotalCost);

        binder.forField(notesField)
                .bind(CryptoTransaction::getNotes, CryptoTransaction::setNotes);

        binder.forField(datePicker)
                .bind(CryptoTransaction::getDate, CryptoTransaction::setDate);
    }

    private double getMarketPriceDefaultValue() {
        Asset selectedAsset = assetSymbolField.getValue();

        if (selectedAsset == null)
            return 0;

        return instrumentsFacadeService.getAssetPrice(selectedAsset);

    }


}
