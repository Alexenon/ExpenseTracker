package com.example.application.services.crypto;

import com.example.application.data.enums.SymbolIndentifier;
import com.example.application.data.models.InstrumentsProvider;
import com.example.application.entities.User;
import com.example.application.entities.common.TransactionType;
import com.example.application.entities.crypto.*;
import com.example.application.utils.fetchers.api_responses.AssetMetadata;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class InstrumentsService {

	private final InstrumentsProvider instrumentsProvider;
	private final PortfolioService portfolioService;
	private final AssetService assetService;
	private final UserAssetService userAssetService;
	private final AssetWatcherService assetWatcherService;
	private final TransactionService transactionService;
	private final AssetBalanceService assetBalanceService;

	@Autowired
	public InstrumentsService(InstrumentsProvider instrumentsProvider,
							  PortfolioService portfolioService,
							  AssetService assetService,
							  UserAssetService userAssetService,
							  TransactionService transactionService,
							  AssetWatcherService assetWatcherService,
							  AssetBalanceService assetBalanceService)
	{
		this.instrumentsProvider = instrumentsProvider;
		this.portfolioService = portfolioService;
		this.assetService = assetService;
		this.userAssetService = userAssetService;
		this.transactionService = transactionService;
		this.assetWatcherService = assetWatcherService;
		this.assetBalanceService = assetBalanceService;
	}

	//<editor-fold desc="ASSETS">
	public List<Asset> getAllAssets() {
		return assetService.findAll();
	}

	public Optional<Asset> getAssetBySymbol(@NotNull String symbolName) {
		return assetService.findBySymbol(symbolName);
	}

	public Optional<Asset> saveAsset(Asset asset) {
		return assetService.save(asset);
	}

	public void updateAssetComment(User user, Asset asset, String comment) {
		userAssetService.updateAssetComment(user, asset, comment);
	}

	public void updateMarkAssetAsFavorite(User user, Asset asset, boolean markedAsFavorite) {
		userAssetService.updateMarkAssetAsFavorite(user, asset, markedAsFavorite);
	}

	public String getAssetComment(User user, Asset asset) {
		return userAssetService.getAssetComment(user, asset);
	}

	public boolean isAssetMarkedAsFavorite(User user, Asset asset) {
		return userAssetService.isAssetMarkedAsFavorite(user, asset);
	}
	//</editor-fold>

	//<editor-fold desc="ASSET WATCHERS">
	public AssetWatcher saveAssetWatcher(AssetWatcher assetWatcher) {
		return assetWatcherService.save(assetWatcher);
	}

	public void deleteAssetWatcher(AssetWatcher assetWatcher) {
		assetWatcherService.delete(assetWatcher);
	}

	public List<AssetWatcher> getAssetWatchersByAsset(Asset asset) {
		return assetWatcherService.findBy(asset);
	}

	public List<AssetWatcher> getAssetWatchersByAsset(Portfolio portfolio, Asset asset) {
		return assetWatcherService.findBy(portfolio, asset);
	}

	public List<AssetWatcher> getAssetWatchersByAssetAndActionType(Portfolio portfolio, Asset asset, AssetWatcher.ActionType actionType) {
		return assetWatcherService.findBy(portfolio, asset, actionType);
	}
	//</editor-fold>

	//<editor-fold desc="TRANSACTIONS">
	public Transaction transferTransaction(Transaction transaction, Portfolio portfolio, boolean replace) {
		return transactionService.transfer(transaction, portfolio, replace);
	}

	public Transaction saveTransaction(Transaction transaction) {
		return transactionService.save(transaction);
	}

	public void saveTransactions(List<Transaction> transactions) {
		transactionService.saveAll(transactions);
	}

	public void deleteTransaction(Transaction transaction) {
		transactionService.delete(transaction);
	}

	public List<Transaction> getTransactionsBy(Portfolio portfolio) {
		return transactionService.findBy(portfolio);
	}

	public List<Transaction> getTransactionsBy(Portfolio portfolio, Asset asset) {
		return transactionService.findBy(portfolio, asset);
	}

	public List<Transaction> getTransactionsBy(Portfolio portfolio, Asset asset, TransactionType type) {
		return transactionService.findBy(portfolio, asset, type);
	}

	public List<Transaction> getTransactionsBy(Portfolio portfolio, LocalDate from, LocalDate to) {
		return transactionService.findBy(portfolio, from, to);
	}

	public List<AssetBalance> getAssetsWithNonZeroAmount(@NotNull Portfolio portfolio) {
		return assetBalanceService.findHoldingsByPortfolio(portfolio);
	}
	//</editor-fold>

	//<editor-fold desc="PORTFOLIOS">
	public Portfolio createNewPortfolio(String name, User user) {
		return portfolioService.createNewPortfolio(name, user);
	}

	public List<Portfolio> getPortfoliosByUser(User user) {
		return portfolioService.findByUser(user);
	}

	public Optional<Portfolio> getPortfolioByNameAndUser(String name, User user) {
		return portfolioService.findByNameAndUser(name, user);
	}
	//</editor-fold>

	//<editor-fold desc="PORTFOLIO BALANCES">
	@NotNull
	public List<AssetBalance> getAssetBalancesByPortfolio(@NotNull Portfolio portfolio) {
		return assetBalanceService.findByPortfolio(portfolio);
	}

	@NotNull
	public Optional<AssetBalance> getAssetBalancesByPortfolioAndAsset(@NotNull Portfolio portfolio, @NotNull Asset asset) {
		return assetBalanceService.findByPortfolioAndAsset(portfolio, asset);
	}
	//</editor-fold>

	//<editor-fold desc="METADATA">
	@NotNull
	public AssetMetadata getAssetMetadata(@NotNull String symbol) {
		Objects.requireNonNull(symbol, "symbol");
		return instrumentsProvider.getMetadata().get(symbol);
	}

	public void updateAssetData() {
		Map<String, AssetMetadata> metadataMap = instrumentsProvider.getUpdatedMetadata();

		if (metadataMap.isEmpty()) {
			log.info("Metadata is empty. Skipping updating the database");
		} else {
			metadataMap.forEach((key, value) -> updateAssetData(SymbolIndentifier.valueOf(key), value));
			log.info("Updated database for {} assets", metadataMap.size());
		}
	}

	private void updateAssetData(SymbolIndentifier indentifier, @Nullable AssetMetadata assetMetadata) {
		if (assetMetadata == null) {
			log.info("Asset metadata for {} asset is null, skipping updating database", indentifier.name());
			return;
		}

		Asset asset = getAssetBySymbol(indentifier.name()).orElse(new Asset());
		asset.setSymbol(indentifier.name());
		asset.setFullName(indentifier.getFullName());
		Optional.ofNullable(assetMetadata.getPriceUsd()).ifPresent(asset::setMarketPrice);
		Optional.ofNullable(assetMetadata.getAssetDescriptionSummary()).ifPresent(asset::setSummaryDescription);
		Optional.ofNullable(assetMetadata.getSpotMoving24HourQuoteVolumeUsd()).ifPresent(asset::setTodayVolume);
		Optional.ofNullable(assetMetadata.getSpotMoving24HourChangePercentageUsd()).ifPresent(asset::setChangePercentage);
		Optional.ofNullable(assetMetadata.getSupplyCirculating()).ifPresent(asset::setCirculationSupply);
		Optional.ofNullable(assetMetadata.getSupplyTotal()).ifPresent(asset::setTotalSupply);
		Optional.ofNullable(assetMetadata.getTotalMktCapUsd()).ifPresent(asset::setTotalMarketCap);
		Optional.ofNullable(assetMetadata.getLogoUrl()).ifPresent(asset::setImageUrl);

		saveAsset(asset);
	}
	//</editor-fold>

}