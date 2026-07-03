package com.example.application.utils.lang;

import java.math.BigDecimal;

public class NumberUtils {

	private NumberUtils() {
		/* Hidden constructor */
	}

	public static BigDecimal applySign(BigDecimal value, boolean isPositive) {
		if (value == null)
			throw new IllegalArgumentException("Value cannot be null");

		return isPositive ? value.abs() : value.abs().negate();
	}

	public static double checkDouble(double value) {
		if (Double.isNaN(value))
			throw new IllegalArgumentException("Invalid number: value is not a number");

		if (Double.isInfinite(value))
			throw new IllegalArgumentException("Invalid number: value is inifite");

		return value;
	}

	/**
	 * Remove non-numeric characters except for decimal point and minus sign
	 * */
	public static String sanitizeNumber(String number) {
		return number.replaceAll("[^\\d.-]", "");
	}

}
