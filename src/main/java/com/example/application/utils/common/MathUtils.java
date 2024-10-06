package com.example.application.utils.common;

import com.example.application.entities.crypto.CryptoTransaction;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class MathUtils {

    private static final double BILLION = 1_000_000_000.0;
    private static final double MILLION = 1_000_000.0;

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

    public static double profit(double buyPrice, double sellPrice, double investedAmount) {
        double coinsBought = investedAmount / buyPrice;
        return (sellPrice - buyPrice) * coinsBought;
    }

    public static double profitPercentage(double buyPrice, double sellPrice) {
        return sellPrice * 100 / buyPrice;
    }

    public static double profit(CryptoTransaction transaction, double currentPrice) {
        return profit(transaction.getMarketPrice(), currentPrice, transaction.getOrderTotalCost());
    }

    public static int percentageOf(BigInteger a, BigInteger b) {
        return a.multiply(BigInteger.valueOf(100)).divide(b).intValue();
    }

    public static String formatBigNumber(BigInteger number) {
        return formatBigNumber(number.doubleValue());
    }

    public static String formatBigNumber(double number) {
        if (number >= BILLION) {
            return String.format("%.2f B", number / BILLION);
        }

        if (number >= MILLION) {
            return String.format("%.2f M", number / MILLION);
        }

        return String.format("%.2f", number);
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
            if(buyPrice == currentPrice)
                return currentPrice;

            double diff = Math.abs(currentPrice - buyPrice);
        }

        return closestPrice;
    }

}
