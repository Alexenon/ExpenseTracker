package com.example.application.views.components.utils.convertors;

import com.example.application.utils.common.MathUtils;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class FlexiblePriceConvertor implements Converter<String, Double> {

    private static final int MAXIMUM_INTEGER_DIGITS = 12;

    private final NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);

    public FlexiblePriceConvertor() {
        numberFormat.setGroupingUsed(true); // Enable thousands separators
        numberFormat.setMinimumFractionDigits(0); // Allows the price to skip decimal points if not entered
        numberFormat.setMaximumIntegerDigits(MAXIMUM_INTEGER_DIGITS);
    }

    @Override
    public Result<Double> convertToModel(String value, ValueContext context) {
        try {
            // Remove non-numeric characters except for decimal point and minus sign
            String sanitizedValue = value.replaceAll("[^\\d.-]", "");
            double parsedValue = numberFormat.parse(sanitizedValue).doubleValue();

            if (parsedValue <= 0) {
                return Result.error("Price must be greater than 0");
            }

            if (sanitizedValue.endsWith(".")) {
                return Result.error("Cannot be converted into a number");
            }

            // Check if the number of integer places exceeds the allowed threshold
            if (MathUtils.integerPlacesInNumber(sanitizedValue) > MAXIMUM_INTEGER_DIGITS) {
                return Result.error("Too many digits before comma. Allowed maximum: " + MAXIMUM_INTEGER_DIGITS);
            }

            // Check if the number of decimal places exceeds the allowed threshold
            int maxFractionDigits = getMaxAllowedFractionDigits(parsedValue);
            if (MathUtils.decimalPlacesInNumber(sanitizedValue) > maxFractionDigits) {
                return Result.error("Too many decimal places. Allowed maximum: " + maxFractionDigits);
            }

            return Result.ok(parsedValue);
        } catch (ParseException | NumberFormatException e) {
            return Result.error("Invalid price format");
        }
    }

    @Override
    public String convertToPresentation(Double value, ValueContext context) {
        if (value == null) {
            return "";
        }

        int maxFractionDigits = getMaxAllowedFractionDigits(value);
        numberFormat.setMaximumFractionDigits(maxFractionDigits);


        System.out.println("Converting " + value + " -> " + numberFormat.format(value));

        return numberFormat.format(value);
    }

    /**
     * Method to get maximum allowed fraction digits based on the value
     */
    private int getMaxAllowedFractionDigits(double value) {
        if (value > 0.5 && value < 1) {
            return 3;
        } else if (value > 0.01 && value <= 0.5) {
            return 4;
        } else if (value > 0.001 && value <= 0.01) {
            return 5;
        } else if (value > 0.0001 && value <= 0.001) {
            return 6;
        } else if (value > 0.00001 && value <= 0.0001) {
            return 7;
        } else if (value > 0.000001 && value <= 0.00001) {
            return 8;
        } else if (value <= 0.000001) {
            return 9;
        }

        return 2;
    }

}
