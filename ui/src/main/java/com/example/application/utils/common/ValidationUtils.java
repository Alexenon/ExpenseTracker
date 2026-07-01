package com.example.application.utils.common;

public class ValidationUtils {

    private ValidationUtils() {
        /* Hidden constructor */
    }

    public static final int MIN_STRING_LENGTH = 5;
    public static final int MAX_STRING_LENGTH = 20;

    public static boolean isPriceInvalid(double price) {
        return Double.isNaN(price) || Double.isInfinite(price) || price <= 0;
    }

    public static boolean isAmountInvalid(double amount) {
        return Double.isNaN(amount) || Double.isInfinite(amount) || amount <= 0;
    }

}
