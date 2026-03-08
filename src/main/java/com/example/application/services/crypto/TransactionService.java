package com.example.application.services.crypto;

import com.example.application.entities.common.TransactionType;
import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.AssetBalance;
import com.example.application.entities.crypto.Portfolio;
import com.example.application.entities.crypto.Transaction;
import com.example.application.repositories.crypto.TransactionRepository;
import com.example.application.utils.exceptions.InternalUnexpectedException;
import com.example.application.utils.exceptions.InvalidBalanceAmountException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/*
	TODO: [NEXT]
		[!] Store the images from external sources into the project or somewhere else
			in case the images got deleted, then we have instances

	TODO: [CRITICAL]
		[!] After deletion -> profit, realized, ... should be reverted
			[-] Rollback should also cover the avgBuyPrice for all transactions that are after the deleted transaction
* */

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

	private final PortfolioService portfolioService;
	private final AssetBalanceService assetBalanceService;
	private final TransactionRepository transactionRepository;

	//<editor-fold desc="SEARCH">
	public Optional<Transaction> findById(@NotNull Long transactionId) {
		Objects.requireNonNull(transactionId, "transactionId");
		return transactionRepository.findById(transactionId);
	}

	public List<Transaction> findBy(@NotNull Long portfolioId) {
		Objects.requireNonNull(portfolioId, "portfolioId");
		return transactionRepository.findByPortfolio(portfolioId);
	}

	public List<Transaction> findBy(@NotNull Long portfolioId, @NotNull String assetSymbol) {
		Objects.requireNonNull(portfolioId, "portfolioId");
		Objects.requireNonNull(assetSymbol, "assetSymbol");
		return transactionRepository.findByPortfolioAndAsset(portfolioId, assetSymbol);
	}

	public List<Transaction> findBy(@NotNull Long portfolioId, @NotNull String assetSymbol, @NotNull TransactionType type) {
		Objects.requireNonNull(portfolioId, "portfolioId");
		Objects.requireNonNull(assetSymbol, "assetSymbol");
		Objects.requireNonNull(type, "type");
		return transactionRepository.findByPortfolioAndAssetAndType(portfolioId, assetSymbol, type);
	}

	public List<Transaction> findBy(@NotNull Long portfolioId, @NotNull LocalDate from, @NotNull LocalDate to) {
		Objects.requireNonNull(portfolioId, "portfolioId");
		Objects.requireNonNull(from, "from");
		Objects.requireNonNull(to, "to");
		LocalDateTime fromDateTime = from.atStartOfDay();
		LocalDateTime toDateTime = to.plusDays(1).atStartOfDay();
		return transactionRepository.findByPortfolioAndDateTimeBetween(portfolioId, fromDateTime, toDateTime);
	}
	//</editor-fold>

	@NotNull
	@Transactional
	public Transaction transfer(@NotNull Long transactionId, @NotNull Long portfolioId) {
		return transfer(transactionId, portfolioId, false);
	}

	@NotNull
	@Transactional
	public Transaction transfer(@NotNull Long transactionId, @NotNull Long portfolioId, boolean replace) {
		Transaction oldTransaction = findById(transactionId)
				.orElseThrow(() -> new IllegalArgumentException("There is no such transaction with id: #" + transactionId));

		Portfolio portfolio = portfolioService.findById(portfolioId)
				.orElseThrow(() -> new IllegalArgumentException("There is no such portfolio with id: #" + transactionId));

		Transaction newTransaction = new Transaction(oldTransaction);
		newTransaction.setPortfolio(portfolio);

		if (replace) {
			log.info("Replacing {} from {} to {}", oldTransaction, oldTransaction.getPortfolio(), portfolio);
			delete(oldTransaction.getId());
		} else {
			log.info("Moving {} from {} to {}", oldTransaction, oldTransaction.getPortfolio(), portfolio);
		}

		return save(newTransaction);
	}

	@Transactional
	public void saveAll(@NotNull List<Transaction> transactions) {
		transactions.forEach(this::save);
	}

	@NotNull
	@Transactional
	public Transaction save(@NotNull Transaction transaction) {
		validate(transaction);
		try {
			updateAssetBalance(transaction);
			Transaction savedTransaction = transactionRepository.save(transaction);
			log.info("Saved successfully {}", savedTransaction);
			return savedTransaction;
		} catch (InvalidBalanceAmountException e) {
			throw e;
		} catch (Exception e) {
			log.error("Failed to save {}", transaction, e);
			throw new InternalUnexpectedException(e);
		}
	}

	@Transactional
	public void delete(@NotNull Long transactionId) {
		Objects.requireNonNull(transactionId, "transactionId");
		Transaction transaction = findById(transactionId)
				.orElseThrow(() -> new IllegalArgumentException("Cannot delete an unexistent transaction: #" + transactionId));

		try {
			transactionRepository.delete(transaction);
			log.info("Deleted successfully {}", transaction);
		} catch (Exception e) {
			log.error("Failed to delete {}", transaction, e);
			throw new InternalUnexpectedException(e);
		}
	}

	private void updateAssetBalance(Transaction transaction) {
		Asset asset = transaction.getAsset();
		Portfolio portfolio = transaction.getPortfolio();

		AssetBalance assetBalance = assetBalanceService.findByPortfolioAndAsset(portfolio.getId(), asset.getSymbol())
				.orElseGet(() -> assetBalanceService.createNew(portfolio.getId(), asset.getSymbol()));

		// Saving current avgBuyPrice before updating it
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