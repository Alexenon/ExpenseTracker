package com.example.application.views.components;

import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.textfield.TextField;
import org.vaadin.textfieldformatter.NumeralFieldFormatter;

public class PercentageField extends TextField {

    public PercentageField(String label) {
        super(label);
        setAllowedCharPattern("[0-9.]");
        setSuffixComponent(new Paragraph("%"));

        NumeralFieldFormatter formatter = new NumeralFieldFormatter.Builder()
                .decimalMark(".")
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
