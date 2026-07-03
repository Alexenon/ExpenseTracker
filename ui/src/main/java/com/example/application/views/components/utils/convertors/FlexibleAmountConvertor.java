package com.example.application.views.components.utils.convertors;

import com.example.application.utils.lang.NumberUtils;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;

public class FlexibleAmountConvertor implements Converter<String, BigDecimal> {

	private static final int MAXIMUM_FRACTION_DIGITS = 8;
	private static final int MAXIMUM_INTEGER_DIGITS = 12;

	private final NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);

	public FlexibleAmountConvertor() {
		numberFormat.setGroupingUsed(true); // Enable thousands separators
		numberFormat.setMinimumFractionDigits(0); // Allows the price to skip decimal points if not entered
		numberFormat.setMaximumFractionDigits(MAXIMUM_FRACTION_DIGITS);
		numberFormat.setMaximumIntegerDigits(MAXIMUM_INTEGER_DIGITS);
	}

	@Override
	public Result<BigDecimal> convertToModel(String value, ValueContext context) {
		try {
			if (value == null || value.isEmpty() || value.isBlank()) {
				return Result.ok(null);
			}

			String sanitizedAmount = NumberUtils.sanitizeNumber(value);
			BigDecimal result = new BigDecimal(sanitizedAmount);
			return Result.ok(result);
		} catch (Exception e) {
			return Result.error("Conversion error: " + e.getMessage());
		}
	}

	@Override
	public String convertToPresentation(BigDecimal value, ValueContext context) {
		return Optional.ofNullable(value)
				.map(numberFormat::format)
				.orElse("");
	}

}
