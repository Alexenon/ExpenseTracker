package com.example.application.services.crypto;

import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.AssetBalance;
import com.example.application.entities.crypto.Portfolio;
import com.example.application.utils.common.lang.MathUtils;
import com.example.application.utils.investment.ProfitCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.application.utils.investment.ProfitUtils.ONE_HUNDRED_PERCENT;

/*
    TODO: [LONG TERM] -> Implement next methods
       - % in Market, how much tokens had been sold and how much are still holding
       - Cumulative Profit Loss
       - Display long term information, like how many assets were bought/sold, totalProfit realized ever...
* */

@Service
public class PortfolioPerformanceTracker {

    private final InstrumentsFacadeService instrumentsFacadeService;

    @Autowired
    public PortfolioPerformanceTracker(InstrumentsFacadeService instrumentsFacadeService) {
        this.instrumentsFacadeService = instrumentsFacadeService;
    }

    //region ASSET STATS
    public double getAssetWorth(Portfolio portfolio, Asset asset) {
        return Optional.ofNullable(asset)
                .map(a -> instrumentsFacadeService.getAmountOfTokens(portfolio, asset) * a.getMarketPrice())
                .orElse(Double.NaN);
    }

    public double getAverageBuyPrice(Portfolio portfolio, Asset asset) {
        return Optional.ofNullable(asset)
                .flatMap(a -> instrumentsFacadeService.getAssetBalanceByAsset(portfolio, asset))
                .map(AssetBalance::getAvgBuyPrice)
                .orElse(Double.NaN);
    }

    public double getAverageSellPrice(Portfolio portfolio, Asset asset) {
        return Optional.ofNullable(asset)
                .flatMap(a -> instrumentsFacadeService.getAssetBalanceByAsset(portfolio, asset))
                .map(AssetBalance::getAvgSellPrice)
                .orElse(Double.NaN);
    }

    public double getAssetRemainingTokensCost(Portfolio portfolio, Asset asset) {
        return Optional.ofNullable(asset)
                .flatMap(a -> instrumentsFacadeService.getAssetBalanceByAsset(portfolio, asset))
                .map(AssetBalance::getCost)
                .orElse(Double.NaN);
    }

    public double getAssetRealizedProfit(Portfolio portfolio, Asset asset) {
        return Optional.ofNullable(asset)
                .flatMap(a -> instrumentsFacadeService.getAssetBalanceByAsset(portfolio, asset))
                .map(AssetBalance::getTotalRealized)
                .orElse(Double.NaN);
    }

    public double getAssetTotalProfit(Portfolio portfolio, Asset asset) {
        return getAssetRealizedProfit(portfolio, asset) + getAssetUnrealizedProfit(portfolio, asset);
    }

    public double getAssetUnrealizedProfit(Portfolio portfolio, Asset asset) {
        return getAssetWorth(portfolio, asset) - getAssetRemainingTokensCost(portfolio, asset);
    }

    public double getAssetNetProfitPercentage(Portfolio portfolio, Asset asset) {
        return MathUtils.safeDivision(getAssetWorth(portfolio, asset), getAssetRemainingTokensCost(portfolio, asset));
    }

    public String getAssetBuySellRatio(Portfolio portfolio, Asset asset) {
        return ProfitCalculator.buySellRatio(instrumentsFacadeService.getTransactionsByAsset(portfolio, asset));
    }

    public double getAssetHoldingDays(Portfolio portfolio, Asset asset) {
        return instrumentsFacadeService.getAssetBalanceByAsset(portfolio, asset)
                .map(AssetBalance::getHoldingDays)
                .orElse(Double.NaN);
    }

    /**
     * @return the asset diversity percentage in the portfolio, range (0 - 100)%
     */
    public int getAssetDiversityPercentage(Portfolio portfolio, Asset asset) {
        return Math.toIntExact(Math.round(getAssetWorth(portfolio, asset) / getPortfolioWorth(portfolio) * ONE_HUNDRED_PERCENT));
    }
    //endregion

    //region PORTFOLIO STATS

    /**
     * How much was invested in all holding assets at this moment
     */
    public double getPortfolioCost(Portfolio portfolio) {
        return instrumentsFacadeService.getAssetBalances(portfolio)
                .stream()
                .mapToDouble(AssetBalance::getCost)
                .sum();
    }

    /**
     * How much is estimated the worth of all holding assets (Overall Unrealized profit)
     */
    public double getPortfolioWorth(Portfolio portfolio) {
        return instrumentsFacadeService.getAssetBalances(portfolio)
                .stream()
                .mapToDouble(balance -> balance.getAmount() * balance.getAsset().getMarketPrice())
                .sum();
    }

    public double getPortfolioUnrealizedProfit(Portfolio portfolio) {
        return getPortfolioWorth(portfolio);
    }

    public double getPortfolioRealizedProfit(Portfolio portfolio) {
        return instrumentsFacadeService.getAssetBalances(portfolio)
                .stream()
                .mapToDouble(AssetBalance::getTotalRealized)
                .sum();
    }

    /**
     * Represents the total profit, realized + unrealized
     */
    public double getPortfolioTotalProfit(Portfolio portfolio) {
        return getPortfolioUnrealizedProfit(portfolio) + getPortfolioRealizedProfit(portfolio);
    }

    /**
     * @return average number of days holding across all assets
     * */
    public double getPortfolioAverageHoldingDays(Portfolio portfolio) {
        return instrumentsFacadeService.getAllAssetsEverBought(portfolio)
                .stream()
                .mapToDouble(a -> getAssetHoldingDays(portfolio, a))
                .average()
                .orElse(0);
    }

    public String getPortfolioBuySellRatio(Portfolio portfolio) {
        return ProfitCalculator.buySellRatio(instrumentsFacadeService.getTransactions(portfolio));
    }

    public double getPortfolioProfitPercentage(Portfolio portfolio) {
        return getPortfolioWorth(portfolio) / getPortfolioCost(portfolio) * ONE_HUNDRED_PERCENT - ONE_HUNDRED_PERCENT;
    }

    public Map<Asset, Double> getMostProfitableAssetsByProfit(Portfolio portfolio) {
        return instrumentsFacadeService.getAssetsWithNonZeroAmount(portfolio)
                .stream()
                .map(AssetBalance::getAsset)
                .collect(Collectors.toMap(asset -> asset,
                        asset -> getAssetTotalProfit(portfolio, asset),
                        (a, b) -> b));
    }
    //endregion

}
