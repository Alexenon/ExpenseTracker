package com.example.application.utils.common;

public class StringUtils {

    public static String uppercaseFirstLetter(String text) {
        return Character.toUpperCase(text.charAt(0)) + text.substring(1).toLowerCase();
    }

}
