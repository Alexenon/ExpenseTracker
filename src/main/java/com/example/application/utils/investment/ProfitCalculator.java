package com.example.application.utils.investment;

import com.example.application.entities.crypto.CryptoTransaction;
import com.example.application.utils.common.MathUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


/**
 * FIFO Implementation (first in first out)
 * */
public class ProfitCalculator {

    static class Batch {
        double quantity;
        double cost;

        Batch(double quantity, double cost) {
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
     */
    public static double getRealizedProfit(List<CryptoTransaction> transactions) {
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

    public static double getTransactionsRemainingCost(List<CryptoTransaction> transactions) {
        Queue<Batch> fifoQueue = new LinkedList<>();
        double remainingCost = 0.0;

        for (CryptoTransaction transaction : transactions) {
            if (transaction.isBuyTransaction()) {
                Batch newBatch = new Batch(transaction.getOrderQuantity(), transaction.getOrderTotalCost());
                fifoQueue.offer(newBatch);
                remainingCost += transaction.getOrderTotalCost();
            } else {
                double sellQuantity = transaction.getOrderQuantity();
                double sellCost = 0.0;

                while (sellQuantity > 0) {
                    if (fifoQueue.isEmpty()) {
                        throw new IllegalArgumentException("Selling more than owned");
                    }

                    Batch oldestBatch = fifoQueue.peek();
                    if (sellQuantity >= oldestBatch.quantity) {
                        sellCost += oldestBatch.cost;
                        sellQuantity -= oldestBatch.quantity;
                        fifoQueue.poll(); // Remove the batch from the queue
                    } else {
                        double proportion = sellQuantity / oldestBatch.quantity;
                        sellCost += proportion * oldestBatch.cost;
                        oldestBatch.cost -= proportion * oldestBatch.cost;
                        oldestBatch.quantity -= sellQuantity;
                        sellQuantity = 0;
                    }
                }

                remainingCost -= sellCost;
            }
        }

        return remainingCost;
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

    private static double calculateAveragePrice(double totalCost, double totalQuantity) {
        return MathUtils.safeZeroDivision(totalCost, totalQuantity);
    }
    //endregion

}
