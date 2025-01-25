package com.example.application.views.components.custom.fields.input;

import com.example.application.entities.crypto.Asset;
import com.example.application.utils.common.StringUtils;
import com.vaadin.flow.component.select.Select;

import java.util.List;

public class PriceField extends AbstractNumberTextField {

    private final Select<Asset> paymentSelector = new Select<>();

    public PriceField() {
        this(null);
    }

    public PriceField(String label) {
        this(label, true);
    }

    public PriceField(String label, boolean formatable) {
        super(label, formatable);
        addClassName("price-field");

        setFormatable(formatable);
        setAllowedCharPattern("[0-9.]");
        numberFormat.setMinimumFractionDigits(2);

        setSuffixComponent(paymentSelector);
        paymentSelector.setItemLabelGenerator(Asset::getSymbol);
    }

    @Override
    public void setValue(double value) {
        String parsedValue = parse(value);
        String formatedValue = StringUtils.stripTrailingZeroes(parsedValue);
        super.setValue(formatedValue);
    }

    private String parse(double value) {
        if (value == 0.0)
            return "0";

        numberFormat.setMaximumFractionDigits(maxDecimalPlaces(value));
        return numberFormat.format(value);
    }

    private int maxDecimalPlaces(double value) {
        if (value >= 1) {
            return 2;
        } else if (value >= 0.1) {
            return 3;
        } else if (value >= 0.01) {
            return 4;
        } else if (value >= 0.001) {
            return 5;
        } else if (value >= 0.0001) {
            return 6;
        } else if (value >= 0.00001) {
            return 7;
        }
        return 8;
    }

    public Select<Asset> getPaymentSelector() {
        return paymentSelector;
    }

    public void setPaymentItems(List<Asset> assetList) {
        paymentSelector.setItems(assetList);
        paymentSelector.setValue(assetList.getFirst());
    }

}
