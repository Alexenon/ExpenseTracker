package com.example.application.utils.common.number;

import com.example.application.utils.common.StringUtils;

import java.text.NumberFormat;
import java.util.Locale;

public class AmountFormatter extends DecimalFormatter {

    public AmountFormatter() {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
        numberFormat.setGroupingUsed(true);
        numberFormat.setMinimumFractionDigits(0);
    }

    // TODO: Work on this more
    @Override
    public String format(Double d) {
        if (d >= 1000) {
            numberFormat.setMaximumFractionDigits(0);
        } else if (d >= 1) {
            numberFormat.setMaximumFractionDigits(4);
        } else if (d >= 0.001) {
            numberFormat.setMaximumFractionDigits(6);
        } else {
            numberFormat.setMaximumFractionDigits(8);
        }

        return StringUtils.stripTrailingZeroes(numberFormat.format(d));
    }

}
