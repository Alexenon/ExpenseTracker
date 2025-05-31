package com.example.application.utils.common.formatters;

import com.example.application.utils.common.number.*;

import java.time.format.DateTimeFormatter;

public class Formatters {

    public static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static final DecimalFormatter CURRENCY = CurrencyFormatter.withDefaults();
    public static final DecimalFormatter AMOUNT = AmountFormatter.withDefaults();
    public static final DecimalFormatter COMPACT = CompactFormatter.withDefaults();
    public static final DecimalFormatter PERCENTAGE = PercentageFormatter.withDefaults();
    public static final DecimalFormatter INTEGER = IntegerFormatter.withDefaults();

}
