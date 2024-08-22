package com.example.application.utils.investment;

public class EarnCalculator {

    private static final int DAYS_IN_YEAR = 365;
    private static final int DAYS_IN_MONTH = 30;

    public static void main(String[] args) {
        System.out.println("|" + "-".repeat(43) + "|");
        printResults("Flexible Earning USDT", 500, 7.0);
        printResults("Flexible Earning USDE", 500, 18.0);
        printResults("Wealth Management USDT", 2000, 2.5);
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
                earnDaily(stakingAmount, apr) * 7,
                earnMonthly(stakingAmount, apr),
                earnYearly(stakingAmount, apr)
        );
    }

    public static double earnDaily(double stackingAmount, double annualPercentageRate) {
        return earnYearly(stackingAmount, annualPercentageRate) / DAYS_IN_YEAR;
    }

    public static double earnMonthly(double stackingAmount, double annualPercentageRate) {
        return earnYearly(stackingAmount, annualPercentageRate) / DAYS_IN_MONTH;
    }

    public static double earnYearly(double stackingAmount, double annualPercentageRate) {
        if (annualPercentageRate <= -1)
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
