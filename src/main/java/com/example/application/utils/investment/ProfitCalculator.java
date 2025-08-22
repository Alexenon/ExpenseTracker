package com.example.application.utils.investment;

import com.example.application.entities.crypto.CryptoTransaction;
import com.example.application.utils.common.lang.DateUtils;
import com.example.application.utils.common.lang.MathUtils;
import com.example.application.utils.common.lang.NumberUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static com.example.application.utils.investment.ProfitUtils.ONE_HUNDRED_PERCENT;

/**
 * Calculates profit and other metrics based on the all transactions that happened <br>
 * FIFO implementation (first in first out)
 */
public class ProfitCalculator {

	public static double calculateNewAvgPrice(double prevAvg, double prevAmount, double newAmount, double buyPrice) {
		double totalValue = (prevAmount * prevAvg) + (newAmount * buyPrice);
		double totalAmount = NumberUtils.checkDouble(prevAmount + newAmount);
		return MathUtils.safeDivision(totalValue, totalAmount);
	}

	/**
	 * Calculates the realized profit based on average cost method.
	 *
	 * @param avgBuyPrice      The average price at which tokens were bought
	 * @param currentSellPrice The price per token in the current sell
	 * @param tokensSoldNow    The number of tokens being sold in the current sell
	 * @return The realized profit for the current sell transaction
	 */
	public static double calculateRealizedProfit(double avgBuyPrice, double currentSellPrice, double tokensSoldNow) {
		return (currentSellPrice - avgBuyPrice) * tokensSoldNow;
	}

	private static class Order {
		double quantity;
		double cost;

		Order(double quantity, double cost) {
			this.quantity = quantity;
			this.cost = cost;
		}
	}

	public static double getAverageBuyPrice(List<CryptoTransaction> transactions) {
		double totalCost = totalCostForBuyTransactions(transactions);
		double totalQuantity = totalQuantityForBuyTransactions(transactions);
		return calculateAveragePrice(totalCost, totalQuantity);
	}

	public static double getAverageSellPrice(List<CryptoTransaction> transactions) {
		double totalSellCost = totalCostForSellTransactions(transactions);
		double totalQuantitySold = totalQuantityForSellTransactions(transactions);
		return calculateAveragePrice(totalSellCost, totalQuantitySold);
	}

	/*
	 * -----------------------------------------------------------------
	 * | OLD IMPLEMENTATION - SHOULD BE REMOVED AFTER PROPERLY TESTING |
	 * -----------------------------------------------------------------
	 * */

	/**
	 * Calculates the total realized profit from provided transactions
	 *
	 * <p>If there are just BUYS transactions, then the value will be {@code 0}
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
		if (transactions == null || transactions.isEmpty())
			return "N/A";

		double buyCost = totalCostForBuyTransactions(transactions);
		double sellCost = totalCostForSellTransactions(transactions);
		double totalCost = buyCost + sellCost;

		double buyRatio = buyCost / totalCost * ONE_HUNDRED_PERCENT;
		double sellRatio = sellCost / totalCost * ONE_HUNDRED_PERCENT;
		return String.format("%d : %d", Math.round(buyRatio), Math.round(sellRatio));
	}

    //region CALCULATION METHODS
    public static double totalCostForBuyTransactions(List<CryptoTransaction> transactions) {
        return transactions.stream()
                .filter(CryptoTransaction::isBuyTransaction)
                .mapToDouble(CryptoTransaction::getOrderTotalCost)
                .sum();
    }

    public static double totalCostForSellTransactions(List<CryptoTransaction> transactions) {
        return transactions.stream()
                .filter(CryptoTransaction::isSellTransaction)
                .mapToDouble(CryptoTransaction::getOrderTotalCost)
                .sum();
    }

    public static double totalQuantityForBuyTransactions(List<CryptoTransaction> transactions) {
        return transactions.stream()
                .filter(CryptoTransaction::isBuyTransaction)
                .mapToDouble(CryptoTransaction::getOrderQuantity)
                .sum();
    }

    public static double totalQuantityForSellTransactions(List<CryptoTransaction> transactions) {
        return transactions.stream()
                .filter(CryptoTransaction::isSellTransaction)
                .mapToDouble(CryptoTransaction::getOrderQuantity)
                .sum();
    }

    public static double getAmountOfRemainingTokens(List<CryptoTransaction> transactions) {
        return totalQuantityForBuyTransactions(transactions) - totalQuantityForSellTransactions(transactions);
    }

    public static double calculateAveragePrice(double totalCost, double totalQuantity) {
        return MathUtils.safeDivision(totalCost, totalQuantity);
    }

    private static long getHoldingTimeInDays(CryptoTransaction buyTransaction, CryptoTransaction sellTransaction) {
        return DateUtils.daysBetween(buyTransaction.getDate(), sellTransaction.getDate());
    }

	//endregion

}
