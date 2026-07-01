package com.example.application.utils.investment;

import com.example.application.data.dtos.TransactionDTO;
import com.example.application.finance.FinancialConstants;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class ProfitUtils {

	public static final BigDecimal ONE_HUNDRED_PERCENT = new BigDecimal("100");

	public static BigDecimal coinsBought(BigDecimal buyPrice, BigDecimal investedAmount) {
		if(buyPrice.signum() == 0)
			return BigDecimal.ZERO;

		return investedAmount.divide(buyPrice, FinancialConstants.AMOUNT_SCALE, RoundingMode.HALF_UP);
	}

	public static BigDecimal worth(BigDecimal buyPrice, BigDecimal investedAmount) {
		return coinsBought(buyPrice, investedAmount)
				.multiply(buyPrice);
	}

	public static BigDecimal netProfit(BigDecimal buyPrice, BigDecimal sellPrice, BigDecimal investedAmount) {
		BigDecimal coinsBought = coinsBought(buyPrice, investedAmount);
		BigDecimal difference = sellPrice.subtract(buyPrice);
		return difference.multiply(coinsBought);
	}

	public static BigDecimal netProfit(TransactionDTO transaction, BigDecimal currentPrice) {
		return netProfit(transaction.getMarketPrice(), currentPrice, transaction.getOrderTotalCost());
	}

	public static BigDecimal netProfitPerToken(BigDecimal totalProfit, BigDecimal amountOfTokens) {
		if(amountOfTokens.signum() == 0)
			return BigDecimal.ZERO;

		return totalProfit.divide(amountOfTokens, FinancialConstants.PRICE_SCALE, RoundingMode.HALF_UP);
	}

	/**
	 * @return percentage profit relative to the investment amount (monetary perspective)
	 */
	public static BigDecimal profitPercentage(BigDecimal buyPrice, BigDecimal sellPrice, BigDecimal investedAmount) {
		if(investedAmount.signum() == 0)
			return BigDecimal.ZERO;

		return netProfit(buyPrice, sellPrice, investedAmount)
				.divide(investedAmount, 2, RoundingMode.HALF_UP)
				.multiply(ONE_HUNDRED_PERCENT);
	}

	/**
	 * @return percentage increase or decrease in the price of the asset (token price perspective)
	 */
	public static BigDecimal growthPercentage(BigDecimal buyPrice, BigDecimal sellPrice) {
		if(buyPrice.signum() == 0)
			return BigDecimal.ZERO;

		return sellPrice
				.subtract(buyPrice)
				.divide(buyPrice, 2, RoundingMode.HALF_UP)
				.multiply(ONE_HUNDRED_PERCENT);
	}

	public static BigDecimal buyPricePerUnit(BigDecimal totalBuyPrice, BigDecimal amountTokens) {
		if(amountTokens.signum() == 0)
			return BigDecimal.ZERO;

		return totalBuyPrice.divide(amountTokens, FinancialConstants.PRICE_SCALE, RoundingMode.HALF_UP);
	}

	public static BigDecimal sellPricePerUnit(BigDecimal totalSellPrice, BigDecimal amountTokens) {
		if(amountTokens.signum() == 0)
			return BigDecimal.ZERO;

		return totalSellPrice.divide(amountTokens, FinancialConstants.PRICE_SCALE, RoundingMode.HALF_UP);
	}

	public static BigDecimal profitPerUnit(BigDecimal buyPrice, BigDecimal sellPrice, BigDecimal amountTokens) {
		BigDecimal buyPricePerUnit = buyPricePerUnit(buyPrice, amountTokens);
		BigDecimal sellPricePerUnit = sellPricePerUnit(sellPrice, amountTokens);
		return buyPricePerUnit.subtract(sellPricePerUnit);
	}

	public static BigDecimal marketCap(BigInteger circulationSupply, BigDecimal tokenPrice) {
		return new BigDecimal(circulationSupply).multiply(tokenPrice);
	}

	public static BigDecimal fdv(BigInteger totalSupply, BigDecimal tokenPrice) {
		return new BigDecimal(totalSupply).multiply(tokenPrice);
	}

}