package com.example.application.services.crypto;

import com.example.application.entities.common.TransactionType;
import com.example.application.entities.crypto.AssetWatcher;
import com.example.application.repositories.crypto.AssetWatcherRepository;
import com.example.application.utils.exceptions.InternalUnexpectedException;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/*
 * TODO: [LONG TERM]
 *  - (checkbox) Retrieve from holdings, on successful state if user wants to
 *
 * */

@Service
public class AssetWatcherService {

	@Autowired
	private AssetWatcherRepository assetWatcherRepository;

	//<editor-fold desc="SEARCH">
	public Optional<AssetWatcher> findById(@NotNull Long assetWatcherId) {
		Objects.requireNonNull(assetWatcherId, "assetWatcherId");
		return assetWatcherRepository.findById(assetWatcherId);
	}

	public List<AssetWatcher> findBy(@NotNull Long portfolioId) {
		Objects.requireNonNull(portfolioId, "portfolioId");
		return assetWatcherRepository.findByPortfolio(portfolioId);
	}

	public List<AssetWatcher> findBy(@NotNull String assetSymbol) {
		Objects.requireNonNull(assetSymbol, "assetSymbol");
		return assetWatcherRepository.findByAsset(assetSymbol);
	}

	public List<AssetWatcher> findBy(@NotNull Long portfolioId, @NotNull String assetSymbol) {
		Objects.requireNonNull(portfolioId, "portfolioId");
		Objects.requireNonNull(assetSymbol, "assetSymbol");
		return assetWatcherRepository.findByPortfolioAndAsset(portfolioId, assetSymbol);
	}

	public List<AssetWatcher> findBy(@NotNull Long portfolioId,
									 @NotNull String assetSymbol,
									 @NotNull TransactionType transactionType)
	{
		Objects.requireNonNull(portfolioId, "portfolioId");
		Objects.requireNonNull(assetSymbol, "assetSymbol");
		Objects.requireNonNull(transactionType, "transactionType");
		return assetWatcherRepository.findByPortfolioAndAssetAndTransactionType(portfolioId, assetSymbol, transactionType);
	}
	//</editor-fold>

	@NotNull
	@Transactional
	public AssetWatcher save(@NotNull AssetWatcher assetWatcher) {
		try {
			validate(assetWatcher);
			return assetWatcherRepository.save(assetWatcher);
		} catch (Exception e) {
			throw new InternalUnexpectedException(e);
		}
	}

	@Transactional
	public void delete(@NotNull Long assetWatcherId) {
		Objects.requireNonNull(assetWatcherId, "assetWatcherId");
		AssetWatcher assetWatcher = findById(assetWatcherId)
				.orElseThrow(() -> new IllegalArgumentException("Cannot delete an unexistent transaction: #" + assetWatcherId));
		try {
			assetWatcherRepository.delete(assetWatcher);
		} catch (Exception e) {
			throw new InternalUnexpectedException(e);
		}
	}

	private void validate(AssetWatcher assetWatcher) {
		Objects.requireNonNull(assetWatcher, "assetWatcher");
		Assert.notNull(assetWatcher.getAsset(), "Asset is missing");
		Assert.notNull(assetWatcher.getPortfolio(), "Portfolio is missing");
		Assert.notNull(assetWatcher.getTransactionType(), "ActionType is missing");
		Assert.isTrue(assetWatcher.getTargetPrice() >= 0, "price cannot be negative");
		Assert.isTrue(assetWatcher.getTargetAmount() >= 0, "amount cannot be negative");
	}

}
