package com.example.application.services.crypto;

import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.CryptoTransaction;
import com.example.application.entities.crypto.WalletBalance;
import com.example.application.utils.common.lang.MathUtils;
import com.example.application.utils.investment.ProfitCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static com.example.application.utils.investment.ProfitUtils.ONE_HUNDRED_PERCENT;

/*
    TODO: [LONG TERM] -> Implement next methods
       - % in Market, how much tokens had been sold and how much are still holding
       - Cumulative Profit Loss
* */

@Service
public class PortfolioPerformanceTracker {

    private final InstrumentsFacadeService instrumentsFacadeService;

    @Autowired
    public PortfolioPerformanceTracker(InstrumentsFacadeService instrumentsFacadeService) {
        this.instrumentsFacadeService = instrumentsFacadeService;
    }

    //region ASSET STATS
    public double getAssetWorth(Asset asset) {
        return Optional.ofNullable(asset)
                .map(a -> instrumentsFacadeService.getAmountOfTokens(a) * a.getMarketPrice())
                .orElse(Double.NaN);
    }

    public double getAverageBuyPrice(Asset asset) {
		return Optional.ofNullable(asset)
                .map(a -> instrumentsFacadeService.getWalletBalanceByAsset(a).getAvgBuyPrice())
                .orElse(Double.NaN);
    }

    public double getAverageSellPrice(Asset asset) {
        return Optional.ofNullable(asset)
                .map(a -> instrumentsFacadeService.getWalletBalanceByAsset(a).getAvgSellPrice())
                .orElse(Double.NaN);
    }

    public double getAssetRemainingTokensCost(Asset asset) {
        return Optional.ofNullable(asset)
                .map(a -> instrumentsFacadeService.getWalletBalanceByAsset(a).getCost())
                .orElse(Double.NaN);
    }

    public double getAssetRealizedProfit(Asset asset) {
        return Optional.ofNullable(asset)
                .map(a -> instrumentsFacadeService.getWalletBalanceByAsset(a).getTotalRealized())
                .orElse(Double.NaN);
    }

    public double getAssetCost(Asset asset) {
        return Optional.ofNullable(asset)
                .map(a -> instrumentsFacadeService.getWalletBalanceByAsset(a).getCost())
                .orElse(Double.NaN);
    }

    public double getAssetTotalHistoryCost(Asset asset) {
        return Optional.ofNullable(asset)
                .map(a -> ProfitCalculator.totalCostForBuyTransactions(instrumentsFacadeService.getTransactionsByAsset(a)))
                .orElse(Double.NaN);
    }

    public double getAssetTotalProfit(Asset asset) {
        return getAssetRealizedProfit(asset) + getAssetUnrealizedProfit(asset);
    }

    public double getAssetUnrealizedProfit(Asset asset) {
        return getAssetWorth(asset) - getAssetRemainingTokensCost(asset);
    }

    public double getAssetNetProfitPercentage(Asset asset) {
        return MathUtils.safeDivision(getAssetWorth(asset), getAssetRemainingTokensCost(asset));
    }

    public String getAssetBuySellRatio(Asset asset) {
        return ProfitCalculator.getBuySellRatio(instrumentsFacadeService.getTransactionsByAsset(asset));
    }

	// TODO:
	//  - HERE IS NOT IMPLEMENTED
	//  - Add last holding details + average holding days
    public double getAssetAverageHoldingDays(Asset asset) {
		return instrumentsFacadeService.getWalletBalanceByAsset(asset).getHoldingDays();
    }

    /**
     * @return the asset diversity percentage in the portfolio, range (0 - 100)%
     */
    public int getAssetDiversityPercentage(Asset asset) {
        return Math.toIntExact(Math.round(getAssetWorth(asset) / getPortfolioWorth() * ONE_HUNDRED_PERCENT));
    }
    //endregion

    //region PORTFOLIO STATS
    private long getHoldingTimeInDays(CryptoTransaction buyTransaction, CryptoTransaction sellTransaction) {
        return getHoldingTimeInDays(buyTransaction.getDate(), sellTransaction.getDate());
    }

    private long getHoldingTimeInDays(LocalDate buyDate, LocalDate sellDate) {
        return ChronoUnit.DAYS.between(buyDate, sellDate);
    }

    /**
     * How much was invested in all holding assets at this moment
     */
    public double getPortfolioCost() {
        return instrumentsFacadeService.getWalletBalances()
                .stream()
                .mapToDouble(WalletBalance::getCost)
                .sum();
    }

    /**
     * How much is estimated the worth of all holding assets (Overall Unrealized profit)
     */
    public double getPortfolioWorth() {
        return instrumentsFacadeService.getWalletBalances()
                .stream()
                .mapToDouble(balance -> balance.getAmount() * balance.getAsset().getMarketPrice())
                .sum();
    }

    public double getPortfolioUnrealizedProfit() {
        return getPortfolioWorth();
    }

    public double getPortfolioRealizedProfit() {
        return instrumentsFacadeService.getWalletBalances()
                .stream()
                .mapToDouble(WalletBalance::getTotalRealized)
                .sum();
    }

    /**
     * Represents the total profit, realized + unrealized
     */
    public double getPortfolioTotalProfit() {
        return getPortfolioUnrealizedProfit() + getPortfolioRealizedProfit();
    }

    public double getPortfolioAverageHoldingDays() {
        return instrumentsFacadeService.getAllAssetsEverBought()
                .stream()
                .mapToDouble(this::getAssetAverageHoldingDays)
                .average()
                .orElse(0);
    }

    public String getPortfolioBuySellRatio() {
        return ProfitCalculator.getBuySellRatio(instrumentsFacadeService.getAllTransactions());
    }

    public double getPortfolioProfitPercentage() {
        return getPortfolioWorth() / getPortfolioCost() * ONE_HUNDRED_PERCENT - ONE_HUNDRED_PERCENT;
    }
    //endregion

}
