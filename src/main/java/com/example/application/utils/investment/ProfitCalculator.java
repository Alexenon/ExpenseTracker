package com.example.application.utils.investment;

import com.example.application.data.models.NumberType;

public class ProfitCalculator {

    public static void main(String[] args) {
        printResult("BTC", 60_100, 65_200, 500);
    }

    @SuppressWarnings("SameParameterValue")
    private static void printResult(String symbol, double assetBuyPrice, double assetSellPrice, double investedAmount) {
        double profitUsd = profit(assetBuyPrice, assetSellPrice, investedAmount);
        double profitPercentage = profitPercentage(assetBuyPrice, assetSellPrice);

        System.out.printf("""
                        Invested in %s $%.0f
                        Buy Price: %s
                        Sell Price: %s
                        Profit: %s ~ %.1f%%
                        """, symbol, investedAmount,
                NumberType.PRICE.parse(assetBuyPrice),
                NumberType.PRICE.parse(assetSellPrice),
                NumberType.PRICE.parse(profitUsd), profitPercentage);
        System.out.println();
    }

    public static double profit(double assetBuyPrice, double assetSellPrice, double investedAmount) {
        double assetQuantity = investedAmount / assetBuyPrice;
        return assetQuantity * (assetSellPrice - assetBuyPrice);
    }

    public static double profitPercentage(double assetBuyPrice, double assetSellPrice) {
        return ((assetSellPrice - assetBuyPrice) / assetBuyPrice) * 100;
    }
}
