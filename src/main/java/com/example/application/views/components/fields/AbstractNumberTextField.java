package com.example.application.views.components.fields;

import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.textfield.TextField;
import org.vaadin.textfieldformatter.NumeralFieldFormatter;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public abstract class AbstractNumberTextField extends TextField {

    protected NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
    protected NumeralFieldFormatter formatter;

    public AbstractNumberTextField(String label, boolean formatable) {
        super(label);
        this.numberFormat.setGroupingUsed(true);
        this.numberFormat.setParseIntegerOnly(false);
        this.numberFormat.setMaximumIntegerDigits(12);

        this.formatter = new NumeralFieldFormatter.Builder()
                .delimiter(",")
                .decimalMark(".")
                .decimalScale(6)
                .nonNegativeOnly(true)
                .stripLeadingZeroes(true)
                .build();

        setAllowedCharPattern("[0-9.]");
        setPrefixComponent(new Paragraph("$"));
        setFormatable(formatable);
    }

    public void setFormatter(NumeralFieldFormatter formatter) {
        this.formatter = formatter;
        setFormatable(formatter != null);
    }

    public void setFormatable(boolean formatable) {
        if (formatable) {
            formatter.extend(this);
        } else {
            formatter.remove();
        }
    }

    public NumberFormat getNumberFormat() {
        return numberFormat;
    }

    public void setNumberFormat(NumberFormat numberFormat) {
        this.numberFormat = numberFormat;
    }

    public double doubleValue() {
        try {
            String value = this.getValue();

            if (value == null || value.isEmpty())
                return 0;

            return numberFormat.parse(value).doubleValue();
        } catch (ParseException e) {
            return 0;
        }
    }

    public abstract void setValue(double value);

}
