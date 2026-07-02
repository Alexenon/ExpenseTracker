package com.example.application.utils.formatters;

import java.text.NumberFormat;

public class IntegerFormatter extends DecimalFormatter {

    public static IntegerFormatter withDefaults() {
        return new IntegerFormatter();
    }

    public IntegerFormatter() {
        numberFormat = NumberFormat.getIntegerInstance();
        setMaximumFractionDigits(0);
        setMaximumIntegerDigits(10);
    }

}
