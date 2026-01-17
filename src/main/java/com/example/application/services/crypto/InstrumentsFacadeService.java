package com.example.application.services.crypto;

import com.example.application.data.dtos.AssetBalanceDTO;
import com.example.application.data.dtos.AssetDTO;
import com.example.application.data.dtos.PortfolioDTO;
import com.example.application.data.dtos.TransactionDTO;
import com.example.application.data.enums.SymbolIndentifier;
import com.example.application.data.models.InstrumentsProvider;
import com.example.application.entities.User;
import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.AssetWatcher;
import com.example.application.entities.crypto.Portfolio;
import com.example.application.services.SecurityService;
import com.example.application.services.UserService;
import com.example.application.utils.exceptions.InternalUnexpectedException;
import com.example.application.utils.fetchers.crypto_compare.response.AssetMetadata;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class InstrumentFacadeService {

	private final SecurityService securityService;
	private final InstrumentsProvider instrumentsProvider;

	private final UserService userService;
	private final PortfolioService portfolioService;
	private final AssetService assetService;
	private final UserAssetService userAssetService;
	private final TransactionService transactionService;
	private final AssetWatcherService assetWatcherService;
	private final AssetBalanceService assetBalanceService;

	@Autowired
	public InstrumentFacadeService(
			SecurityService securityService,
			InstrumentsProvider instrumentsProvider,
			UserService userService,
			PortfolioService portfolioService,
			AssetService assetService,
			UserAssetService userAssetService,
			TransactionService transactionService,
			AssetWatcherService assetWatcherService,
			AssetBalanceService assetBalanceService
	)
	{
		this.securityService = securityService;
		this.instrumentsProvider = instrumentsProvider;
		this.userService = userService;
		this.portfolioService = portfolioService;
		this.assetService = assetService;
		this.userAssetService = userAssetService;
		this.transactionService = transactionService;
		this.assetWatcherService = assetWatcherService;
		this.assetBalanceService = assetBalanceService;
	}

	//<editor-fold desc="USERS">
	public User createNewUser(User user) {
		Portfolio portfolio = portfolioService.createPortfolio("Main", user.getId());
		return portfolio.getUser();
	}

	public boolean isUsernameTaken(String username) {
		return userService.isUsernameTaken(username);
	}

	public boolean isEmailTaken(String email) {
		return userService.isEmailTaken(email);
	}
	//</editor-fold>

	//<editor-fold desc="ASSETS">
	public List<AssetDTO> getAllAssets() {
		return assetService.findAll()
				.stream()
				.map(AssetDTO::mappedFrom)
				.toList();
	}

	public Optional<AssetDTO> getAssetBySymbol(@NotNull String symbol) {
		return assetService.findBySymbol(symbol).map(AssetDTO::mappedFrom);
	}

	public double getAmountOfTokens(Long portfolioId, String assetSymbol) {
		return getAssetBalanceByAsset(portfolioId, assetSymbol)
				.map(AssetBalanceDTO::getAmount)
				.orElse(Double.NaN);
	}

	@Nullable
	public String getAssetComment(String assetSymbol) {
		return userAssetService.getAssetComment(getAuthenticatedUser().getId(), assetSymbol);
	}

	public boolean isAssetMarkedAsFavorite(String assetSymbol) {
		return userAssetService.isAssetMarkedAsFavorite(getAuthenticatedUser().getId(), assetSymbol);
	}

	public void updateAssetComment(String assetSymbol, @Nullable String comment) {
		userAssetService.updateAssetComment(getAuthenticatedUser().getId(), assetSymbol, comment);
	}

	public void updateMarkAssetAsFavorite(String assetSymbol, boolean isFavorite) {
		userAssetService.updateMarkAssetAsFavorite(getAuthenticatedUser().getId(), assetSymbol, isFavorite);
	}

	public List<AssetDTO> getAllAssetsEverBought(PortfolioDTO portfolio) {
		return getTransactions(portfolio.getId())
				.stream()
				.filter(TransactionDTO::isBuyTransaction)
				.map(TransactionDTO::getAssetSymbol)
				.distinct()
				.map(symbol ->
						getAssetBySymbol(symbol)
								.orElseThrow(() ->
										new InternalUnexpectedException("No asset with symbol " + symbol)))
				.toList();
	}
	//</editor-fold>

	//<editor-fold desc="TRANSACTIONS">
	public List<TransactionDTO> getTransactions(Long portfolioId) {
		return transactionService.findBy(portfolioId)
				.stream()
				.map(TransactionDTO::new)
				.toList();
	}

	public List<TransactionDTO> getTransactions(Long portfolioId, LocalDate from, LocalDate to) {
		return transactionService.findBy(portfolioId, from, to)
				.stream()
				.map(TransactionDTO::new)
				.toList();
	}

	public List<TransactionDTO> getTransactionsByAsset(Long portfolioId, String assetSymbol) {
		return transactionService.findBy(portfolioId, assetSymbol)
				.stream()
				.map(TransactionDTO::new)
				.toList();
	}

	public TransactionDTO transferTransaction(Long transactionId, Long portfolioId, boolean replace) {
		return new TransactionDTO(
				transactionService.transfer(transactionId, portfolioId, replace)
		);
	}

	public TransactionDTO saveTransaction(TransactionDTO dto) {
		return new TransactionDTO(transactionService.save(dto.toEntity()));
	}

	public void saveTransactions(List<TransactionDTO> transactions) {
		transactionService.saveAll(
				transactions.stream().map(TransactionDTO::toEntity).toList()
		);
	}

	public void deleteTransaction(TransactionDTO dto) {
		transactionService.delete(dto.getId());
	}
	//</editor-fold>

	//<editor-fold desc="ASSET WATCHERS">
	public AssetWatcher saveAssetWatcher(AssetWatcher watcher) {
		return assetWatcherService.save(watcher);
	}

	public void deleteAssetWatcher(AssetWatcher watcher) {
		assetWatcherService.delete(watcher.getId());
	}

	public List<AssetWatcher> getAssetWatchersByAsset(Long portfolioId, String assetSymbol) {
		return assetWatcherService.findBy(portfolioId, assetSymbol);
	}

	public List<AssetWatcher> getAssetWatchersByAssetAndActionType(Long portfolioId,
																   String assetSymbol,
																   AssetWatcher.ActionType type)
	{
		return assetWatcherService.findBy(portfolioId, assetSymbol, type);
	}

	public double getClosestBuyWatcherPrice(Long portfolioId, String assetSymbol) {
		return getAssetWatchersByAssetAndActionType(portfolioId, assetSymbol, AssetWatcher.ActionType.BUY)
				.stream()
				.filter(w -> !w.isCompleted())
				.map(AssetWatcher::getTarget)
				.min(Comparator.naturalOrder())
				.orElse(0.0);
	}

	public double getClosestSellWatcherPrice(Long portfolioId, String assetSymbol) {
		return getAssetWatchersByAssetAndActionType(portfolioId, assetSymbol, AssetWatcher.ActionType.SELL)
				.stream()
				.filter(w -> !w.isCompleted())
				.map(AssetWatcher::getTarget)
				.max(Comparator.naturalOrder())
				.orElse(0.0);
	}
	//</editor-fold>

	//<editor-fold desc="ASSET BALANCES">
	public List<AssetBalanceDTO> getAssetBalances(Long portfolioId) {
		return assetBalanceService.findByPortfolio(portfolioId)
				.stream()
				.map(AssetBalanceDTO::mappedFrom)
				.toList();
	}

	public Optional<AssetBalanceDTO> getAssetBalanceByAsset(Long portfolioId, String assetSymbol) {
		return assetBalanceService
				.findByPortfolioAndAsset(portfolioId, assetSymbol)
				.map(AssetBalanceDTO::mappedFrom);
	}
	//</editor-fold>

	//<editor-fold desc="PORTFOLIOS">
	@NotNull
	public PortfolioDTO createPortfolio(String name) {
		return new PortfolioDTO(portfolioService.createPortfolio(name, getAuthenticatedUser().getId()));
	}

	public Optional<PortfolioDTO> getPortfolioByName(String name) {
		return portfolioService
				.findByNameAndUser(name, getAuthenticatedUser().getId())
				.map(PortfolioDTO::new);
	}

	public List<PortfolioDTO> getUserPortfolios() {
		return portfolioService
				.findByUserId(getAuthenticatedUser().getId())
				.stream()
				.map(PortfolioDTO::new)
				.toList();
	}

	@NotNull
	public PortfolioDTO getActivePortfolio() {
		return new PortfolioDTO(getAuthenticatedUser().getActivePortfolio());
	}

	public PortfolioDTO setPortfolioAsActive(Long portfolioId) {
		return new PortfolioDTO(
				portfolioService.setPortfolioAsActive(portfolioId)
		);
	}
	//</editor-fold>

	//<editor-fold desc="ASSET METADATA">
	public void updateAssetData() {
		Map<String, AssetMetadata> metadataMap = instrumentsProvider.getUpdatedMetadata();

		if (metadataMap.isEmpty()) {
			log.info("Metadata is empty. Skipping update.");
			return;
		}

		metadataMap.forEach((symbol, metadata) ->
				updateAssetData(SymbolIndentifier.valueOf(symbol), metadata)
		);

		log.info("Updated {} assets", metadataMap.size());
	}

	private void updateAssetData(SymbolIndentifier identifier, @Nullable AssetMetadata metadata) {
		if (metadata == null) {
			return;
		}

		Asset asset = assetService.findBySymbol(identifier.name()).orElse(new Asset());

		asset.setSymbol(identifier.name());
		asset.setFullName(identifier.getFullName());
		Optional.ofNullable(metadata.getPriceUsd()).ifPresent(asset::setMarketPrice);
		Optional.ofNullable(metadata.getAssetDescriptionSummary()).ifPresent(asset::setSummaryDescription);
		Optional.ofNullable(metadata.getSpotMoving24HourQuoteVolumeUsd()).ifPresent(asset::setTodayVolume);
		Optional.ofNullable(metadata.getSpotMoving24HourChangePercentageUsd()).ifPresent(asset::setChangePercentage);
		Optional.ofNullable(metadata.getSupplyCirculating()).ifPresent(asset::setCirculationSupply);
		Optional.ofNullable(metadata.getSupplyTotal()).ifPresent(asset::setTotalSupply);
		Optional.ofNullable(metadata.getTotalMktCapUsd()).ifPresent(asset::setTotalMarketCap);
		Optional.ofNullable(metadata.getLogoUrl()).ifPresent(asset::setImageUrl);

		assetService.save(asset);
	}
	//</editor-fold>

	@NotNull
	public User getAuthenticatedUser() {
		return securityService.getAuthenticatedUser();
	}
}
