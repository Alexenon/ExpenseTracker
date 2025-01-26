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
        numberFormat.setGroupingUsed(true);
        numberFormat.setMinimumFractionDigits(0);
        numberFormat.setMaximumIntegerDigits(MAXIMUM_INTEGER_DIGITS);
    }

    @Override
    public Result<Double> convertToModel(String value, ValueContext context) {
        try {
            // Remove non-numeric characters except for decimal point and minus sign
            String sanitizedValue = value.replaceAll("[^\\d.-]", "");
            double doubleValue = numberFormat.parse(sanitizedValue).doubleValue();

            if (doubleValue <= 0) {
                return Result.error("Price must be greater than 0");
            }

            if (sanitizedValue.endsWith(".")) {
                return Result.error("Cannot be converted into a number");
            }

            if (doubleValue < 0.000001) {
                return Result.error("The price is too low to be processed");
            }

            // Check if the number of integer places exceeds the allowed threshold
            if (MathUtils.integerPlacesInNumber(sanitizedValue) > MAXIMUM_INTEGER_DIGITS) {
                return Result.error("The price is too big to be processed");
            }

            // Check if the number of decimal places exceeds the allowed threshold
            int maxFractionDigits = getMaxAllowedFractionDigits(doubleValue);
            if (MathUtils.decimalPlacesInNumber(sanitizedValue) > maxFractionDigits) {
                return Result.error("Too many decimal places. Allowed maximum: " + maxFractionDigits);
            }

            return Result.ok(doubleValue);
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

        return numberFormat.format(value);
    }

    /**
     * Method to get maximum allowed fraction digits based on the value
     */
    private int getMaxAllowedFractionDigits(double value) {
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
