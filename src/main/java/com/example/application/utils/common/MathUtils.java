package com.example.application.utils.common;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static com.example.application.utils.investment.ProfitUtils.ONE_HUNDRED_PERCENT;

public class MathUtils {

    /**
     * Method to count whole part in a decimal number
     */
    public static int integerPlacesInNumber(String number) {
        int indexOfDecimal = number.indexOf('.');
        return indexOfDecimal < 0 ? number.length() : indexOfDecimal;
    }

    /**
     * Method to count the decimal places in the string
     */
    public static int decimalPlacesInNumber(String number) {
        int indexOfDecimal = number.indexOf('.');
        return indexOfDecimal < 0 ? 0 : number.length() - indexOfDecimal - 1;
    }

    public static double safeZeroDivision(double dividend, double divisor) {
        return Double.compare(divisor, 0.0) == 0 ? 0 : dividend / divisor;
    }

    /**
     * Usage
     * <pre><code>
     *     generateFibonacciSequence()
     *                 .limit(5)
     *                 .boxed()
     *                 .collect(Collectors.toList());
     * </code></pre>
     *
     * @return a stream that can be boxed as a list of integers, or any kind of list
     */
    public static IntStream generateFibonacciSequence() {
        AtomicInteger fibonacci = new AtomicInteger(1);
        return IntStream.iterate(1, fibonacci::getAndAdd);
    }

    public static int sumSeriesPowTwo(int n) {
        return IntStream.range(0, n).map(i -> (int) Math.pow(2, i)).sum();
    }

    public static int percentageOf(BigInteger from, BigInteger to) {
        return from.multiply(BigInteger.valueOf(ONE_HUNDRED_PERCENT)).divide(to).intValue();
    }

    public static int percentageOf(double from, double to) {
        return to == 0
                ? ONE_HUNDRED_PERCENT
                : (int) (from * ONE_HUNDRED_PERCENT / to);
    }

    // [34.000, 35.000, 36.000], 40.000  ->  36.000
    // [34.000, 35.000, 36.000], 20.000  ->  34.000
    // [34.000, 35.000, 36.000], 35.000  ->  35.000
    // [34.000, 37.000, 40.000], 35.000  ->  34.000
    // [34.000, 37.000, 40.000], 36.000  ->  37.000
    // [34.000, 37.000, 40.000], 36.000  ->  37.000
    @SuppressWarnings("unused")
    private double closestPrice(List<Double> prices, double currentPrice) {
        double closestPrice = 0;
        double minDiff = Double.MAX_VALUE;

        for (double buyPrice : prices) {
            if (buyPrice == currentPrice)
                return currentPrice;

            double diff = Math.abs(currentPrice - buyPrice);
        }

        return closestPrice;
    }

}
