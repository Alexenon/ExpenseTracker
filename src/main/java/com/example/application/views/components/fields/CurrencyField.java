package com.example.application.views.components.fields;

import com.example.application.utils.common.StringUtils;
import com.vaadin.flow.component.html.Paragraph;

public class CurrencyField extends AbstractNumberTextField {

    public CurrencyField() {
        this(null);
    }

    public CurrencyField(String label) {
        this(label, true);
    }

    public CurrencyField(String label, boolean formatable) {
        super(label, formatable);
        numberFormat.setMinimumFractionDigits(2);

        setAllowedCharPattern("[0-9.]");
        setPrefixComponent(new Paragraph("$"));
        setFormatable(formatable);
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


}
