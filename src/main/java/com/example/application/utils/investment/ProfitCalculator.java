package com.example.application.utils.investment;

import com.example.application.entities.crypto.CryptoTransaction;
import com.example.application.utils.common.lang.MathUtils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.example.application.utils.investment.ProfitUtils.ONE_HUNDRED_PERCENT;

/**
 * Calculates profit and other metrics based on the all transactions that happened <br>
 * FIFO implementation (first in first out)
 */
public class ProfitCalculator {

    private static class Order {
        double quantity;
        double cost;

        Order(double quantity, double cost) {
            this.quantity = quantity;
            this.cost = cost;
        }
    }

    public static double getAverageBuyPrice(List<CryptoTransaction> transactions) {
        double totalCost = calculateTotalCostForBuyTransactions(transactions);
        double totalQuantity = calculateTotalQuantityForBuyTransactions(transactions);
        return calculateAveragePrice(totalCost, totalQuantity);
    }

    public static double getAverageSellPrice(List<CryptoTransaction> transactions) {
        double totalSellCost = calculateTotalCostForSellTransactions(transactions);
        double totalQuantitySold = calculateTotalQuantityForSellTransactions(transactions);
        return calculateAveragePrice(totalSellCost, totalQuantitySold);
    }

    /**
     * Calculates the total realized profit from provided transactions
     *
     * <p>If there are just BUYS transactions, then the value will be {@code 0}
     */
    public static double getRealizedNetProfit(List<CryptoTransaction> transactions) {
        double totalCost = 0.0;
        double remainingQuantity = 0.0;
        double realizedProfit = 0.0;

        for (CryptoTransaction transaction : transactions) {
            if (transaction.isBuyTransaction()) {
                totalCost += transaction.getOrderTotalCost();
                remainingQuantity += transaction.getOrderQuantity();
            } else {
                double sellQuantity = transaction.getOrderQuantity();
                if (sellQuantity > remainingQuantity) {
                    throw new IllegalArgumentException("Selling more than owned");
                }

                // Calculate proportional cost of sold tokens
                double averageCostPerUnit = MathUtils.safeZeroDivision(totalCost, remainingQuantity);
                double costOfSoldTokens = averageCostPerUnit * sellQuantity;
                realizedProfit += transaction.getOrderTotalCost() - costOfSoldTokens;

                // Update remaining portfolio cost and quantity
                totalCost -= costOfSoldTokens;
                remainingQuantity -= sellQuantity;
            }
        }

        return realizedProfit;
    }

    /**
     * Calculates the cost of remaining tokens, where if contains SELL transactions, then the cost value
     * is substracted confirming with transaction order cost
     */
    public static double getRemainingTokensCost(List<CryptoTransaction> transactions) {
        Queue<Order> fifoQueue = new LinkedList<>();
        double remainingCost = 0.0;

        for (CryptoTransaction transaction : transactions) {
            if (transaction.isBuyTransaction()) {
                Order newOrder = new Order(transaction.getOrderQuantity(), transaction.getOrderTotalCost());
                fifoQueue.offer(newOrder);
                remainingCost += transaction.getOrderTotalCost();
            } else {
                double sellQuantity = transaction.getOrderQuantity();
                double sellCost = 0.0;

                while (sellQuantity > 0) {
                    if (fifoQueue.isEmpty()) {
                        throw new IllegalArgumentException("Selling more than owned");
                    }

                    Order oldestOrder = fifoQueue.peek();
                    if (sellQuantity >= oldestOrder.quantity) {
                        sellCost += oldestOrder.cost;
                        sellQuantity -= oldestOrder.quantity;
                        fifoQueue.poll(); // Remove the order from the queue
                    } else {
                        double proportion = sellQuantity / oldestOrder.quantity;
                        sellCost += proportion * oldestOrder.cost;
                        oldestOrder.cost -= proportion * oldestOrder.cost;
                        oldestOrder.quantity -= sellQuantity;
                        sellQuantity = 0;
                    }
                }

                remainingCost -= sellCost;
            }
        }

        return remainingCost;
    }

    public static String getBuySellRatio(List<CryptoTransaction> transactions) {
        double buyCost = calculateTotalCostForBuyTransactions(transactions);
        double sellCost = calculateTotalCostForSellTransactions(transactions);
        double totalCost = buyCost + sellCost;

        double buyRatio = buyCost / totalCost * ONE_HUNDRED_PERCENT;
        double sellRatio = sellCost / totalCost * ONE_HUNDRED_PERCENT;

        return String.format("%d : %d", Math.round(buyRatio), Math.round(sellRatio));
    }

    public static double getAverageHoldingDays(List<CryptoTransaction> transactions) {
        List<CryptoTransaction> sortedAssetTransactions = transactions.stream()
                .sorted(Comparator.comparing(CryptoTransaction::getDate))
                .toList();

        List<CryptoTransaction> buyTransactionsTillSale = new ArrayList<>();
        double totalWeightedHoldingTime = 0;
        double totalSoldQuantity = 0;

        for (CryptoTransaction transaction : sortedAssetTransactions) {
            if (transaction.isBuyTransaction()) {
                buyTransactionsTillSale.add(transaction);
                continue;
            }

            // Process sell transactions
            double quantityToSell = transaction.getOrderQuantity();
            long weightedHoldingTime = 0;

            for (CryptoTransaction buyTransaction : buyTransactionsTillSale) {
                if (quantityToSell <= 0) break;

                double buyQuantity = buyTransaction.getOrderQuantity();
                double soldQuantityFromThisBuy = Math.min(buyQuantity, quantityToSell);
                weightedHoldingTime += (long) (getHoldingTimeInDays(buyTransaction, transaction) * soldQuantityFromThisBuy);
                quantityToSell -= soldQuantityFromThisBuy;
            }

            totalWeightedHoldingTime += weightedHoldingTime;
            totalSoldQuantity += transaction.getOrderQuantity();
        }

        if (totalSoldQuantity == 0) {
            LocalDate firstBoughtDate = buyTransactionsTillSale.getFirst().getDate();
            return getHoldingTimeInDays(firstBoughtDate, LocalDate.now());
        }

        return totalWeightedHoldingTime / totalSoldQuantity;
    }

    //region CALCULATION METHODS
    public static double calculateTotalCostForBuyTransactions(List<CryptoTransaction> transactions) {
        return transactions.stream()
                .filter(CryptoTransaction::isBuyTransaction)
                .mapToDouble(CryptoTransaction::getOrderTotalCost)
                .sum();
    }

    public static double calculateTotalCostForSellTransactions(List<CryptoTransaction> transactions) {
        return transactions.stream()
                .filter(CryptoTransaction::isSellTransaction)
                .mapToDouble(CryptoTransaction::getOrderTotalCost)
                .sum();
    }

    public static double calculateTotalQuantityForBuyTransactions(List<CryptoTransaction> transactions) {
        return transactions.stream()
                .filter(CryptoTransaction::isBuyTransaction)
                .mapToDouble(CryptoTransaction::getOrderQuantity)
                .sum();
    }

    public static double calculateTotalQuantityForSellTransactions(List<CryptoTransaction> transactions) {
        return transactions.stream()
                .filter(CryptoTransaction::isSellTransaction)
                .mapToDouble(CryptoTransaction::getOrderQuantity)
                .sum();
    }

    public static double getAmountOfRemainingTokens(List<CryptoTransaction> transactions) {
        return calculateTotalQuantityForBuyTransactions(transactions) - calculateTotalQuantityForSellTransactions(transactions);
    }

    public static double calculateAveragePrice(double totalCost, double totalQuantity) {
        return MathUtils.safeZeroDivision(totalCost, totalQuantity);
    }

    private static long getHoldingTimeInDays(CryptoTransaction buyTransaction, CryptoTransaction sellTransaction) {
        return getHoldingTimeInDays(buyTransaction.getDate(), sellTransaction.getDate());
    }

    private static long getHoldingTimeInDays(LocalDate buyDate, LocalDate sellDate) {
        return ChronoUnit.DAYS.between(buyDate, sellDate);
    }
    //endregion

}
