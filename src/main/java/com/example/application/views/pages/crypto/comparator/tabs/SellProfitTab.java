package com.example.application.views.pages.crypto.comparator.tabs;

import com.example.application.data.models.NumberType;
import com.example.application.entities.crypto.Asset;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.utils.common.MathUtils;
import com.example.application.views.components.fields.AmountField;
import com.example.application.views.components.fields.CurrencyField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SellProfitTab extends BaseCompareTab {

    private final ComboBox<Asset> assetSymbolField = new ComboBox<>("Asset");
    private final AmountField amountField = new AmountField("Amount");
    private final CurrencyField buyPriceField = new CurrencyField("Buy Price");
    private final CurrencyField totalPriceField = new CurrencyField("Total");
    private final CurrencyField sellPriceField = new CurrencyField("Sell Price");
    private final Binder<?> binder = new Binder<>();

    @Autowired
    public SellProfitTab(InstrumentsFacadeService instrumentsFacadeService) {
        super("Sell profit calculator", instrumentsFacadeService);
        buildForm();
    }

    private void buildForm() {
        initializeFieldsValues();
        initializeFieldsListeners();
    }

    private void initializeFieldsValues() {
        assetSymbolField.setItems(instrumentsFacadeService.getAllAssets());
        assetSymbolField.setItemLabelGenerator(instrumentsFacadeService::getAssetFullName);
        assetSymbolField.setRenderer(assetSymbolRenderer());
        assetSymbolField.addValueChangeListener(l -> buyPriceField.setValue(assetMarketPrice));

        amountField.setValue("");
        buyPriceField.setValue(assetMarketPrice);
        totalPriceField.setValue(0);
        sellPriceField.setValue("");
    }

    private void initializeFieldsListeners() {
        assetSymbolField.addValueChangeListener(l -> {
            binder.setValidatorsDisabled(false);
            buyPriceField.setValue(assetMarketPrice);
            amountField.setSuffixComponent(new Span(getSelectedAssetSymbol(assetSymbolField)));
        });

        amountField.setValueChangeMode(ValueChangeMode.EAGER);
        amountField.addKeyUpListener(e -> {
            double totalPrice = amountField.doubleValue() * buyPriceField.doubleValue();
            totalPriceField.setValue(totalPrice);
            binder.validate();
        });

        buyPriceField.setValueChangeMode(ValueChangeMode.EAGER);
        buyPriceField.addKeyUpListener(e -> {
            double totalPrice = amountField.doubleValue() * buyPriceField.doubleValue();
            totalPriceField.setValue(totalPrice);
            binder.validate();
        });

        totalPriceField.setValueChangeMode(ValueChangeMode.EAGER);
        totalPriceField.addKeyUpListener(e -> {
            double amountValue = MathUtils.safeZeroDivision(totalPriceField.doubleValue(), buyPriceField.doubleValue());
            amountField.setValue(amountValue);
            binder.validate();
        });
    }

    @Override
    protected Div createInputFieldsContainer() {
        return new Div(assetSymbolField, amountField, buyPriceField, totalPriceField, sellPriceField);
    }

    @Override
    protected Button createDisplayResultsBtn() {
        Button calculateBtn = new Button("Calculate");
        calculateBtn.addClickListener(e -> {
            double profit = MathUtils.profit(buyPriceField.doubleValue(), sellPriceField.doubleValue(), totalPriceField.doubleValue());
            double profitPercentage = MathUtils.profitPercentage(buyPriceField.doubleValue(), sellPriceField.doubleValue());

            String text = """
                    Invested in %s $%.0f
                    Buy Price: %s
                    Sell Price: %s
                    Profit: %s ~ %.1f%%
                    """.formatted(getSelectedAssetSymbol(assetSymbolField), totalPriceField.doubleValue(),
                    NumberType.PRICE.parse(buyPriceField.doubleValue()),
                    NumberType.PRICE.parse(sellPriceField.doubleValue()),
                    NumberType.PRICE.parse(profit), profitPercentage
            );

        });

        return calculateBtn;
    }

}
