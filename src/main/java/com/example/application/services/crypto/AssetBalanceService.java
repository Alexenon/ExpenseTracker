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

import static com.example.application.utils.investment.ProfitCalculator.calculateNewAvgPrice;

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

		updateAvgBuySellPrice(assetBalance, transaction);
		assetBalance.setAmount(calculateAmountAfterSupply(assetBalance, transaction));
		assetBalance.setCost(calculateTotalCost(assetBalance, transaction));

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

	/**
	 * @return totalCost calculated for provided transaction, that cannot be less than ZERO.
	 */
	private static double calculateTotalCost(@NotNull AssetBalance assetBalance, @NotNull Transaction transaction) {
		double newCost = MathUtils.withSign(transaction.getOrderTotalCost(), transaction.isBuyTransaction());
		double formatedCost = Math.max(0, assetBalance.getCost() + newCost);
		return NumberUtils.checkDouble(formatedCost);
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

	private static void updateAvgBuySellPrice(@NotNull AssetBalance assetBalance, @NotNull Transaction transaction) {
		double marketPrice = transaction.getMarketPrice();
		double previousAmount = assetBalance.getAmount();
		double orderQuantity = transaction.getOrderQuantity();

		if (transaction.isBuyTransaction()) {
			assetBalance.setAvgBuyPrice(
					calculateNewAvgPrice(assetBalance.getAvgBuyPrice(), previousAmount, orderQuantity, marketPrice)
			);
		} else {
			assetBalance.setAvgSellPrice(
					calculateNewAvgPrice(assetBalance.getAvgSellPrice(), previousAmount, orderQuantity, marketPrice)
			);
		}
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
