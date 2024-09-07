package com.example.application.data.models;

import java.text.NumberFormat;
import java.util.Locale;

/* TODO:
    Take a look at the DecimalFormat class. Most people use it for formatting numbers as strings,
    but it actually has a parse method to go the other way around! You initialize it with your
    pattern (see the tutorial), and then invoke parse() on the input string.
*/

/**
 * Class to identify type of number, and how it should be parsed as String
 * <li>integer - rounded integer, without digits after comma</li>
 * <li>percentage - 25.34%</li>
 * <li>currency - $250,300,00.05</li>
 */
public enum NumberType {
    INTEGER {
        @Override
        public String parse(double value) {
            return NumberFormat.getIntegerInstance().format(value);
        }
    },
    AMOUNT {
        @Override
        public String parse(double value) {
            return value < 1
                    ? String.format("%.6f", value)
                    : String.format("%.2f", value);
        }
    },
    PERCENT {
        @Override
        public String parse(double value) {
            return String.format("%.2f%%", Math.abs(value));
        }
    },
    CURRENCY {
        @Override
        public String parse(double value) {
            return NumberFormat.getCurrencyInstance(Locale.US).format(value);
        }
    };

    public abstract String parse(double value);
}