package com.example.application.views.components.utils.common;

import com.example.application.utils.common.formatters.CommonFormatters;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.function.ValueProvider;
import org.springframework.lang.Nullable;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

public class GridUtils {

    private static final String MISSING_DATA_SIGN = "-";

    public static <T> LitRenderer<T> columnPriceRenderer(@Nullable ValueProvider<T, Number> priceProvider) {
        return LitRenderer.<T>of("<p>${item.price}</p>")
                .withProperty("price", t -> {
                    if (priceProvider == null)
                        return MISSING_DATA_SIGN;

                    NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
                    double price = priceProvider.apply(t).doubleValue();

                    return Double.isNaN(price) || price <= 0
                            ? MISSING_DATA_SIGN
                            : formatter.format(price);
                });
    }

    public static <T> LitRenderer<T> columnPercentageRenderer(@Nullable ValueProvider<T, Double> percentageProvider) {
        return LitRenderer.<T>of("<p>${item.percentage}</p>")
                .withProperty("percentage", t -> {
                    if (percentageProvider == null)
                        return MISSING_DATA_SIGN;

                    Double percentageValue = percentageProvider.apply(t);

                    return percentageValue == null || Double.isNaN(percentageValue)
                            ? MISSING_DATA_SIGN
                            : CommonFormatters.PERCENTAGE.format(percentageValue);
                });
    }

    public static <T> LitRenderer<T> columnAmountRenderer(@Nullable ValueProvider<T, Double> amountProvider) {
        return columnAmountRenderer(amountProvider, null);
    }

    public static <T> LitRenderer<T> columnAmountRenderer(@Nullable ValueProvider<T, Double> amountProvider,
                                                          @Nullable ValueProvider<T, String> symbolProvider)
    {
        return LitRenderer.<T>of("<p>${item.amount}</p>")
                .withProperty("amount", t -> {
                            if (amountProvider == null)
                                return MISSING_DATA_SIGN;

                            Double amountOfTokens = amountProvider.apply(t);
                            String formattedAmount = CommonFormatters.AMOUNT.format(amountOfTokens);

                            if (amountOfTokens == null || Double.isNaN(amountOfTokens) || amountOfTokens <= 0)
                                return MISSING_DATA_SIGN;

                            if (symbolProvider == null)
                                return formattedAmount;

                            String symbol = Objects.requireNonNullElse(symbolProvider.apply(t), "");
                            return symbol.isEmpty() ? formattedAmount : formattedAmount + " " + symbol;
                        }
                );
    }

}
