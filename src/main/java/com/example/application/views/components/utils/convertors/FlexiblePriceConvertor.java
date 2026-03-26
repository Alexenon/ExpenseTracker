package com.example.application.views.components.utils.convertors;

import com.example.application.utils.common.lang.MathUtils;
import com.example.application.utils.common.lang.NumberUtils;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class FlexiblePriceConvertor implements Converter<String, BigDecimal> {

	private static final int MAXIMUM_INTEGER_DIGITS = 12;

	private final NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);

	public FlexiblePriceConvertor() {
		numberFormat.setGroupingUsed(true);
		numberFormat.setMinimumFractionDigits(0);
		numberFormat.setMaximumIntegerDigits(MAXIMUM_INTEGER_DIGITS);
	}

	@Override
	public Result<BigDecimal> convertToModel(String value, ValueContext context) {
		try {
			if (value == null || value.isEmpty() || value.isBlank()) {
				return Result.ok(null);
			}

			String sanitizedAmount = NumberUtils.sanitizeNumber(value);
			BigDecimal decimal = new BigDecimal(sanitizedAmount);

			if (decimal.signum() <= 0) {
				return Result.error("Price must be greater than 0");
			}

			if (decimal.compareTo(new BigDecimal("0.000001")) < 0) {
				return Result.error("The price is too low to be processed");
			}

			// Check if the number of integer places exceeds the allowed threshold
			if (MathUtils.integerPlacesInNumber(sanitizedAmount) > MAXIMUM_INTEGER_DIGITS) {
				return Result.error("The price is too big to be processed");
			}

			// Check if the number of decimal places exceeds the allowed threshold
			int maxFractionDigits = maxDecimalPlaces(decimal);
			if (MathUtils.decimalPlacesInNumber(sanitizedAmount) > maxFractionDigits) {
				return Result.error("Too many decimal places. Allowed maximum: " + maxFractionDigits);
			}

			return Result.ok(decimal);
		} catch (Exception e) {
			return Result.error("Invalid price format");
		}
	}

	@Override
	public String convertToPresentation(BigDecimal value, ValueContext context) {
		if (value == null) {
			return "";
		}

		int maxFractionDigits = maxDecimalPlaces(value);
		numberFormat.setMaximumFractionDigits(maxFractionDigits);

		return numberFormat.format(value);
	}

	/**
	 * @return number of maximum allowed fraction digits, based on the value
	 */
	public static int maxDecimalPlaces(BigDecimal value) {
		if (value.compareTo(new BigDecimal("1")) >= 0) {
			return 2;
		} else if (value.compareTo(new BigDecimal("0.1")) >= 0) {
			return 3;
		} else if (value.compareTo(new BigDecimal("0.01")) >= 0) {
			return 4;
		} else if (value.compareTo(new BigDecimal("0.001")) >= 0) {
			return 5;
		} else if (value.compareTo(new BigDecimal("0.0001")) >= 0) {
			return 6;
		} else if (value.compareTo(new BigDecimal("0.00001")) >= 0) {
			return 7;
		}
		return 8;
	}

}
