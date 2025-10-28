package com.example.application.utils.common.lang;

public class NumberUtils {

    private NumberUtils() {
        /* Hidden constructor */
    }

    public static double checkDouble(double value) {
        if (Double.isNaN(value))
            throw new IllegalArgumentException("Invalid number: value is not a number");

        if (Double.isInfinite(value))
            throw new IllegalArgumentException("Invalid number: value is inifite");

        return value;
    }

}
