package com.example.application.services.crypto;

import com.example.application.data.dtos.AssetBalanceDTO;
import com.example.application.data.dtos.AssetDTO;
import com.example.application.data.dtos.PortfolioDTO;
import com.example.application.finance.FinancialConstants;
import com.example.application.utils.investment.ProfitCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.application.utils.investment.ProfitUtils.ONE_HUNDRED_PERCENT;

/*
	TODO: [CRITICAL] EXTREME
		- After updating a transaction, totalCost doesnt display value right
*/


@Service
public class PortfolioPerformanceTracker {

	private final InstrumentsFacadeService instrumentsFacadeService;

	@Autowired
	public PortfolioPerformanceTracker(InstrumentsFacadeService instrumentsFacadeService) {
		this.instrumentsFacadeService = instrumentsFacadeService;
	}

	//region ASSET STATS
	public BigDecimal getAssetWorth(PortfolioDTO portfolio, AssetDTO asset) {
		return Optional.ofNullable(asset)
				.map(a -> instrumentsFacadeService.getAmountOfTokens(portfolio.getId(), asset.getSymbol()).multiply(a.getMarketPrice()))
				.orElse(BigDecimal.ZERO);
	}

	public Optional<BigDecimal> getAverageBuyPrice(PortfolioDTO portfolio, AssetDTO asset) {
		return Optional.ofNullable(asset)
				.flatMap(a -> instrumentsFacadeService.getAssetBalanceByAsset(portfolio.getId(), asset.getSymbol()))
				.map(AssetBalanceDTO::getAvgBuyPrice);
	}

	public Optional<BigDecimal> getAverageSellPrice(PortfolioDTO portfolio, AssetDTO asset) {
		return Optional.ofNullable(asset)
				.flatMap(a -> instrumentsFacadeService.getAssetBalanceByAsset(portfolio.getId(), asset.getSymbol()))
				.map(AssetBalanceDTO::getAvgSellPrice);
	}

	public Optional<BigDecimal> getAssetRemainingTokensCost(PortfolioDTO portfolio, AssetDTO asset) {
		return Optional.ofNullable(asset)
				.flatMap(a -> instrumentsFacadeService.getAssetBalanceByAsset(portfolio.getId(), asset.getSymbol()))
				.map(AssetBalanceDTO::getCost);
	}

	public Optional<BigDecimal> getAssetRealizedProfit(PortfolioDTO portfolio, AssetDTO asset) {
		return Optional.ofNullable(asset)
				.flatMap(a -> instrumentsFacadeService.getAssetBalanceByAsset(portfolio.getId(), asset.getSymbol()))
				.map(AssetBalanceDTO::getTotalRealizedProfit);
	}

	public BigDecimal getAssetTotalProfit(PortfolioDTO portfolio, AssetDTO asset) {
		BigDecimal realizedProfit = getAssetRealizedProfit(portfolio, asset)
				.orElse(BigDecimal.ZERO);
		BigDecimal unrealizedProfit = getAssetUnrealizedProfit(portfolio, asset);
		return realizedProfit.add(unrealizedProfit);
	}

	public BigDecimal getAssetUnrealizedProfit(PortfolioDTO portfolio, AssetDTO asset) {
		BigDecimal worth = getAssetWorth(portfolio, asset);
		BigDecimal remainingTokensCost = getAssetRemainingTokensCost(portfolio, asset)
				.orElse(BigDecimal.ZERO);
		return worth.subtract(remainingTokensCost);
	}

	public BigDecimal getAssetNetProfitPercentage(PortfolioDTO portfolio, AssetDTO asset) {
		BigDecimal worth = getAssetWorth(portfolio, asset);
		BigDecimal remainingTokensCost = getAssetRemainingTokensCost(portfolio, asset)
				.orElse(BigDecimal.ZERO);
		return worth.divide(remainingTokensCost, FinancialConstants.PRICE_SCALE, RoundingMode.HALF_UP);
	}

	public String getAssetBuySellRatio(PortfolioDTO portfolio, AssetDTO asset) {
		return ProfitCalculator.buySellRatio(instrumentsFacadeService.getTransactionsByAsset(portfolio.getId(), asset.getSymbol()));
	}

	public double getAssetHoldingDays(PortfolioDTO portfolio, AssetDTO asset) {
		return instrumentsFacadeService.getAssetBalanceByAsset(portfolio.getId(), asset.getSymbol())
				.map(AssetBalanceDTO::getHoldingDays)
				.orElse(Double.NaN);
	}

	/**
	 * @return the asset diversity percentage in the portfolio, range (0 - 100)%
	 */
	public int getAssetDiversityPercentage(PortfolioDTO portfolio, AssetDTO asset) {
		BigDecimal totalPercentage = getAssetWorth(portfolio, asset)
				.divide(getPortfolioWorth(portfolio), 2, RoundingMode.HALF_UP);
		return totalPercentage.multiply(ONE_HUNDRED_PERCENT).intValue();
	}
	//endregion

	//region PORTFOLIO STATS

	/**
	 * How much was invested in all holding assets at this moment
	 */
	public BigDecimal getPortfolioCost(PortfolioDTO portfolio) {
		return instrumentsFacadeService.getPorfolioAssetBalances(portfolio.getId())
				.stream()
				.map(AssetBalanceDTO::getCost)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	/**
	 * How much is estimated the worth of all holding assets (Overall Unrealized profit)
	 */
	public BigDecimal getPortfolioWorth(PortfolioDTO portfolio) {
		return instrumentsFacadeService.getPorfolioAssetBalances(portfolio.getId())
				.stream()
				.map(balance -> {
					BigDecimal assetCurrentPrice = instrumentsFacadeService.getAssetBySymbol(balance.getAssetSymbol())
							.orElseThrow()
							.getMarketPrice();

					return balance.getAmount().multiply(assetCurrentPrice);
				})
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	public BigDecimal getPortfolioUnrealizedProfit(PortfolioDTO portfolio) {
		return getPortfolioWorth(portfolio);
	}

	public BigDecimal getPortfolioRealizedProfit(PortfolioDTO portfolio) {
		return instrumentsFacadeService.getPorfolioAssetBalances(portfolio.getId())
				.stream()
				.map(AssetBalanceDTO::getTotalRealizedProfit)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	/**
	 * Represents the total profit, realized + unrealized
	 */
	public BigDecimal getPortfolioTotalProfit(PortfolioDTO portfolio) {
		BigDecimal unrealizedProfit = getPortfolioUnrealizedProfit(portfolio);
		BigDecimal realizedProfit = getPortfolioRealizedProfit(portfolio);
		return unrealizedProfit.add(realizedProfit);
	}

	/**
	 * @return average number of days holding across all assets
	 */
	public double getPortfolioAverageHoldingDays(PortfolioDTO portfolio) {
		return instrumentsFacadeService.getAllAssetsEverBought(portfolio)
				.stream()
				.mapToDouble(a -> getAssetHoldingDays(portfolio, a))
				.average()
				.orElse(0);
	}

	public BigDecimal getPortfolioProfitPercentage(PortfolioDTO portfolio) {
		BigDecimal portfolioCost = getPortfolioCost(portfolio);

		if (portfolioCost.signum() == 0)
			return BigDecimal.ZERO;

		BigDecimal totalPercentage = getPortfolioWorth(portfolio)
				.divide(portfolioCost, FinancialConstants.PRICE_SCALE, RoundingMode.HALF_UP);

		return totalPercentage.multiply(ONE_HUNDRED_PERCENT)
				.subtract(ONE_HUNDRED_PERCENT);
	}

	public String getPortfolioBuySellRatio(PortfolioDTO portfolio) {
		return ProfitCalculator.buySellRatio(instrumentsFacadeService.getTransactions(portfolio.getId()));
	}

	public Map<AssetDTO, BigDecimal> getMostProfitableAssetsByProfit(PortfolioDTO portfolio) {
		return instrumentsFacadeService.getPorfolioAssetBalances(portfolio.getId())
				.stream()
				.map(AssetBalanceDTO::getAssetSymbol)
				.map(s -> instrumentsFacadeService.getAssetBySymbol(s).orElseThrow())
				.collect(Collectors.toMap(asset -> asset,
						asset -> getAssetTotalProfit(portfolio, asset),
						(a, b) -> b));
	}
	//endregion

}
