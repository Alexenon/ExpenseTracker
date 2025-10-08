package com.example.application.utils.common.lang;

public class NumberUtils {

    private NumberUtils() {
        // Constructor not needed
    }
    
    public static boolean isPriceInvalid(double price) {
        return Double.isNaN(price) || Double.isInfinite(price) || price < 0;
    }

    public static boolean isAmountInvalid(double amount) {
        return Double.isNaN(amount) || Double.isInfinite(amount) || amount < 0;
    }

    public static double checkDouble(double value) {
        if (Double.isNaN(value))
            throw new IllegalArgumentException("Invalid number: value is not a number");

        if (Double.isInfinite(value))
            throw new IllegalArgumentException("Invalid number: value is inifite");

        return value;
    }

}
