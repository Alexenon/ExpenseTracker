package com.example.application.utils.common.lang;

public class StringUtils {

    private StringUtils() {
        /* Hidden constructor */
    }

    public static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public static boolean isNotBlank(String value) {
        return !isBlank(value);
    }

    public static String uppercaseFirstLetter(String text) {
        return Character.toUpperCase(text.charAt(0)) + text.substring(1).toLowerCase();
    }

    /**
     * This will strip trailing zeroes and the decimal point only if there are no non-zero digits after the decimal.
     *
     * <p>For example:
     * <ul>
     *  <li>"123.4500" becomes "123.45".
     *  <li>"123.000" becomes "123".
     *  <li>"0.002300" becomes "0.0023".
     * </ul>
     */
    public static String stripTrailingZeroes(String number) {
        if (number.contains(".")) {
            return number.replaceAll("\\.?0+$", "");
        }

        return number;
    }

}
