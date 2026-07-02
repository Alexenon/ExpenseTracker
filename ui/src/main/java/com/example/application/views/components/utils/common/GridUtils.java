package com.example.application.views.components.utils.common;

import com.example.application.utils.formatters.CommonFormatters;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.function.ValueProvider;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;

public class GridUtils {

	private static final String MISSING_DATA_SIGN = "-";

	public static <T> LitRenderer<T> columnPriceRenderer(@Nullable ValueProvider<T, BigDecimal> priceProvider) {
		return LitRenderer.<T>of("<p>${item.price}</p>")
				.withProperty("price", t -> {
					if (priceProvider == null)
						return MISSING_DATA_SIGN;

					NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
					BigDecimal price = priceProvider.apply(t);

					return price.signum() <= 0
							? MISSING_DATA_SIGN
							: formatter.format(price);
				});
	}

	public static <T> LitRenderer<T> columnPercentageRenderer(@Nullable ValueProvider<T, BigDecimal> percentageProvider) {
		return LitRenderer.<T>of("<p>${item.percentage}</p>")
				.withProperty("percentage", t -> {
					if (percentageProvider == null)
						return MISSING_DATA_SIGN;

					BigDecimal percentageValue = percentageProvider.apply(t);

					return percentageValue == null
							? MISSING_DATA_SIGN
							: CommonFormatters.PERCENTAGE.format(percentageValue);
				});
	}

	public static <T> LitRenderer<T> columnAmountRenderer(@Nullable ValueProvider<T, BigDecimal> amountProvider) {
		return columnAmountRenderer(amountProvider, null);
	}

	public static <T> LitRenderer<T> columnAmountRenderer(@Nullable ValueProvider<T, BigDecimal> amountProvider,
														  @Nullable ValueProvider<T, String> symbolProvider)
	{
		return LitRenderer.<T>of("<p>${item.amount}</p>")
				.withProperty("amount", t -> {
							if (amountProvider == null)
								return MISSING_DATA_SIGN;

							BigDecimal amountOfTokens = amountProvider.apply(t);
							String formattedAmount = CommonFormatters.AMOUNT.format(amountOfTokens);

							if (amountOfTokens == null || amountOfTokens.signum() <= 0)
								return MISSING_DATA_SIGN;

							if (symbolProvider == null)
								return formattedAmount;

							return Optional.ofNullable(symbolProvider.apply(t))
									.map(s -> formattedAmount + " " + s)
									.orElse(formattedAmount);
						}
				);
	}

}
