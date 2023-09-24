package com.example.application.utils;

import org.springframework.lang.Nullable;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringUtils {

    public static boolean hasLength(@Nullable String str) {
        return str != null && !str.isEmpty();
    }

    public static String replaceSpecialCharacters(@Nullable String str) {
        if(!hasLength(str)) return str;

        return Pattern
                .compile("[^a-zA-Z0-9\\s]")
                .matcher(str)
                .replaceAll("");
    }

    public static String replaceWithCapitalLetter(@Nullable String str) {
        if (!hasLength(str)) return str;

        return Character.toUpperCase(str.charAt(0)) + str.substring(1).toLowerCase();
    }

    public static String replaceWithCapitalLetter(@Nullable String str, String delimeter) {
        if (!hasLength(str)) return str;

        return Arrays
                .stream(str.split(delimeter))
                .map(s -> s = replaceWithCapitalLetter(s))
                .filter(StringUtils::hasLength)
                .collect(Collectors.joining(delimeter));
    }

}
