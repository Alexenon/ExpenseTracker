package com.example.application.services.crypto;

import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.AssetBalance;
import com.example.application.entities.crypto.Portfolio;
import com.example.application.entities.crypto.Transaction;
import com.example.application.repositories.crypto.AssetBalanceRepository;
import com.example.application.utils.common.lang.MathUtils;
import com.example.application.utils.common.lang.NumberUtils;
import com.example.application.utils.exceptions.InternalUnexpectedException;
import com.example.application.utils.exceptions.InvalidBalanceAmountException;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class AssetBalanceService {

	private final AssetBalanceRepository assetBalanceRepository;

	@Autowired
	public AssetBalanceService(AssetBalanceRepository repository)
	{
		this.assetBalanceRepository = repository;
	}

	public Optional<AssetBalance> findById(@NotNull Long assetBalanceId) {
		Objects.requireNonNull(assetBalanceId, "assetBalanceId");
		return assetBalanceRepository.findById(assetBalanceId);
	}

	/**
	 * @return list of {@link AssetBalance} that are currently holded in the provided portfolio.
	 */
	public List<AssetBalance> findByPortfolio(@NotNull Long portfolioId) {
		Objects.requireNonNull(portfolioId, "portfolioId");
		return assetBalanceRepository.findByPortfolio(portfolioId);
	}

	public Optional<AssetBalance> findByPortfolioAndAsset(@NotNull Long portfolioId, @NotNull String assetSymbol) {
		Objects.requireNonNull(portfolioId, "portfolioId");
		Objects.requireNonNull(assetSymbol, "assetSymbol");
		return assetBalanceRepository.findByPortfolioAndAsset(portfolioId, assetSymbol);
	}

	public Optional<AssetBalance> findByPortfolioAndAsset(@NotNull Long portfolioId, @NotNull Long assetId) {
		Objects.requireNonNull(portfolioId, "portfolioId");
		Objects.requireNonNull(assetId, "assetId");
		return assetBalanceRepository.findByPortfolioAndAsset(portfolioId, assetId);
	}

	@Transactional
	public AssetBalance createNew(@NotNull Portfolio portfolio, @NotNull Asset asset) {
		AssetBalance assetBalance = new AssetBalance();
		assetBalance.setPortfolio(portfolio);
		assetBalance.setAsset(asset);
		return save(assetBalance);
	}

	@Transactional
	public AssetBalance update(@NotNull AssetBalance assetBalance, @NotNull Transaction transaction) {
		validate(assetBalance);

		assetBalance.setTotalBuyCost(calculateTotalBuyCost(assetBalance, transaction));
		assetBalance.setTotalSellValue(calculateTotalSellValue(assetBalance, transaction));
		assetBalance.setTotalBoughtQuantity(calculateTotalBoughtQuantity(assetBalance, transaction));
		assetBalance.setTotalSoldQuantity(calculateTotalSoldQuantity(assetBalance, transaction));
		assetBalance.setTotalRealizedProfit(calculateTotalRealizedProfit(assetBalance, transaction));
		assetBalance.setAvgBuyPrice(calculateAvgBuyPrice(assetBalance));
		assetBalance.setAvgSellPrice(calculateAvgSellPrice(assetBalance));
		assetBalance.setAmount(calculateAmountAfterSupply(assetBalance, transaction));
		assetBalance.setCost(calculateCost(assetBalance, transaction));

		return save(assetBalance);
	}

	@Transactional
	public AssetBalance save(@NotNull AssetBalance assetBalance) {
		assetBalance.setLastTimeUpdated(LocalDateTime.now());
		validate(assetBalance);
		try {
			return assetBalanceRepository.save(assetBalance);
		} catch (Exception e) {
			throw new InternalUnexpectedException(e);
		}
	}

	@Transactional
	public void delete(@NotNull Long assetBalanceId) {
		try {
			assetBalanceRepository.deleteById(assetBalanceId);
			log.info("Deleted successfully transaction: #{}", assetBalanceId);
		} catch (Exception e) {
			log.error("Failed to delete transaction: #{}", assetBalanceId, e);
			throw new InternalUnexpectedException(e);
		}
	}

	@Transactional
	public void deleteAll(@NotNull List<AssetBalance> assetBalances) {
		int size = assetBalances.size();
		log.info("Deleting {} assetBalances", size);
		try {
			assetBalanceRepository.deleteAll(assetBalances);
		} catch (Exception e) {
			log.error("Failed to delete {} assetBalances", size, e);
			throw new InternalUnexpectedException(e);
		}
		log.info("Deleted successfully {} assetBalances", size);
	}

	private static double calculateTotalRealizedProfit(@NotNull AssetBalance assetBalance, @NotNull Transaction transaction) {
		if (transaction.isBuyTransaction())
			return assetBalance.getTotalRealizedProfit();

		double totalRealizedProfit = assetBalance.getTotalSellValue() - (assetBalance.getAvgBuyPrice() * assetBalance.getTotalSoldQuantity());
		return NumberUtils.checkDouble(totalRealizedProfit);
	}

	private static double calculateTotalBoughtQuantity(@NotNull AssetBalance assetBalance, @NotNull Transaction transaction) {
		if (transaction.isSellTransaction())
			return assetBalance.getTotalBoughtQuantity();

		return NumberUtils.checkDouble(assetBalance.getTotalBoughtQuantity() + transaction.getOrderQuantity());
	}

	private static double calculateTotalSoldQuantity(@NotNull AssetBalance assetBalance, @NotNull Transaction transaction) {
		if (transaction.isBuyTransaction())
			return assetBalance.getTotalSoldQuantity();

		return NumberUtils.checkDouble(assetBalance.getTotalSoldQuantity() + transaction.getOrderQuantity());
	}

	private static double calculateTotalBuyCost(@NotNull AssetBalance assetBalance, @NotNull Transaction transaction) {
		if (transaction.isSellTransaction())
			return assetBalance.getTotalBuyCost();

		return NumberUtils.checkDouble(assetBalance.getTotalBuyCost() + transaction.getOrderTotalCost());
	}

	private static double calculateTotalSellValue(@NotNull AssetBalance assetBalance, @NotNull Transaction transaction) {
		if (transaction.isBuyTransaction())
			return assetBalance.getTotalSellValue();

		return NumberUtils.checkDouble(assetBalance.getTotalSellValue() + transaction.getOrderTotalCost());
	}

	/**
	 * @return amount of tokens that should be in the asset balance after transaction is commited.
	 * Cannot be less than ZERO.
	 */
	private static double calculateAmountAfterSupply(@NotNull AssetBalance assetBalance, @NotNull Transaction transaction) {
		double transactionAmount = NumberUtils.checkDouble(transaction.getOrderQuantity());

		if (transactionAmount <= 0)
			throw new InvalidBalanceAmountException("Invalid transaction amount. Transaction amount should be greater than 0");

		double quantityToAdd = MathUtils.withSign(transactionAmount, transaction.isBuyTransaction());
		double tokensAmountAfterSupply = assetBalance.getAmount() + quantityToAdd;

		if (tokensAmountAfterSupply < 0)
			throw new InvalidBalanceAmountException("Invalid transaction amount. The amount of tokens after transaction is less than 0");

		return NumberUtils.checkDouble(tokensAmountAfterSupply);
	}

	private static double calculateCost(@NotNull AssetBalance assetBalance, @NotNull Transaction transaction) {
		if (transaction.isBuyTransaction())
			return NumberUtils.checkDouble(assetBalance.getCost() + transaction.getOrderTotalCost());

		if (assetBalance.getAmount() == 0)
			return 0;

		double updatedCost = assetBalance.getCost() - (assetBalance.getAvgBuyPrice() * transaction.getOrderQuantity());
		return Math.max(0, updatedCost);
	}

	private static double calculateAvgBuyPrice(@NotNull AssetBalance updatedAssetBalance) {
		double totalBuyCost = updatedAssetBalance.getTotalBuyCost();
		double totalBoughtQuantity = updatedAssetBalance.getTotalBoughtQuantity();
		return totalBoughtQuantity == 0 ? 0 : totalBuyCost / totalBoughtQuantity;
	}

	private static double calculateAvgSellPrice(@NotNull AssetBalance updatedAssetBalance) {
		double totalSellValue = updatedAssetBalance.getTotalSellValue();
		double totalSoldQuantity = updatedAssetBalance.getTotalSoldQuantity();
		return totalSoldQuantity == 0 ? 0 : totalSellValue / totalSoldQuantity;
	}

	private static void validate(AssetBalance assetBalance) {
		Objects.requireNonNull(assetBalance, "assetBalance");
		Assert.notNull(assetBalance.getAsset(), "assetBalance asset");
		Assert.notNull(assetBalance.getPortfolio(), "assetBalance portfolio");
		Assert.notNull(assetBalance.getTimeCreatedAt(), "assetBalance createdAt");
		Assert.notNull(assetBalance.getLastTimeUpdated(), "assetBalance lastTimeUpdated");

		Assert.isTrue(assetBalance.getAmount() >= 0, "amount cannot be negative");
		Assert.isTrue(assetBalance.getHoldingDays() >= 0, "holdingDays cannot be negative");
		Assert.isTrue(assetBalance.getAvgBuyPrice() >= 0, "avgBuyPrice cannot be negative");
		Assert.isTrue(assetBalance.getAvgSellPrice() >= 0, "avgSellPrice cannot be negative");
	}

}
