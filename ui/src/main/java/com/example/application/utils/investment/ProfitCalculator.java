package com.example.application.utils.investment;

import com.example.application.transaction.TransactionDTO;
import com.example.application.utils.FinancialConstants;
import com.example.application.utils.lang.DateUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static com.example.application.utils.investment.ProfitUtils.ONE_HUNDRED_PERCENT;

/**
 * Calculates profit and other metrics based on the all transactions that happened <br>
 * FIFO implementation (first in first out)
 */
public class ProfitCalculator {

	public static BigDecimal calculateNewAvgPrice(BigDecimal prevAvg, BigDecimal prevAmount, BigDecimal newAmount, BigDecimal buyPrice) {
		BigDecimal prevCost = prevAmount.multiply(prevAvg);
		BigDecimal newCost = newAmount.multiply(buyPrice);
		BigDecimal totalValue = prevCost.add(newCost);
		BigDecimal totalAmount = prevAmount.add(newAmount);
		return totalAmount.signum() == 0
				? BigDecimal.ZERO
				: totalValue.divide(totalAmount, FinancialConstants.PRICE_SCALE, RoundingMode.HALF_UP);
	}

	/**
	 * Calculates the realized profit based on average cost method.
	 *
	 * @param avgBuyPrice      The average price at which tokens were bought
	 * @param currentSellPrice The price per token in the current sell
	 * @param tokensSoldNow    The number of tokens being sold in the current sell
	 * @return The realized profit for the current sell transaction
	 */
	public static BigDecimal calculateRealizedProfit(BigDecimal avgBuyPrice, BigDecimal currentSellPrice, BigDecimal tokensSoldNow) {
		return currentSellPrice
				.subtract(avgBuyPrice)
				.multiply(tokensSoldNow);
	}

	public static BigDecimal averageBuyPrice(List<TransactionDTO> transactions) {
		BigDecimal totalCost = totalCostForBuyTransactions(transactions);
		BigDecimal totalQuantity = totalQuantityForBuyTransactions(transactions);
		return calculateAveragePrice(totalCost, totalQuantity);
	}

	public static BigDecimal averageSellPrice(List<TransactionDTO> transactions) {
		BigDecimal totalSellCost = totalCostForSellTransactions(transactions);
		BigDecimal totalQuantitySold = totalQuantityForSellTransactions(transactions);
		return calculateAveragePrice(totalSellCost, totalQuantitySold);
	}

	/*
	 * -----------------------------------------------------------------
	 * | OLD IMPLEMENTATION - SHOULD BE REMOVED AFTER PROPERLY TESTING |
	 * -----------------------------------------------------------------
	 * */

	private static class Order {
		BigDecimal quantity;
		BigDecimal cost;

		Order(BigDecimal quantity, BigDecimal cost) {
			this.quantity = quantity;
			this.cost = cost;
		}
	}

	// FIXME: TODO: [URGENT] HERE IS SOMETHING STRANGE

	/**
	 * Calculates the total realized profit from provided transactions
	 *
	 * <p>If there are just BUYS transactions, then the value will be {@code 0}
	 */
	public static BigDecimal realizedProfit(List<TransactionDTO> transactions) {
		BigDecimal totalCost = BigDecimal.ZERO;
		BigDecimal remainingQuantity = BigDecimal.ZERO;
		BigDecimal realizedProfit = BigDecimal.ZERO;

		for (TransactionDTO transaction : transactions) {
			if (transaction.isBuyTransaction()) {
				totalCost = totalCost.add(transaction.getOrderTotalCost());
				remainingQuantity = remainingQuantity.add(transaction.getOrderQuantity());
			} else {
				BigDecimal sellQuantity = transaction.getOrderQuantity();

				if (sellQuantity.compareTo(remainingQuantity) > 0) {
					throw new IllegalArgumentException("Selling more than owned");
				}

				BigDecimal averageCostPerUnit = remainingQuantity.signum() == 0
						? BigDecimal.ZERO
						: totalCost.divide(remainingQuantity, FinancialConstants.PRICE_SCALE, RoundingMode.HALF_UP);

				BigDecimal costOfSoldTokens =
						averageCostPerUnit.multiply(sellQuantity);

				realizedProfit = realizedProfit
						.add(transaction.getOrderTotalCost()
								.subtract(costOfSoldTokens));

				totalCost = totalCost.subtract(costOfSoldTokens);
				remainingQuantity = remainingQuantity.subtract(sellQuantity);
			}
		}

		return realizedProfit;
	}

	/**
	 * Calculates the cost of remaining tokens, where if contains SELL transactions, then the cost value
	 * is substracted confirming with transaction order cost
	 */
	public static BigDecimal remainingTokensCost(List<TransactionDTO> transactions) {
		Queue<Order> fifoQueue = new LinkedList<>();
		BigDecimal remainingCost = BigDecimal.ZERO;

		for (TransactionDTO transaction : transactions) {
			if (transaction.isBuyTransaction()) {
				Order newOrder = new Order(
						transaction.getOrderQuantity(),
						transaction.getOrderTotalCost()
				);
				fifoQueue.offer(newOrder);
				remainingCost = remainingCost.add(transaction.getOrderTotalCost());
			} else {
				BigDecimal sellQuantity = transaction.getOrderQuantity();
				BigDecimal sellCost = BigDecimal.ZERO;

				while (sellQuantity.compareTo(BigDecimal.ZERO) > 0) {
					if (fifoQueue.isEmpty()) {
						throw new IllegalArgumentException("Selling more than owned");
					}

					Order oldestOrder = fifoQueue.peek();

					if (sellQuantity.compareTo(oldestOrder.quantity) >= 0) {
						sellCost = sellCost.add(oldestOrder.cost);
						sellQuantity = sellQuantity.subtract(oldestOrder.quantity);
						fifoQueue.poll();
					} else {
						BigDecimal oldestQuantity = oldestOrder.quantity;
						BigDecimal proportion = oldestQuantity.signum() == 0
								? BigDecimal.ZERO
								: sellQuantity.divide(oldestQuantity, FinancialConstants.AMOUNT_SCALE, RoundingMode.HALF_UP);

						BigDecimal partialCost = oldestOrder.cost.multiply(proportion);

						sellCost = sellCost.add(partialCost);

						oldestOrder.cost = oldestOrder.cost.subtract(partialCost);
						oldestOrder.quantity = oldestOrder.quantity.subtract(sellQuantity);

						sellQuantity = BigDecimal.ZERO;
					}
				}

				remainingCost = remainingCost.subtract(sellCost);
			}
		}

		return remainingCost;
	}

	public static String buySellRatio(List<TransactionDTO> transactions) {
		if (transactions == null || transactions.isEmpty())
			return "N/A";

		// TODO: Optimize this from database directly
		BigDecimal buyCost = totalCostForBuyTransactions(transactions);
		BigDecimal sellCost = totalCostForSellTransactions(transactions);
		BigDecimal totalCost = buyCost.add(sellCost);

		if (totalCost.signum() == 0)
			return buyCost.compareTo(sellCost) == 0 ? "50 : 50" : "0 : 0";

		BigDecimal buyRatio = buyCost
				.divide(totalCost, 0, RoundingMode.HALF_UP)
				.multiply(ONE_HUNDRED_PERCENT)
				.stripTrailingZeros();

		BigDecimal sellRatio = sellCost
				.divide(totalCost, 0, RoundingMode.HALF_UP)
				.multiply(ONE_HUNDRED_PERCENT)
				.stripTrailingZeros();

		return String.format("%f : %f", buyRatio, sellRatio);
	}

	//region CALCULATION METHODS
	public static BigDecimal totalCostForBuyTransactions(List<TransactionDTO> transactions) {
		return transactions.stream()
				.filter(TransactionDTO::isBuyTransaction)
				.map(TransactionDTO::getOrderTotalCost)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	public static BigDecimal totalCostForSellTransactions(List<TransactionDTO> transactions) {
		return transactions.stream()
				.filter(TransactionDTO::isSellTransaction)
				.map(TransactionDTO::getOrderTotalCost)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	public static BigDecimal totalQuantityForBuyTransactions(List<TransactionDTO> transactions) {
		return transactions.stream()
				.filter(TransactionDTO::isBuyTransaction)
				.map(TransactionDTO::getOrderQuantity)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	public static BigDecimal totalQuantityForSellTransactions(List<TransactionDTO> transactions) {
		return transactions.stream()
				.filter(TransactionDTO::isSellTransaction)
				.map(TransactionDTO::getOrderQuantity)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	public static BigDecimal getAmountOfRemainingTokens(List<TransactionDTO> transactions) {
		BigDecimal boughtAmount = totalQuantityForBuyTransactions(transactions);
		BigDecimal soldAmount = totalQuantityForSellTransactions(transactions);
		return boughtAmount.subtract(soldAmount);
	}

	public static BigDecimal calculateAveragePrice(BigDecimal totalCost, BigDecimal totalQuantity) {
		if (totalQuantity.signum() == 0)
			return BigDecimal.ZERO;

		return totalCost.divide(totalQuantity, FinancialConstants.PRICE_SCALE, RoundingMode.HALF_UP);
	}

	private static long getHoldingTimeInDays(TransactionDTO buyTransaction, TransactionDTO sellTransaction) {
		return DateUtils.daysBetween(buyTransaction.getDateTime(), sellTransaction.getDateTime());
	}
	//endregion

}
