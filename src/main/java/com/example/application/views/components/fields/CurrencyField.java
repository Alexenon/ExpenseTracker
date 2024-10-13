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

    @Override
    public void setValue(double value) {
        super.setValue(parse(value));
    }

    @Override
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
        numberFormat.setMaximumFractionDigits(decimalPlaces);

        return numberFormat.format(value);
    }

}
