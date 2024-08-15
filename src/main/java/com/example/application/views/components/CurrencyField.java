package com.example.application.views.components;

import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.textfield.TextField;
import org.vaadin.textfieldformatter.NumeralFieldFormatter;

public class CurrencyField extends TextField {

    public CurrencyField(String label) {
        super(label);
        setAllowedCharPattern("[0-9.]");
        setPrefixComponent(new Paragraph("$"));

        NumeralFieldFormatter formatter = new NumeralFieldFormatter.Builder()
                .delimiter(",")
                .decimalMark(".")
                .decimalScale(3)
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
        this.setValue(String.valueOf(value));
    }

}
