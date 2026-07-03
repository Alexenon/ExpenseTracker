package com.example.application.utils.formatters;

import com.example.application.utils.PercentageFormatter;

public class CommonFormatters extends com.example.application.utils.CommonFormatters {

	/**
	 * Format: "19 Aug 2024"
	 */
	public static final String DATE_FRIENDLY_FORMAT = "dd MMM yyyy";

	public static final IntegerFormatter INTEGER = IntegerFormatter.withDefaults();
	public static final DecimalFormatter DECIMAL = DecimalFormatter.withDefaults();
	public static final AmountFormatter AMOUNT = AmountFormatter.withDefaults();
	public static final CurrencyFormatter CURRENCY = CurrencyFormatter.withDefaults();
	public static final PercentageFormatter PERCENTAGE = PercentageFormatter.withDefaults();
	public static final CompactFormatter COMPACT = CompactFormatter.withDefaults();

}
