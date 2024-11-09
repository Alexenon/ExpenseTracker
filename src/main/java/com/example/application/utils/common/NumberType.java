package com.example.application.utils.common;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

/*
    TODO: Make this class to return only NumberFormat
        - Explain difference between price and currency !!!
        - Check spring NumberStyleFormatter
* */

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
            NumberFormat numberFormat = NumberFormat.getPercentInstance();
            numberFormat.setMaximumFractionDigits(2);
            double formattedValue = Math.abs(value / 100);
            return numberFormat.format(formattedValue);
        }
    },
    CLEAR_PERCENT {
        @Override
        public String parse(double value) {
            NumberFormat numberFormat = NumberFormat.getPercentInstance();
            double formattedValue = Math.abs(value / 100);
            return numberFormat.format(formattedValue);
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
            String format = "%." + decimalPlaces + "f";
            String shortedPrice = String.format(format, value);

            return "$" + new BigDecimal(shortedPrice)
                    .stripTrailingZeros()
                    .toPlainString();
        }
    },
    SHORT {
        @Override
        public String parse(double value) {
            NumberFormat numberFormat = NumberFormat.getCompactNumberInstance();
            return numberFormat.format(value);
        }
    };

    public abstract String parse(double value);
}