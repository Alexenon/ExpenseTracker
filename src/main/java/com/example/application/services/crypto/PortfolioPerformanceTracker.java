package com.example.application.services.crypto;

import com.example.application.data.dtos.AssetDTO;
import com.example.application.data.dtos.PortfolioDTO;
import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.AssetBalance;
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
    public double getAssetWorth(PortfolioDTO portfolio, AssetDTO asset) {
        return Optional.ofNullable(asset)
                .map(a -> instrumentsFacadeService.getAmountOfTokens(portfolio, asset) * a.getMarketPrice())
                .orElse(Double.NaN);
    }

    public double getAverageBuyPrice(PortfolioDTO portfolio, AssetDTO asset) {
        return Optional.ofNullable(asset)
                .flatMap(a -> instrumentsFacadeService.getAssetBalanceByAsset(portfolio, asset))
                .map(AssetBalance::getAvgBuyPrice)
                .orElse(Double.NaN);
    }

    public double getAverageSellPrice(PortfolioDTO portfolio, AssetDTO asset) {
        return Optional.ofNullable(asset)
                .flatMap(a -> instrumentsFacadeService.getAssetBalanceByAsset(portfolio, asset))
                .map(AssetBalance::getAvgSellPrice)
                .orElse(Double.NaN);
    }

    public double getAssetRemainingTokensCost(PortfolioDTO portfolio, AssetDTO asset) {
        return Optional.ofNullable(asset)
                .flatMap(a -> instrumentsFacadeService.getAssetBalanceByAsset(portfolio, asset))
                .map(AssetBalance::getCost)
                .orElse(Double.NaN);
    }

    public double getAssetRealizedProfit(PortfolioDTO portfolio, AssetDTO asset) {
        return Optional.ofNullable(asset)
                .flatMap(a -> instrumentsFacadeService.getAssetBalanceByAsset(portfolio, asset))
                .map(AssetBalance::getTotalRealized)
                .orElse(Double.NaN);
    }

    public double getAssetTotalProfit(PortfolioDTO portfolio, AssetDTO asset) {
        return getAssetRealizedProfit(portfolio, asset) + getAssetUnrealizedProfit(portfolio, asset);
    }

    public double getAssetUnrealizedProfit(PortfolioDTO portfolio, AssetDTO asset) {
        return getAssetWorth(portfolio, asset) - getAssetRemainingTokensCost(portfolio, asset);
    }

    public double getAssetNetProfitPercentage(PortfolioDTO portfolio, AssetDTO asset) {
        return MathUtils.safeDivision(getAssetWorth(portfolio, asset), getAssetRemainingTokensCost(portfolio, asset));
    }

    public String getAssetBuySellRatio(PortfolioDTO portfolio, AssetDTO asset) {
        return ProfitCalculator.buySellRatio(instrumentsFacadeService.getTransactionsByAsset(portfolio.getId(), asset.getSymbol()));
    }

    public double getAssetHoldingDays(PortfolioDTO portfolio, AssetDTO asset) {
        return instrumentsFacadeService.getAssetBalanceByAsset(portfolio, asset)
                .map(AssetBalance::getHoldingDays)
                .orElse(Double.NaN);
    }

    /**
     * @return the asset diversity percentage in the portfolio, range (0 - 100)%
     */
    public int getAssetDiversityPercentage(PortfolioDTO portfolio, AssetDTO asset) {
        return Math.toIntExact(Math.round(getAssetWorth(portfolio, asset) / getPortfolioWorth(portfolio) * ONE_HUNDRED_PERCENT));
    }
    //endregion

    //region PORTFOLIO STATS

    /**
     * How much was invested in all holding assets at this moment
     */
    public double getPortfolioCost(PortfolioDTO portfolio) {
        return instrumentsFacadeService.getAssetBalances(portfolio)
                .stream()
                .mapToDouble(AssetBalance::getCost)
                .sum();
    }

    /**
     * How much is estimated the worth of all holding assets (Overall Unrealized profit)
     */
    public double getPortfolioWorth(PortfolioDTO portfolio) {
        return instrumentsFacadeService.getAssetBalances(portfolio)
                .stream()
                .mapToDouble(balance -> balance.getAmount() * balance.getAsset().getMarketPrice())
                .sum();
    }

    public double getPortfolioUnrealizedProfit(PortfolioDTO portfolio) {
        return getPortfolioWorth(portfolio);
    }

    public double getPortfolioRealizedProfit(PortfolioDTO portfolio) {
        return instrumentsFacadeService.getAssetBalances(portfolio)
                .stream()
                .mapToDouble(AssetBalance::getTotalRealized)
                .sum();
    }

    /**
     * Represents the total profit, realized + unrealized
     */
    public double getPortfolioTotalProfit(PortfolioDTO portfolio) {
        return getPortfolioUnrealizedProfit(portfolio) + getPortfolioRealizedProfit(portfolio);
    }

    /**
     * @return average number of days holding across all assets
     * */
    public double getPortfolioAverageHoldingDays(PortfolioDTO portfolio) {
        return instrumentsFacadeService.getAllAssetsEverBought(portfolio)
                .stream()
                .mapToDouble(a -> getAssetHoldingDays(portfolio, a))
                .average()
                .orElse(0);
    }

    public String getPortfolioBuySellRatio(PortfolioDTO portfolio) {
        return ProfitCalculator.buySellRatio(instrumentsFacadeService.getTransactions(portfolio));
    }

    public double getPortfolioProfitPercentage(PortfolioDTO portfolio) {
        return getPortfolioWorth(portfolio) / getPortfolioCost(portfolio) * ONE_HUNDRED_PERCENT - ONE_HUNDRED_PERCENT;
    }

    public Map<Asset, Double> getMostProfitableAssetsByProfit(PortfolioDTO portfolio) {
        return instrumentsFacadeService.getAssetBalances(portfolio)
                .stream()
                .map(AssetBalance::getAsset)
                .collect(Collectors.toMap(asset -> asset,
                        asset -> getAssetTotalProfit(portfolio, asset),
                        (a, b) -> b));
    }
    //endregion

}
