package com.example.application.utils.investment;

public class EarnCalculator {

    private static final int DAYS_IN_YEAR = 365;
    private static final int DAYS_IN_MONTH = 12;

    public static void main(String[] args) {
        double totalAmount = 1000;
        double amountStaked = 500;

        double flexibleEarnLimit = earnDaily(amountStaked, 7.29);
        double flexibleEarnNoLimit = earnDaily(totalAmount, 2.29);
        double fixedEarnWealth = earnDaily(totalAmount - amountStaked, 2.5);

        System.out.println(flexibleEarnLimit);
        System.out.println(flexibleEarnNoLimit);
        System.out.println(flexibleEarnLimit + fixedEarnWealth);
        System.out.println(earnDaily(500, 7.29)     // USDT Staking
                           + earnDaily(500, 7.09)   // USDC Staking
                           + earnDaily(500, 2.5));  // Wealth Management

        System.out.println(earnDaily(1000, 158));

    }

    public static double earnDaily(double startAmount, double annualPercentageRate) {
        return earnYearly(startAmount, annualPercentageRate) / DAYS_IN_YEAR;
    }

    public static double earnMonthly(double startAmount, double annualPercentageRate) {
        return earnYearly(startAmount, annualPercentageRate) / DAYS_IN_MONTH;
    }

    public static double earnYearly(double startAmount, double annualPercentageRate) {
        if (annualPercentageRate <= -1)
            throw new IllegalArgumentException("Percentage amount should be greater or equal to zero");

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
