package com.example.application.views.components.fields;

import com.example.application.utils.common.StringUtils;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.textfield.TextField;
import org.vaadin.textfieldformatter.NumeralFieldFormatter;

import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyField extends TextField {

    public CurrencyField() {
        this(null);
    }

    public CurrencyField(String label) {
        super(label);
        setAllowedCharPattern("[0-9.]");
        setPrefixComponent(new Paragraph("$"));
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

    public void setFormatter(NumeralFieldFormatter formatter) {
        formatter.extend(this);
    }

    public void setValue(double value) {
        super.setValue(parse(value));
    }

    public void setFormatedValue(double value) {
        String formatedValue = StringUtils.stripTrailingZeroes(parse(value));
        super.setValue(formatedValue);
    }

    private String parse(double value) {
        if (value == 0.0)
            return "0";

        if (value > 1.00) {
            return String.format("%.2f", value);
        }

        int decimalPlaces = (int) Math.abs(Math.floor(Math.log10(value))) + 3;
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
        numberFormat.setGroupingUsed(true);
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(decimalPlaces);

        return numberFormat.format(value);
    }

}
