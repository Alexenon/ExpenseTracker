package com.example.application.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Locale;

public class FastUtils {

    public static void main(String[] args) {
        List<String> optionSeries = List.of(
                "AAPL 16 AUG 24 100",
                "AAPL 16 SEP 24 100",
                "AAPL 20 OCT 24 100",
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
     * @param option extracts and parses the expiration date from an option name.
     *               Must be in the format {@code "SYMBOL dd MMM yy STRIKE"}, such as {@code "AAPL 16 AUG 24 100"}.
     *
     * @return expiration date extracted from the option name.
     * @throws IllegalArgumentException if the input does not follow the expected format.
     * @throws DateTimeParseException  if the extracted date cannot be parsed.
     */
    private static LocalDate extractExpireDate(String option) {
        String[] parts = option.split("\\s");

        String dateStr = String.format("%s %s %s", parts[1], parts[2], parts[3]);

        // Formatter able to handle 2 digits year -> "18 AUG 24"
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("dd MMM ")
                .appendValueReduced(ChronoField.YEAR, 2, 2, 2000)
                .toFormatter(Locale.ENGLISH);

        return LocalDate.parse(dateStr, formatter);
    }



}
