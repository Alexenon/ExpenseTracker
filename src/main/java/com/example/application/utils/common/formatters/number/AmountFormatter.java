package com.example.application.utils.common.formatters.number;

import com.example.application.entities.crypto.Asset;
import com.example.application.utils.common.lang.StringUtils;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class AmountFormatter extends DecimalFormatter {

    public static AmountFormatter withDefaults() {
        return new AmountFormatter();
    }

    private final NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);

    public AmountFormatter() {
        numberFormat.setGroupingUsed(true);
        numberFormat.setMinimumFractionDigits(0);
    }

	public String format(BigDecimal value) {
		if (value == null)
			return "0";

		value = value.abs();

		int digits = fractionDigits(value);

		numberFormat.setMinimumFractionDigits(0);
		numberFormat.setMaximumFractionDigits(digits);

		return StringUtils.stripTrailingZeroes(numberFormat.format(value));
	}

	private static int fractionDigits(BigDecimal value) {
		if (value.compareTo(BigDecimal.valueOf(1000)) >= 0)
			return 0;

		if (value.compareTo(BigDecimal.ONE) >= 0)
			return 2;

		int leadingZeros = value.scale() - value.precision() + 1;

		return Math.min(8, 3 + Math.max(0, leadingZeros));
	}

    public String format(BigDecimal d, Asset asset) {
        return format(d, asset.getSymbol());
    }

    public String format(BigDecimal d, String symbol) {
        return String.format("%s %s", format(d), symbol);
    }

}
