package com.example.application.services.crypto;

import com.example.application.components.EntityValidator;
import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.AssetBalance;
import com.example.application.entities.crypto.Portfolio;
import com.example.application.entities.crypto.Transaction;
import com.example.application.finance.FinancialConstants;
import com.example.application.repositories.crypto.AssetBalanceRepository;
import com.example.application.utils.common.lang.NumberUtils;
import com.example.application.utils.exceptions.InternalUnexpectedException;
import com.example.application.utils.exceptions.InvalidBalanceAmountException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssetBalanceService {

	private final EntityValidator validator;
	private final AssetBalanceRepository assetBalanceRepository;

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
		validator.validate(assetBalance);
		try {
			return assetBalanceRepository.save(assetBalance);
		} catch (Exception e) {
			throw new InternalUnexpectedException(e);
		}
	}

	@Transactional
	public void delete(@NotNull Long assetBalanceId) {
		AssetBalance assetBalance = findById(assetBalanceId)
				.orElseThrow(() -> new EntityNotFoundException("Cannot delete an unexistent assetBalace: #" + assetBalanceId));

		try {
			assetBalanceRepository.delete(assetBalance);
			log.info("Deleted successfully asset balance: #{}", assetBalanceId);
		} catch (Exception e) {
			log.error("Failed to delete asset balance: #{}", assetBalanceId, e);
			throw new InternalUnexpectedException(e);
		}
	}

	@Transactional
	public void deleteAllForPortfolio(Long portfolioId) {
		List<AssetBalance> balances = findByPortfolio(portfolioId);
		log.info("Deleting all asset balances for portfolio: #{}", portfolioId);
		balances.forEach(assetBalance -> delete(assetBalance.getId()));
		log.info("Deleted succesfully all {} asset balances for portfolio: #{}", balances.size(), portfolioId);
	}

	private static BigDecimal calculateTotalRealizedProfit(@NotNull AssetBalance assetBalance, @NotNull Transaction transaction) {
		if (transaction.isBuyTransaction())
			return assetBalance.getTotalRealizedProfit();

		BigDecimal avgSoldPrice = assetBalance.getAvgBuyPrice().multiply(assetBalance.getTotalSoldQuantity());
		return assetBalance.getTotalSellValue().subtract(avgSoldPrice);
	}

	private static BigDecimal calculateTotalBoughtQuantity(@NotNull AssetBalance assetBalance, @NotNull Transaction transaction) {
		if (transaction.isSellTransaction())
			return assetBalance.getTotalBoughtQuantity();

		return assetBalance.getTotalBoughtQuantity().add(transaction.getOrderQuantity());
	}

	private static BigDecimal calculateTotalSoldQuantity(@NotNull AssetBalance assetBalance, @NotNull Transaction transaction) {
		if (transaction.isBuyTransaction())
			return assetBalance.getTotalSoldQuantity();

		return assetBalance.getTotalSoldQuantity().add(transaction.getOrderQuantity());
	}

	private static BigDecimal calculateTotalBuyCost(@NotNull AssetBalance assetBalance, @NotNull Transaction transaction) {
		if (transaction.isSellTransaction())
			return assetBalance.getTotalBuyCost();

		return assetBalance.getTotalBuyCost().add(transaction.getOrderTotalCost());
	}

	private static BigDecimal calculateTotalSellValue(@NotNull AssetBalance assetBalance, @NotNull Transaction transaction) {
		if (transaction.isBuyTransaction())
			return assetBalance.getTotalSellValue();

		return assetBalance.getTotalSellValue().add(transaction.getOrderTotalCost());
	}

	/**
	 * @return amount of tokens that should be in the asset balance after transaction is commited.
	 * Cannot be less than ZERO.
	 */
	private static BigDecimal calculateAmountAfterSupply(@NotNull AssetBalance assetBalance, @NotNull Transaction transaction) {
		BigDecimal transactionAmount = transaction.getOrderQuantity();

		if (transactionAmount.signum() <= 0)
			throw new InvalidBalanceAmountException("Invalid transaction amount. Transaction amount should be greater than 0");

		BigDecimal quantityToAdd = NumberUtils.applySign(transactionAmount, transaction.isBuyTransaction());
		BigDecimal tokensAmountAfterSupply = assetBalance.getAmount().add(quantityToAdd);

		if (tokensAmountAfterSupply.signum() < 0)
			throw new InvalidBalanceAmountException("Invalid transaction amount. The amount of tokens after transaction is less than 0");

		return tokensAmountAfterSupply;
	}

	private static BigDecimal calculateCost(@NotNull AssetBalance assetBalance, @NotNull Transaction transaction) {
		if (transaction.isBuyTransaction())
			return assetBalance.getCost().add(transaction.getOrderTotalCost());

		if (assetBalance.getAmount().signum() == 0)
			return BigDecimal.ZERO;

		BigDecimal avgBoughtQuantity = assetBalance.getAvgBuyPrice().multiply(transaction.getOrderQuantity());
		BigDecimal updatedCost = assetBalance.getCost().subtract(avgBoughtQuantity);
		return updatedCost.max(BigDecimal.ZERO);
	}

	private static BigDecimal calculateAvgBuyPrice(@NotNull AssetBalance updatedAssetBalance) {
		BigDecimal totalBuyCost = updatedAssetBalance.getTotalBuyCost();
		BigDecimal totalBoughtQuantity = updatedAssetBalance.getTotalBoughtQuantity();
		return totalBoughtQuantity.signum() == 0
				? BigDecimal.ZERO
				: totalBuyCost.divide(totalBoughtQuantity, FinancialConstants.PRICE_SCALE, RoundingMode.HALF_UP);
	}

	private static BigDecimal calculateAvgSellPrice(@NotNull AssetBalance updatedAssetBalance) {
		BigDecimal totalSellValue = updatedAssetBalance.getTotalSellValue();
		BigDecimal totalSoldQuantity = updatedAssetBalance.getTotalSoldQuantity();
		return totalSoldQuantity.signum() == 0
				? BigDecimal.ZERO
				: totalSellValue.divide(totalSoldQuantity, FinancialConstants.PRICE_SCALE, RoundingMode.HALF_UP);
	}

}
