package com.example.application.utils.investment;

import com.example.application.data.models.NumberType;
import com.example.application.utils.common.MathUtils;

public class ProfitCalculator {

    public static void main(String[] args) {
        printResult("BTC", 60_100, 65_200, 500);
    }

    @SuppressWarnings("SameParameterValue")
    private static void printResult(String symbol, double assetBuyPrice, double assetSellPrice, double investedAmount) {
        double profitUsd = MathUtils.profit(assetBuyPrice, assetSellPrice, investedAmount);
        double profitPercentage = MathUtils.profitPercentage(assetBuyPrice, assetSellPrice);

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

}
