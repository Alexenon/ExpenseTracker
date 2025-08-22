package com.example.application.utils.investment;


import com.example.application.utils.common.formatters.number.*;

import java.util.List;

public class EarnCalculator {

    private static final int DAYS_IN_YEAR = 365;
    private static final int DAYS_IN_MONTH = 30;

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

    private static void printResults(String details, double stakingAmount, double apr) {
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

    public static double earnDaily(double stackingAmount, double annualPercentageRate) {
        return earnYearly(stackingAmount, annualPercentageRate) / DAYS_IN_YEAR;
    }

    public static double earnWeekly(double stackingAmount, double annualPercentageRate) {
        return earnDaily(stackingAmount, annualPercentageRate) * 7;
    }

    public static double earnMonthly(double stackingAmount, double annualPercentageRate) {
        return earnYearly(stackingAmount, annualPercentageRate) / DAYS_IN_MONTH;
    }

    public static double earnYearly(double stackingAmount, double annualPercentageRate) {
        if (annualPercentageRate < 0)
            throw new IllegalArgumentException("Percentage amount should be greater or equal to zero");

        return stackingAmount * (annualPercentageRate / 100);
    }

    public static int daysToEarn(double stackingAmount, double apr, double withdrawAmount) {
        double amountToEarn = withdrawAmount - stackingAmount;
        double dailyEarn = earnDaily(stackingAmount, apr);
        return daysToEarn(amountToEarn, dailyEarn);
    }

    public static int daysToEarn(double amountToEarn, double dailyEarn) {
        return (int) Math.ceil(amountToEarn / dailyEarn);
    }


}
