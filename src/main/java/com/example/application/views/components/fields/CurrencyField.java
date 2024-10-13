package com.example.application.views.components.fields;

import com.example.application.utils.common.StringUtils;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.validator.AbstractValidator;

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
        String parsedValue = parse(value);
        String formatedValue = StringUtils.stripTrailingZeroes(parsedValue);
        System.out.println("SET VALUE -> " + parsedValue + " -> " + formatedValue);
        super.setValue(formatedValue);
    }

    //
    // TODO: Currently user doesn't know when the maximal fraction digit is
    //  - Should
    //
    private String parse(double value) {
        if (value == 0.0)
            return "0";

        int maxDecimalPlaces = 2;
        if (value < 1.00) {
            maxDecimalPlaces = (int) Math.abs(Math.floor(Math.log10(value))) + 3;
        }

        numberFormat.setMaximumFractionDigits(maxDecimalPlaces);

        return numberFormat.format(value);
    }

    @Override
    public Validator<String> getDefaultValidator() {
        // TODO: CREATE
        AbstractValidator validator;
        return super.getDefaultValidator();
    }
}
