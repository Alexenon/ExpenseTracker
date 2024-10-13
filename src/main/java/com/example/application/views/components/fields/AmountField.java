package com.example.application.views.components.fields;

import com.example.application.utils.common.StringUtils;

public class AmountField extends AbstractNumberTextField {

    public AmountField() {
        this(null);
    }

    public AmountField(String label) {
        this(label, true);
    }

    public AmountField(String label, boolean formatable) {
        super(label, formatable);
        setAllowedCharPattern("[0-9.]");
        numberFormat.setMinimumFractionDigits(0);
    }

    public double doubleValue() {
        try {
            String value = this.getValue();

            if (value == null || value.isEmpty())
                return 0;

            return Double.parseDouble(value.replaceAll(",", ""));
        } catch (NumberFormatException | NullPointerException e) {
            e.printStackTrace();
            return 0;
        }
    }

    //
    // TODO: THIS IS NOT USED....
    //
    public void setValue(double value) {
        super.setValue(parse(value));
    }

    public void setFormatedValue(double value) {
        String formatedValue = StringUtils.stripTrailingZeroes(parse(value));
        super.setValue(formatedValue);
    }

    private String parse(double value) {
        if (value >= 1000) {
            numberFormat.setMaximumFractionDigits(2);
        } else if (value >= 1) {
            numberFormat.setMaximumFractionDigits(4);
        } else if (value >= 0.001) {
            numberFormat.setMaximumFractionDigits(6);
        } else {
            numberFormat.setMaximumFractionDigits(8);
        }

        return numberFormat.format(value);
    }

}
