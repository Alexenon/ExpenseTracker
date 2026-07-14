package com.example.application.asset_watcher;


import com.example.application.InternalUnexpectedException;
import com.example.application.transaction.TransactionType;
import com.example.application.utils.EntityValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssetWatcherService {

	private final AssetWatcherRepository assetWatcherRepository;
	private final EntityValidator validator;

	//<editor-fold desc="SEARCH">
	public Optional<AssetWatcher> findById(@NotNull Long assetWatcherId) {
		Objects.requireNonNull(assetWatcherId, "assetWatcherId");
		return assetWatcherRepository.findById(assetWatcherId);
	}

	public List<AssetWatcher> findByPortfolio(@NotNull Long portfolioId) {
		Objects.requireNonNull(portfolioId, "portfolioId");
		return assetWatcherRepository.findByPortfolio(portfolioId);
	}

	public List<AssetWatcher> findByAsset(@NotNull String assetSymbol) {
		Objects.requireNonNull(assetSymbol, "assetSymbol");
		return assetWatcherRepository.findByAsset(assetSymbol);
	}

	public List<AssetWatcher> findByPortfolioAndAsset(@NotNull Long portfolioId, @NotNull String assetSymbol) {
		Objects.requireNonNull(portfolioId, "portfolioId");
		Objects.requireNonNull(assetSymbol, "assetSymbol");
		return assetWatcherRepository.findByPortfolioAndAsset(portfolioId, assetSymbol);
	}

	public List<AssetWatcher> findByPortfolioAndAssetAndTransactionType(@NotNull Long portfolioId,
																		@NotNull String assetSymbol,
																		@NotNull TransactionType transactionType)
	{
		Objects.requireNonNull(portfolioId, "portfolioId");
		Objects.requireNonNull(assetSymbol, "assetSymbol");
		Objects.requireNonNull(transactionType, "transactionType");
		return assetWatcherRepository.findByPortfolioAndAssetAndTransactionType(portfolioId, assetSymbol, transactionType);
	}
	//</editor-fold>


	public AssetWatcher save(@NotNull AssetWatcher assetWatcher) {
		validator.validate(assetWatcher);
		try {
			return assetWatcherRepository.save(assetWatcher);
		} catch (Exception e) {
			throw new InternalUnexpectedException(e);
		}
	}


	public void delete(@NotNull Long assetWatcherId) {
		log.info("Deleting asset watcher: #{}", assetWatcherId);
		AssetWatcher assetWatcher = findById(assetWatcherId)
				.orElseThrow(() -> new EntityNotFoundException("Cannot delete an unexistent asset watcher: #" + assetWatcherId));
		try {
			assetWatcherRepository.delete(assetWatcher);
		} catch (Exception e) {
			throw new InternalUnexpectedException(e);
		}
		log.info("Succesfully deleted asset watcher: #{}", assetWatcherId);
	}


	public void deleteAllForPortfolio(Long portfolioId) {
		List<AssetWatcher> assetWatcherList = findByPortfolio(portfolioId);
		log.info("Deleting all asset watchers for portfolio: #{}", portfolioId);
		assetWatcherList.forEach(aw -> delete(aw.getId()));
		log.info("Deleted succesfully all {} asset watchers for portfolio: #{}", assetWatcherList.size(), portfolioId);
	}

}
