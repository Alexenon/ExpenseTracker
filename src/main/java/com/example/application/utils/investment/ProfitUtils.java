package com.example.application.utils.investment;

import com.example.application.entities.crypto.Transaction;
import com.example.application.utils.common.lang.MathUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public class ProfitUtils {

    public static final int ONE_HUNDRED_PERCENT = 100;

    public static double coinsBought(double buyPrice, double investedAmount) {
        return MathUtils.safeDivision(investedAmount, buyPrice);
    }

    public static double worth(double buyPrice, double investedAmount) {
        return coinsBought(buyPrice, investedAmount) * buyPrice;
    }

    public static double netProfit(double buyPrice, double sellPrice, double investedAmount) {
        return (sellPrice - buyPrice) * coinsBought(buyPrice, investedAmount);
    }

    public static double netProfit(Transaction transaction, double currentPrice) {
        return netProfit(transaction.getMarketPrice(), currentPrice, transaction.getOrderTotalCost());
    }

    /**
     * @return percentage profit relative to the investment amount (monetary perspective)
     */
    public static double profitPercentage(double buyPrice, double sellPrice, double investedAmount) {
        return MathUtils.safeDivision(netProfit(buyPrice, sellPrice, investedAmount), investedAmount) * ONE_HUNDRED_PERCENT;
    }

    /**
     * @return percentage increase or decrease in the price of the asset (token price perspective)
     */
    public static double growthPercentage(double buyPrice, double sellPrice) {
        return MathUtils.safeDivision(sellPrice - buyPrice, buyPrice) * ONE_HUNDRED_PERCENT;
    }

    public static double buyPricePerUnit(double totalBuyPrice, double amountTokens) {
        return totalBuyPrice / amountTokens;
    }

    public static double sellPricePerUnit(double totalSellPrice, double amountTokens) {
        return totalSellPrice / amountTokens;
    }

    public static double profitPerUnit(double buyPrice, double sellPrice, int amountTokens) {
        return buyPricePerUnit(buyPrice, amountTokens) - sellPricePerUnit(sellPrice, amountTokens);
    }

    public static double marketCap(BigInteger circulationSupply, double tokenPrice) {
        return new BigDecimal(circulationSupply)
                .multiply(BigDecimal.valueOf(tokenPrice))
                .doubleValue();
    }

    public static double fdv(BigInteger totalSupply, double tokenPrice) {
        return new BigDecimal(totalSupply)
                .multiply(BigDecimal.valueOf(tokenPrice))
                .doubleValue();
    }

    /**
     * Calculates how much the price has recovered after a drop.
     */
    public static double recoveryPercentage(double currentPrice, double lowestPrice) {
        return MathUtils.safeDivision(currentPrice - lowestPrice, lowestPrice) * ONE_HUNDRED_PERCENT;
    }

    /**
     * Indicates how much % the price needs to increase to recover the initial investment (if at a loss).
     */
    public static double breakEvenPercentage(double buyPrice, double currentPrice) {
        return MathUtils.safeDivision(buyPrice - currentPrice, buyPrice) * ONE_HUNDRED_PERCENT;
    }

    /**
     * Calculates the total realized profit from provided transactions
     */
    public static double getTransactionsRealizedProfit(List<Transaction> transactions) {
        double totalCost = 0.0;
        double remainingQuantity = 0.0;
        double realizedProfit = 0.0;

        for (Transaction transaction : transactions) {
            if (transaction.isBuyTransaction()) {
                totalCost += transaction.getOrderTotalCost();
                remainingQuantity += transaction.getOrderQuantity();
            } else {
                double sellQuantity = transaction.getOrderQuantity();
                if (sellQuantity > remainingQuantity) {
                    throw new IllegalArgumentException("Selling more than owned");
                }

                // Calculate proportional cost of sold tokens
                double averageCostPerUnit = MathUtils.safeDivision(totalCost, remainingQuantity);
                double costOfSoldTokens = averageCostPerUnit * sellQuantity;
                realizedProfit += transaction.getOrderTotalCost() - costOfSoldTokens;

                // Update remaining portfolio cost and quantity
                totalCost -= costOfSoldTokens;
                remainingQuantity -= sellQuantity;
            }
        }

        return realizedProfit;
    }


}