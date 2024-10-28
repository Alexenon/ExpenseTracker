package com.example.application.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FastUtils {

    public static void main(String[] args) {
        List<String> optionSeries = List.of(
                "AAPL 16 AUG 24 100",
                "AAPL 16 SEP 24 100",
                "AAPL 21 OCT 24 100",
                "AAPL 16 NOV 24 100",
                "AAPL 16 DEC 24 100",
                "AAPL 16 JAN 24 100",
                "AAPL 16 MAR 24 100"
        );

        String getCurrentMonthOption = optionSeries.stream()
                .filter(option -> extractExpireDate(option).isAfter(LocalDate.now()))
                .findFirst()
                .orElseThrow();

        System.out.println(getCurrentMonthOption);

    }

    /**
     * @param option must be in the format {@code "SYMBOL dd MMM yy STRIKE"}, such as {@code "AAPL 16 AUG 24 100"}.
     *
     * @return expiration date extracted from the option name.
     * @throws IllegalArgumentException if the input does not follow the expected format.
     * @throws DateTimeParseException   if the extracted date cannot be parsed.
     */
    private static LocalDate extractExpireDate(String option) {
        Pattern pattern = Pattern.compile("\\w+ (\\d+) ([A-Z]{3}) (\\d+) \\d+");
        Matcher matcher = pattern.matcher(option);

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Option doesn't match the expected format: \"SYMBOL dd MMM yy STRIKE\"");
        }

        String dateStr = String.format("%s %s %s", matcher.group(1), matcher.group(2), matcher.group(3));

        // Formatter able to handle 2 digits year dates -> "18 AUG 24"
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("dd MMM ")
                .appendValueReduced(ChronoField.YEAR, 2, 2, 2000)
                .toFormatter(Locale.ENGLISH);

        return LocalDate.parse(dateStr, formatter);
    }


}
