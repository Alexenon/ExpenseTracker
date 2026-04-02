package com.example.application.services.crypto;

import com.example.application.components.EntityValidator;
import com.example.application.entities.common.TransactionType;
import com.example.application.entities.crypto.AssetWatcher;
import com.example.application.repositories.crypto.AssetWatcherRepository;
import com.example.application.utils.exceptions.InternalUnexpectedException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/*
 * TODO: [LONG TERM]
 *  - (checkbox) Retrieve from holdings, on successful state if user wants to
 *
 * */

@Service
@RequiredArgsConstructor
public class AssetWatcherService {

	private AssetWatcherRepository assetWatcherRepository;
	private EntityValidator validator;

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

	@Transactional
	public AssetWatcher save(@NotNull AssetWatcher assetWatcher) {
		validator.validate(assetWatcher);
		try {
			return assetWatcherRepository.save(assetWatcher);
		} catch (Exception e) {
			throw new InternalUnexpectedException(e);
		}
	}

	@Transactional
	public void delete(@NotNull Long assetWatcherId) {
		AssetWatcher assetWatcher = findById(assetWatcherId)
				.orElseThrow(() -> new EntityNotFoundException("Cannot delete an unexistent asset watcher: #" + assetWatcherId));
		try {
			assetWatcherRepository.delete(assetWatcher);
		} catch (Exception e) {
			throw new InternalUnexpectedException(e);
		}
	}

}
