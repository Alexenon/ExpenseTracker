package com.example.application.utils.common.number;

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
