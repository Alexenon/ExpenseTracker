package com.example.application.services.crypto;

import com.example.application.entities.common.TransactionType;
import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.AssetBalance;
import com.example.application.entities.crypto.Portfolio;
import com.example.application.entities.crypto.Transaction;
import com.example.application.repositories.crypto.TransactionRepository;
import com.example.application.utils.exceptions.InternalUnexpectedException;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class TransactionService {

	private final AssetBalanceService assetBalanceService;
	private final TransactionRepository transactionRepository;

	@Autowired
	public TransactionService(TransactionRepository transactionRepository, AssetBalanceService assetBalanceService) {
		this.assetBalanceService = assetBalanceService;
		this.transactionRepository = transactionRepository;
	}

	public List<Transaction> findBy(@NotNull Portfolio portfolio) {
		return transactionRepository.findByPortfolio(portfolio);
	}

	public List<Transaction> findBy(@NotNull Portfolio portfolio, @NotNull Asset asset) {
		return transactionRepository.findByPortfolioAndAsset(portfolio, asset);
	}

	public List<Transaction> findBy(@NotNull Portfolio portfolio, @NotNull Asset asset, @NotNull TransactionType type) {
		return transactionRepository.findByPortfolioAndAssetAndType(portfolio, asset, type);
	}

	public List<Transaction> findBy(@NotNull Portfolio portfolio, @NotNull LocalDate from, @NotNull LocalDate to) {
		Objects.requireNonNull(portfolio, "portfolio");
		Objects.requireNonNull(from, "from");
		Objects.requireNonNull(to, "to");
		LocalDateTime fromDateTime = from.atStartOfDay();
		LocalDateTime toDateTime = to.plusDays(1).atStartOfDay();
		return transactionRepository.findByPortfolioAndDateTimeBetween(portfolio, fromDateTime, toDateTime);
	}

	@Transactional
	public Transaction transfer(Transaction transaction, Portfolio portfolio) {
		return transfer(transaction, portfolio, false);
	}

	@Transactional
	public Transaction transfer(Transaction transaction, Portfolio portfolio, boolean replace) {
		Transaction newTransaction = new Transaction(transaction);
		newTransaction.setPortfolio(portfolio);

		if (replace)
			delete(transaction);

		return save(newTransaction);
	}

	@Transactional
	public void saveAll(@NotNull List<Transaction> transactions) {
		transactions.forEach(this::save);
	}

	@NotNull
	@Transactional
	public Transaction save(@NotNull Transaction transaction) {
		try {
			validate(transaction);
			updateQuantityIfRequired(transaction);
			updateAssetBalance(transaction);
			Transaction savedTransaction = transactionRepository.save(transaction);
			log.info("Saved successfully {}", savedTransaction);
			return savedTransaction;
		} catch (Exception e) {
			log.error("Failed to save {}, cause: {}", transaction, e.getMessage());
			throw new InternalUnexpectedException(e);
		}
	}

	@Transactional
	public void delete(@NotNull Transaction transaction) {
		Objects.requireNonNull(transaction, "transaction");
		try {
			transactionRepository.delete(transaction);
			log.info("Deleted successfully {}", transaction);
		} catch (Exception e) {
			log.error("Failed to delete {}, cause: {}", transaction, e.getMessage());
			throw new InternalUnexpectedException(e);
		}
	}

	/**
	 * Sets order quantity in case it's missing in the transaction itself
	 */
	private void updateQuantityIfRequired(@NotNull Transaction transaction) {
		if (transaction.getOrderQuantity() > 0)
			return;

		double orderQuantity = transaction.getOrderTotalCost() / transaction.getMarketPrice();
		transaction.setOrderQuantity(orderQuantity);
	}

	private void updateAssetBalance(Transaction transaction) {
		Asset asset = transaction.getAsset();
		Portfolio portfolio = transaction.getPortfolio();

		AssetBalance assetBalance = assetBalanceService.findByPortfolioAndAsset(portfolio, asset)
				.orElse(assetBalanceService.createNew(portfolio, asset));

		// Saving avgBuyPrice before updating the balance
		transaction.setAvgBuyPriceAtMoment(assetBalance.getAvgBuyPrice());

		assetBalanceService.update(assetBalance, transaction);
	}

	private void validate(Transaction transaction) {
		Objects.requireNonNull(transaction, "Transaction is missing");
		Assert.notNull(transaction.getAsset(), "Asset is missing");
		Assert.notNull(transaction.getType(), "Type is missing");
		Assert.notNull(transaction.getPortfolio(), "Portfolio is missing");
		Assert.notNull(transaction.getDateTime(), "DateTime is missing");
		Assert.isTrue(transaction.getMarketPrice() > 0, "Price should be above 0");
		Assert.isTrue(transaction.getOrderTotalCost() > 0, "Order cost should be above 0");
	}

}
