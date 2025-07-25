package com.example.application.utils.common.lang;

public class NumberUtils {

    private NumberUtils() {
        // Constructor not needed
    }

    public static double checkDouble(double value) {
        if (Double.isNaN(value))
            throw new IllegalArgumentException("Invalid number: value is not a number");

        if (Double.isInfinite(value))
            throw new IllegalArgumentException("Invalid number: value is inifite");

        if (value >= Double.MAX_VALUE || value <= Double.MIN_VALUE)
            throw new IllegalArgumentException("Invalid number: value exceeds the allowed DOUBLE range");

        return value;
    }

}
