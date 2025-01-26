package com.example.application.utils.common.number;

import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyFormatter extends DecimalFormatter {

    public static CurrencyFormatter withDefaults() {
        return new CurrencyFormatter();
    }

    public CurrencyFormatter() {
        numberFormat = NumberFormat.getCurrencyInstance(Locale.US);
        setMinimumFractionDigits(0);
        setMaximumFractionDigits(2);
        setMaximumIntegerDigits(10);
    }
}
