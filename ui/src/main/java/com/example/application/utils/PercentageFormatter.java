package com.example.application.utils;

import com.example.application.utils.formatters.DecimalFormatter;

import java.text.NumberFormat;

public class PercentageFormatter extends DecimalFormatter {

    public static PercentageFormatter withDefaults() {
        return new PercentageFormatter();
    }

    public PercentageFormatter() {
        numberFormat = NumberFormat.getPercentInstance();
        numberFormat.setMaximumFractionDigits(2);
    }

    @Override
    public String format(Double d) {
        return super.format(d / 100);
    }
}
