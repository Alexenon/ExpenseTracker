package com.example.application.views.components.fields;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;
import lombok.SneakyThrows;

import java.text.NumberFormat;

/*
    TODO: Apply this Validator
* */
public class StringFractionalDigitsValidator implements Validator<String> {

    private final NumberFormat numberFormat;

    public StringFractionalDigitsValidator(NumberFormat numberFormat) {
        this.numberFormat = numberFormat;
    }

    @Override
    @SneakyThrows
    public ValidationResult apply(String value, ValueContext context) {
        // Allow empty values, assuming the field itself is not required
        if (value == null || value.isEmpty()) {
            return ValidationResult.ok();
        }

        if (!value.contains(".")) {
            return ValidationResult.ok();
        }

        double doubleValue = numberFormat.parse(value).doubleValue();

        // Split the string by the decimal point and check the part after the decimal
        String[] parts = value.split("\\.");

        System.out.println(parts.length);
        if (parts.length == 0) {
            return ValidationResult.ok();
        }

        double decimalPlaces = parts[1].length();
        int maxDecimalPlaces = maxDecimalPlaces(doubleValue);

        System.out.println("--> " + parts[1] + " " + decimalPlaces + " " + maxDecimalPlaces);

        if (decimalPlaces > maxDecimalPlaces) {
            return ValidationResult.error("The number cannot have more than " + maxDecimalPlaces + " decimal places");
        }

        return ValidationResult.ok();
    }

    private int maxDecimalPlaces(double value) {
        if (value >= 1) {
            return 2;
        } else if (value >= 0.1) {
            return 3;
        } else if (value >= 0.01) {
            return 4;
        } else if (value >= 0.001) {
            return 5;
        } else if (value >= 0.0001) {
            return 6;
        } else if (value >= 0.00001) {
            return 7;
        }
        return 8;
    }
}
