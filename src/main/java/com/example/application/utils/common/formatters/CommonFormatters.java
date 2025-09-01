package com.example.application.utils.common.formatters;

import com.example.application.utils.common.formatters.number.*;

import java.time.format.DateTimeFormatter;

public class CommonFormatters {

    public static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss");
    public static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    /**
     * 19 Aug 2024
     * */
    public static final String DATE_FRIENDLY_FORMAT = "dd MMM yyyy";

    public static final IntegerFormatter INTEGER = IntegerFormatter.withDefaults();
    public static final DecimalFormatter DECIMAL = DecimalFormatter.withDefaults();
    public static final AmountFormatter AMOUNT = AmountFormatter.withDefaults();
    public static final CurrencyFormatter CURRENCY = CurrencyFormatter.withDefaults();
    public static final PercentageFormatter PERCENTAGE = PercentageFormatter.withDefaults();
    public static final CompactFormatter COMPACT = CompactFormatter.withDefaults();

}
