package com.example.application.views.components.fields;

import com.vaadin.flow.component.textfield.TextField;

public class AmountField extends TextField {

    /**
     * Pattern any number bigger than 0
     */
    private static final String PATTERN = "^(?!0+$)\\d*\\.?\\d+$";

    public AmountField() {
        this(null);
    }

    public AmountField(String label) {
        super(label);
        setPattern(PATTERN);
        setAllowedCharPattern("[0-9.]");

//        NumeralFieldFormatter formatter = new NumeralFieldFormatter.Builder()
//                .decimalMark(".")
//                .integerScale(3)
//                .decimalScale(6)
//                .nonNegativeOnly(true)
//                .stripLeadingZeroes(true)
//                .build();
//
//        formatter.extend(this);
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

    public void setValue(double value) {
        System.out.println("SETTING AMOUNT " + value);
        this.setValue(String.valueOf(value));
    }

}
