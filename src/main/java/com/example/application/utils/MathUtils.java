package com.example.application.utils;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class MathUtils {

    /**
     * Usage
     * <pre><code>
     *     generateFibonacciSequence()
     *                 .limit(5)
     *                 .boxed()
     *                 .collect(Collectors.toList());
     * </code></pre>
     *
     *
     * @return a stream that can be boxed as a list of integers, or any kind of list
     * */
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


}
