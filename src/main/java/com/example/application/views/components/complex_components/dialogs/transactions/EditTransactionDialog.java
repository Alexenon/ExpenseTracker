package com.example.application.views.components.complex_components.dialogs.transactions;

import com.example.application.data.models.InstrumentsProvider;
import com.example.application.data.models.crypto.AssetData;
import com.example.application.data.models.crypto.CryptoTransaction;
import com.example.application.entities.crypto.Asset;
import com.example.application.services.crypto.InstrumentsService;
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
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.data.converter.StringToDoubleConverter;
import org.springframework.beans.factory.annotation.Autowired;

public class EditTransactionDialog extends Dialog {

    private final AssetData assetData;
    private final InstrumentsProvider instrumentsProvider;
    private final InstrumentsService instrumentsService;
    private final Binder<CryptoTransaction> binder = new Binder<>(CryptoTransaction.class);

    private final ComboBox<AssetData> assetSymbolField = new ComboBox<>("Asset");
    private final Select<CryptoTransaction.TransactionType> typeField = new Select<>();
    private final CurrencyField marketPriceField = new CurrencyField("Market Price");
    private final CurrencyField totalPriceField = new CurrencyField("Order Total Price");
    private final TextArea notesField = new TextArea("Notes");
    private final DatePicker datePicker = new DatePicker("Date");

    private final Button saveButton = new Button("Save");
    private final Button cancelButton = new Button("Cancel");

    private CryptoTransaction transaction;

    @Autowired
    public EditTransactionDialog(AssetData assetData,
                                 CryptoTransaction transaction,
                                 InstrumentsService instrumentsService,
                                 InstrumentsProvider instrumentsProvider) {
        this.assetData = assetData;
        this.transaction = transaction;
        this.instrumentsService = instrumentsService;
        this.instrumentsProvider = instrumentsProvider;
        buildForm();
    }

    private void buildForm() {
        initializeFields();
        initializeBinder();
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
            transaction = binder.getBean();
            instrumentsService.saveTransaction(transaction);
            TransactionDetailsDialog detailsDialog = new TransactionDetailsDialog(transaction, instrumentsService, instrumentsProvider);
            detailsDialog.open();
            this.close();
        });

        cancelButton.addClickShortcut(Key.ESCAPE);
        cancelButton.addClickListener(e -> this.close());

        getFooter().add(saveButton, cancelButton);
    }

    private void initializeFields() {
        assetSymbolField.setItems(instrumentsProvider.getListOfAssetData());
        assetSymbolField.setItemLabelGenerator(AssetData::getSymbol); // TODO: Icon + Name + Symbol
        assetSymbolField.addValueChangeListener(l -> marketPriceField.setValue(getMarketPriceDefaultValue()));

        typeField.setLabel("Transaction Type");
        typeField.setItems(CryptoTransaction.TransactionType.values());

        // Initializing with default values
        assetSymbolField.setValue(assetData);
        typeField.setValue(transaction.getType());
        totalPriceField.setValue(transaction.getOrderTotalCost());
        marketPriceField.setValue(transaction.getMarketPrice());
        datePicker.setValue(transaction.getDate());
    }

    private void initializeBinder() {
        binder.setBean(transaction);

        binder.forField(assetSymbolField)
                .asRequired("Please fill this field")
                .withConverter(new Converter<AssetData, Asset>() {
                    @Override
                    public Result<Asset> convertToModel(AssetData assetData, ValueContext valueContext) {
                        return Result.ok(assetData.getAsset());
                    }

                    @Override
                    public AssetData convertToPresentation(Asset asset, ValueContext valueContext) {
                        if (asset == null)
                            return null;

                        AssetData assetData = instrumentsProvider.getAssetDataBySymbol(asset.getSymbol());
                        System.out.println(assetData);
                        return assetData;
                    }
                })
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
        return assetSymbolField.getValue() != null ? assetSymbolField.getValue().getPrice() : 0;
    }


}
