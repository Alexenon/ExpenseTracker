package com.example.application.utils.investment;

import com.example.application.entities.crypto.CryptoTransaction;
import com.example.application.utils.common.MathUtils;

public class ProfitUtils {

    public static final int ONE_HUNDRED_PERCENT = 100;

    public static double coinsBought(double buyPrice, double investedAmount) {
        return MathUtils.safeZeroDivision(investedAmount, buyPrice);
    }

    public static double worth(double buyPrice, double investedAmount) {
        return coinsBought(buyPrice, investedAmount) * buyPrice;
    }

    public static double netProfit(double buyPrice, double sellPrice, double investedAmount) {
        return (sellPrice - buyPrice) * coinsBought(buyPrice, investedAmount);
    }

    public static double netProfit(CryptoTransaction transaction, double currentPrice) {
        return netProfit(transaction.getMarketPrice(), currentPrice, transaction.getOrderTotalCost());
    }

    // TODO: FIXME
    public static double totalProfitPercentage(double buyPrice, double sellPrice) {
        return (sellPrice - buyPrice) / buyPrice * ONE_HUNDRED_PERCENT;
    }
    public static double netProfitPercentage(double buyPrice, double sellPrice) {
        return (sellPrice - buyPrice) / buyPrice * ONE_HUNDRED_PERCENT;
    }

    public static double roi(double buyPrice, double sellPrice, double investedAmount) {
        return (worth(buyPrice, investedAmount) - investedAmount) / investedAmount * ONE_HUNDRED_PERCENT;
    }

    public static double roi(double netProfit, double investedCost) {
        return netProfit / investedCost * 100;
    }

    public static double buyPricePerUnit(double totalBuyPrice, double amountTokens) {
        return totalBuyPrice / amountTokens;
    }

    public static double sellPricePerUnit(double totalSellPrice, double amountTokens) {
        return totalSellPrice / amountTokens;
    }

    public static double profitPerUnit(double buyPrice, double sellPrice, int amohntTokens) {
        return buyPricePerUnit(buyPrice, amohntTokens) - sellPricePerUnit(sellPrice, amohntTokens);
    }

}