package com.example.application.utils.common.number;

import com.example.application.entities.crypto.Asset;
import com.example.application.utils.common.StringUtils;

import java.text.NumberFormat;
import java.util.Locale;

public class AmountFormatter extends DecimalFormatter {

    public static AmountFormatter withDefaults() {
        return new AmountFormatter();
    }

    public AmountFormatter() {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
        numberFormat.setGroupingUsed(true);
        numberFormat.setMinimumFractionDigits(0);
    }

    @Override
    public String format(Double value) {
        value = Math.abs(value);

        if (value >= 1000) {
            numberFormat.setMaximumFractionDigits(0);
        } else if (value >= 1) {
            numberFormat.setMaximumFractionDigits(2);
        } else if (value >= 0.1) {
            numberFormat.setMaximumFractionDigits(3);
        } else if (value >= 0.01) {
            numberFormat.setMaximumFractionDigits(4);
        } else if (value >= 0.001) {
            numberFormat.setMaximumFractionDigits(5);
        } else if (value >= 0.0001) {
            numberFormat.setMaximumFractionDigits(6);
        } else if (value >= 0.00001) {
            numberFormat.setMaximumFractionDigits(7);
        } else {
            numberFormat.setMaximumFractionDigits(8);
        }

        return StringUtils.stripTrailingZeroes(numberFormat.format(value));
    }

    public String format(Double d, String symbol) {
        return String.format("%s %s", format(d), symbol);
    }

    public String format(Double d, Asset asset) {
        return format(d, asset.getSymbol());
    }

}
