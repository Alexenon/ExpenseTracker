package com.example.application.utils.investment;


import com.example.application.finance.FinancialConstants;
import com.example.application.utils.common.formatters.number.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class EarnCalculator {

	private static final int DAYS_IN_YEAR = 365;
	private static final int DAYS_IN_MONTH = 30;
	private static final int DAYS_IN_WEEK = 7;

	public static void main(String[] args) {

		List<Double> numbers = List.of(
				0.000001,
				0.000012,
				0.000123,
				0.001234,
				0.012345,
				0.123456,
				0.123456,
				1.234567,
				23.45678
		);

		List<DecimalFormatter> formatters = List.of(
				new AmountFormatter(),
				new CompactFormatter(),
				new IntegerFormatter(),
				new DecimalFormatter(),
				new CurrencyFormatter(),
				new PercentageFormatter()
		);

		formatters.forEach(f -> {
			numbers.forEach(n -> System.out.println(n + " -> " + f.format(n)));
			System.out.println();
		});

	}

	private static void printResults(String details, BigDecimal stakingAmount, BigDecimal apr) {
		System.out.printf("""
						| %s
						| Stacking amount - $%.2f, APR - %.2f%%
						|-------------------------------------------|
						| Daily - $%.2f
						| Weekly - $%.2f
						| Monthly - $%.2f
						| Yearly - $%.2f
						|-------------------------------------------|
						""",
				details, stakingAmount, apr,
				earnDaily(stakingAmount, apr),
				earnWeekly(stakingAmount, apr),
				earnMonthly(stakingAmount, apr),
				earnYearly(stakingAmount, apr)
		);
	}

	public static BigDecimal earnDaily(BigDecimal stackingAmount, BigDecimal annualPercentageRate) {
		return earnYearly(stackingAmount, annualPercentageRate)
				.divide(new BigDecimal(DAYS_IN_YEAR), FinancialConstants.PRICE_SCALE, RoundingMode.HALF_UP);
	}

	public static BigDecimal earnWeekly(BigDecimal stackingAmount, BigDecimal annualPercentageRate) {
		return earnDaily(stackingAmount, annualPercentageRate)
				.multiply(BigDecimal.valueOf(DAYS_IN_WEEK));
	}

	public static BigDecimal earnMonthly(BigDecimal stackingAmount, BigDecimal annualPercentageRate) {
		return earnYearly(stackingAmount, annualPercentageRate)
				.divide(BigDecimal.valueOf(DAYS_IN_MONTH), FinancialConstants.PRICE_SCALE, RoundingMode.HALF_UP);
	}

	/**
	 * @param annualPercentageRate - value from 0 to infite, where 100% means that at the end of the year
	 *                             same amount of money will be on balance that was at the start of the year
	 */
	public static BigDecimal earnYearly(BigDecimal stackingAmount, BigDecimal annualPercentageRate) {
		if (annualPercentageRate.signum() < 0)
			throw new IllegalArgumentException("Percentage amount should be greater or equal to zero");

		BigDecimal percentage = annualPercentageRate.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
		return stackingAmount.multiply(percentage);
	}

	public static int daysToEarn(BigDecimal stackingAmount, BigDecimal annualPercentageRate, BigDecimal withdrawAmount) {
		BigDecimal amountToEarn = withdrawAmount.subtract(stackingAmount);
		BigDecimal dailyEarn = earnDaily(stackingAmount, annualPercentageRate);
		return daysToEarn(amountToEarn, dailyEarn);
	}

	public static int daysToEarn(BigDecimal amountToEarn, BigDecimal dailyEarn) {
		return dailyEarn.signum() == 0 ? 0 : amountToEarn.divide(dailyEarn, 0, RoundingMode.HALF_UP).intValue();

	}

}
