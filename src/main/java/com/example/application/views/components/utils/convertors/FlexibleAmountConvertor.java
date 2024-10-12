package com.example.application.views.components.utils.convertors;

import com.example.application.utils.common.MathUtils;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class FlexibleAmountConvertor implements Converter<String, Double> {

    private static final int MAXIMUM_FRACTION_DIGITS = 8;
    private static final int MAXIMUM_INTEGER_DIGITS = 12;

    private final NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);

    public FlexibleAmountConvertor() {
        numberFormat.setGroupingUsed(true); // Enable thousands separators
        numberFormat.setMinimumFractionDigits(0); // Allows the price to skip decimal points if not entered
        numberFormat.setMaximumFractionDigits(MAXIMUM_FRACTION_DIGITS);
        numberFormat.setMaximumIntegerDigits(MAXIMUM_INTEGER_DIGITS);
    }

    @Override
    public Result<Double> convertToModel(String value, ValueContext context) {
        try {
            // Remove non-numeric characters except for decimal point and minus sign
            String sanitizedValue = value.replaceAll("[^\\d.]", "");
            double parsedValue = numberFormat.parse(sanitizedValue).doubleValue();

            if (parsedValue <= 0) {
                return Result.error("Amount must be greater than 0");
            }

            if(sanitizedValue.endsWith(".")) {
                return Result.error("Cannot be converted into a number");
            }

            // Check if the number of decimal places exceeds the allowed threshold
            if (MathUtils.decimalPlacesInNumber(sanitizedValue) > MAXIMUM_FRACTION_DIGITS) {
                return Result.error("Too many decimal places. Allowed maximum: " + MAXIMUM_FRACTION_DIGITS);
            }

            return Result.ok(parsedValue);
        } catch (ParseException | NumberFormatException e) {
            return Result.error("Invalid amount format");
        }
    }

    @Override
    public String convertToPresentation(Double value, ValueContext context) {
        if (value == null) {
            return "";
        }

        return numberFormat.format(value);
    }

}
