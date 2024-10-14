package com.example.application.views.components.fields;

import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.textfield.TextField;
import org.vaadin.textfieldformatter.NumeralFieldFormatter;

public class PercentageField extends TextField {

    /**
     * Pattern to allow any decimal number between 0 and 100 <br>
     * Examples:
     * <li>0.29
     * <li>32.33
     * <li>89.29
     * */
    private static final String PATTERN = "^(100(?:\\.\\d{1,2})?|([0-9]|[1-9][0-9])(?:\\.\\d{1,2})?)$";

    public PercentageField(String label) {
        super(label);
        setPattern(PATTERN);
        setAllowedCharPattern("[0-9.]");
        setSuffixComponent(new Paragraph("%"));

        NumeralFieldFormatter formatter = new NumeralFieldFormatter.Builder()
                .decimalMark(".")
                .integerScale(3)
                .decimalScale(2)
                .nonNegativeOnly(true)
                .stripLeadingZeroes(true)
                .build();

        formatter.extend(this);
    }

    public double doubleValue() {
        try {
            return Double.parseDouble(this.getValue());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public void setValue(double value) {
        this.setValue(value >= 0 && value <= 100 ? String.valueOf(value) : "0");
    }


}
