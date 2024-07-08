package com.example.application.utils.investment;

public class EarnCalculator {

    private static final int DAYS_IN_YEAR = 365;
    private static final int DAYS_IN_MONTH = 12;

    public static void main(String[] args) {
        System.out.println(earnYearly(480, 7));
        System.out.println(daysToEarn(480, 7, 487));
    }

    public static double earnDaily(double startAmount, double annualPercentageRate) {
        return earnYearly(startAmount, annualPercentageRate) / DAYS_IN_YEAR;
    }

    public static double earnMonthly(double startAmount, double annualPercentageRate) {
        return earnYearly(startAmount, annualPercentageRate) / DAYS_IN_MONTH;
    }

    public static double earnYearly(double startAmount, double annualPercentageRate) {
        if (annualPercentageRate < 0 || annualPercentageRate > 100)
            throw new IllegalArgumentException("Percentage amount should be 0-100%");

        return startAmount * (annualPercentageRate / 100);
    }

    public static int daysToEarn(double startAmount, double apr, double finalAmount) {
        double amountToEarn = finalAmount - startAmount;
        double dailyEarn = earnDaily(startAmount, apr);
        return daysToEarn(amountToEarn, dailyEarn);
    }

    public static int daysToEarn(double amountToEarn, double dailyEarn) {
        return (int) Math.ceil(amountToEarn / dailyEarn);
    }


}
