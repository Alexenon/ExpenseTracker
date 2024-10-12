package com.example.application.views.components.fields;

import com.example.application.utils.common.StringUtils;
import com.vaadin.flow.component.textfield.TextField;
import org.vaadin.textfieldformatter.NumeralFieldFormatter;

import java.text.NumberFormat;
import java.util.Locale;

public class AmountField extends TextField {

    /**
     * Pattern any number bigger than 0
     */
    private static final String PATTERN = "^(?!0+$)\\d*\\.?\\d+$";

    private final NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);

    public AmountField() {
        this(null);
    }

    public AmountField(String label) {
        super(label);
        setPattern(PATTERN);
        setAllowedCharPattern("[0-9.]");
        numberFormat.setGroupingUsed(true);
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

    public void setValue(double value) {
        System.out.println("SETTING AMOUNT " + value);
        super.setValue(parse(value));
    }

    public void setFormatedValue(double value) {
        String formatedValue = StringUtils.stripTrailingZeroes(parse(value));
        super.setValue(formatedValue);
    }

    public void setFormatter(NumeralFieldFormatter formatter) {
        formatter.extend(this);
    }

}
