package com.example.application.utils.common.formatters.number;

import java.text.NumberFormat;

public class CompactFormatter extends DecimalFormatter {

    public static CompactFormatter withDefaults() {
        return new CompactFormatter();
    }

    public CompactFormatter() {
        numberFormat = NumberFormat.getCompactNumberInstance();
        numberFormat.setGroupingUsed(false);
        numberFormat.setMaximumFractionDigits(0);
    }

}
