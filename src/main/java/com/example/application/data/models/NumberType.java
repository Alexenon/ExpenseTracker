package com.example.application.data.models;

import com.example.application.utils.common.StringUtils;

import java.math.BigDecimal;
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
            NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
            numberFormat.setGroupingUsed(true);
            numberFormat.setMinimumFractionDigits(0);

            if (value >= 1000) {
                numberFormat.setMaximumFractionDigits(0);
            } else if (value >= 1) {
                numberFormat.setMaximumFractionDigits(4);
            } else if (value >= 0.001) {
                numberFormat.setMaximumFractionDigits(6);
            } else {
                numberFormat.setMaximumFractionDigits(8);
            }

            return StringUtils.stripTrailingZeroes(numberFormat.format(value));
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
    },
    PRICE {
        @Override
        public String parse(double value) {
            if (value == 0.0)
                return "$0";

            if (value > 1.00) {
                String formattedPrice = NumberFormat.getCurrencyInstance(Locale.US).format(value);
                return StringUtils.stripTrailingZeroes(formattedPrice);
            }

            // +3 digits after decimal point
            int decimalPlaces = (int) Math.abs(Math.floor(Math.log10(value))) + 3;
            String shortedPrice = String.format("%." + decimalPlaces + "f", value);

            return "$" + new BigDecimal(shortedPrice)
                    .stripTrailingZeros()
                    .toPlainString();
        }
    };

    public abstract String parse(double value);
}