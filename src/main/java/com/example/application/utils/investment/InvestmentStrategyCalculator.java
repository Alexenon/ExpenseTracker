package com.example.application.utils.investment;

import com.example.application.utils.MathUtils;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class InvestmentStrategyCalculator {

    private static final String COIN_NAME = "ETH";

    public static void main(String[] args) {
        double totalAmount = 4000.0;
        double investRatePercentage = 1.0;
//        double sumToInvest = totalAmount * investRatePercentage / 100.0;
//        double sumToInvest = 800;
        double sumToInvest = 250;
        double sellPrice = 500;


        // BTC - 60k submitted
//        List<Double> pricesWhenToBuy = List.of(60_000.0, 55_000.0, 50_000.0, 45_000.0, 40_000.0, 35_000.0, 30_000.0);
        // ETH -
//        List<Double> pricesWhenToBuy = List.of(3000.0, 2750.0, 2500.0, 2250.0, 2000.0);
//        List<Double> pricesWhenToBuy = List.of(0.35, 0.25, 0.10);
        // SOL
        List<Double> pricesWhenToBuy = List.of(140.0, 130.0, 120.0, 110.0);


        System.out.println("Constant Strategy");
        printSell(sumToInvest, pricesWhenToBuy, constantStrategy(sumToInvest, pricesWhenToBuy), sellPrice);

        System.out.println("Linear Strategy");
        printSell(sumToInvest, pricesWhenToBuy, linearStrategy(sumToInvest, pricesWhenToBuy), sellPrice);

        System.out.println("Fibonacci Strategy");
        printSell(sumToInvest, pricesWhenToBuy, fibonacciStrategy(sumToInvest, pricesWhenToBuy), sellPrice);

        System.out.println("Exponential Strategy");
        printSell(sumToInvest, pricesWhenToBuy, exponentialStrategy(sumToInvest, pricesWhenToBuy), sellPrice);

        System.out.println("Custom Strategy");
//        printSell(sumToInvest, pricesWhenToBuy, customStrategy(sumToInvest, List.of(0.3, 0.4, 0.5, 0.2, 0.1)), sellPrice);

        System.out.println("Step Strategy");
        printSell(sumToInvest, pricesWhenToBuy, stepStrategy(sumToInvest, 60_000, 30_000,
                new Step(5000.0, Step.StepType.AMOUNT), StrategyType.LINEAR), sellPrice);


    }

    private static void printBuy(List<Double> pricesToBuy, List<Double> amountToBuy) {
        IntStream.range(0, pricesToBuy.size()).forEach(i -> {
            double price = pricesToBuy.get(i);
            double amount = amountToBuy.get(i);
            System.out.printf("At $%.2f: $%.2f\n", price, amount);
        });
    }

    private static void printSell(double totalInvestAmount, List<Double> pricesToBuy, List<Double> amountToBuy, double sellPrice) {
        System.out.printf("Invest Amount for %s: $%.2f\n", COIN_NAME, totalInvestAmount);
        AtomicReference<Double> totalProfit = new AtomicReference<>((double) 0);

        IntStream.range(0, pricesToBuy.size()).forEach(i -> {
            double buyPrice = pricesToBuy.get(i);
            double investedAmount = amountToBuy.get(i);
            double investPercentage = investedAmount * 100 / totalInvestAmount;
            double profitPercentage = MathUtils.profitPercentage(buyPrice, sellPrice);
            double profit = MathUtils.profit(buyPrice, sellPrice, investedAmount);
            totalProfit.updateAndGet(v -> v + profit);
            System.out.printf("At $%.2f -> invest: $%.2f investRate: %.2f%% -> profitRate: %.2f%% profit: $%.2f\n",
                    buyPrice, investedAmount, investPercentage, profitPercentage, profit);
        });

        System.out.printf("Total profit: $%.2f\n", totalProfit.get());
        System.out.printf("Sell at $%.2f\n\n", sellPrice);
    }


    /*
     * Amount - $100
     * Nr of buys - 3 (Three)
     *  - $60000
     *  - $55000
     *  - $50000
     * */


    /*
    Calculation
        Price Levels: $60,000, $55,000, $50,000
        Number of Levels (n): 3
        Base Increment Amount (I): Total Budget / S = $100 / 3 ≈ $33.33
        At $60,000: $33.33
        At $55,000: $33.33
        At $50,000: $33.33
    * */
    private static List<Double> constantStrategy(double sumToInvest, List<Double> pricesToBuy) {
        double distribution = sumToInvest / pricesToBuy.size();

        return pricesToBuy.stream()
                .map(i -> i = distribution)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    /*
    Calculation
        Price Levels: $60,000, $55,000, $50,000
        Number of Levels (n): 3
        Sum of the Series (S): 1 + 2 + 3 = 6
        Base Increment Amount (I): Total Budget / S = $100 / 6 ≈ $16.67
        At $60,000: 1 time * 16.67 = $16.67
        At $55,000: 2 times * 16.67 = $33.33
        At $50,000: 3 times * 16.67 = $50.00
    * */
    private static List<Double> linearStrategy(double sumToInvest, List<Double> pricesToBuy) {
        int n = pricesToBuy.size();
        int sumOfSeries = n * (n + 1) / 2;  // Sum of the first n natural numbers
        double distribution = sumToInvest / sumOfSeries;

        return IntStream.range(0, pricesToBuy.size())
                .mapToObj(i -> distribution * (i + 1))
                .collect(Collectors.toCollection(LinkedList::new));
    }


    /*
    * Calculation
        Price Levels: $60,000, $55,000, $50,000
        Number of Levels (n): 3
        Sum of the Series (S): 2^0 + 2^1 + 2^2 = 1 + 2 + 4 = 7
        Base Investment Amount (x): Total Budget / S = $100 / 7 ≈ $14.29

        Investment Distribution
        At $60,000: 1 time * 14.29 = $14.29
        At $55,000: 2 times * 14.29 = $28.57
        At $50,000: 4 times * 14.29 = $57.14
    * */
    private static List<Double> exponentialStrategy(double sumToInvest, List<Double> pricesToBuy) {
        double distribution = sumToInvest / MathUtils.sumSeriesPowTwo(pricesToBuy.size());

        return IntStream.range(0, pricesToBuy.size())
                .mapToObj(i -> distribution * Math.pow(2, i))
                .collect(Collectors.toCollection(LinkedList::new));
    }


    /*
    BEST FOR A LOT OF BUYS MORE YOU BUY DECREASING, MORE PRICE

    Sum of first 3 Fibonacci numbers:
        Sum = 1 + 1 + 2 = 4
        Allocate the total budget proportionally:

        The unit of allocation = $100 / 4 = $25
        Distribute the budget:

        At the first level: 1 unit * $25 = $25
        At the second level: 1 unit * $25 = $25
        At the third level: 2 units * $25 = $50
    * */
    private static List<Double> fibonacciStrategy(double sumToInvest, List<Double> pricesToBuy) {
        List<Integer> fibonacciList = MathUtils.generateFibonacciSequence()
                .limit(pricesToBuy.size())
                .boxed()
                .collect(Collectors.toList());

        int fibonacciSum = fibonacciList.stream().mapToInt(Integer::intValue).sum();
        double distribution = sumToInvest / fibonacciSum;

        return fibonacciList.stream()
                .map(i -> i * distribution)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * @param percentagesAmount list of percentages that should be applied to the sum<br>
     *                          Example: [0.2, 0.3, 0.45] - 20%, 30%, 45%
     */
    private static List<Double> customStrategy(double sumToInvest, List<Double> percentagesAmount) {
        return percentagesAmount.stream()
                .mapToDouble(i -> sumToInvest * i)
                .boxed()
                .collect(Collectors.toCollection(LinkedList::new));
    }

    // TODO: Think more about this
    private static List<Double> customStrategy(double startPrice, double endPrice, double decreasePriceStep, double increasePriceStep) {
        return null;
    }

    // TODO: Think more about this
    private static List<Double> stepStrategy(double sumToInvest, double start, double end, Step step, StrategyType strategyType) {
        List<Double> pricesWhenToBuy = new LinkedList<>();

        double currentStep = step.getValue();
        while (start > end) {
            start -= currentStep;
            pricesWhenToBuy.add(start);
        }

        return pricesWhenToBuy;
    }

    private static String formattedPrice(double price) {
        String priceString = String.format("%.2f", price);
        if (price > 0) {
            return "$" + priceString;
        } else {
            return "-$" + priceString.substring(1);
        }
    }

    enum StrategyType {
        CONSTANT, LINEAR, FIBONACCI, EXPONENTIAL
    }

    @Data
    @AllArgsConstructor
    static class Step {
        private double value;
        private StepType type;

        enum StepType {
            AMOUNT, PERCENTAGE
        }
    }

}
