package com.example.application.utils;

import java.util.regex.Pattern;

public class StringUtils {

    public static String replaceSpecialCharacters(String str) {
        return Pattern
                .compile("[^a-zA-Z0-9\\s]")
                .matcher(str)
                .replaceAll("");
    }

    public static String replaceWithCapitalLetter(String str) {
        if (str.isEmpty() || str.isBlank()) {
            return str;
        }

        return Character.toUpperCase(str.charAt(0)) + str.substring(1).toLowerCase();
    }


}
