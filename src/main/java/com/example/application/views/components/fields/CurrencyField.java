package com.example.application.views.components.fields;

import com.example.application.data.models.NumberType;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.textfield.TextField;
import org.vaadin.textfieldformatter.NumeralFieldFormatter;

public class CurrencyField extends TextField {

    public CurrencyField() {
        this(null);
    }

    public CurrencyField(String label) {
        super(label);
        setAllowedCharPattern("[0-9.]");
        setPrefixComponent(new Paragraph("$"));
    }

    public static double doubleValue(String s) {
        try {
            return Double.parseDouble(s.replace(",", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public double doubleValue() {
        try {
            String value = this.getValue();

            if(value == null || value.isEmpty())
                return 0;

            return Double.parseDouble(value.replaceAll(",", ""));
        } catch (NumberFormatException | NullPointerException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void setValue(double value) {
        this.setValue(NumberType.PRICE.parse(value).substring(1));
    }

    public void setFormatter(NumeralFieldFormatter formatter) {
        formatter.extend(this);
    }

}
