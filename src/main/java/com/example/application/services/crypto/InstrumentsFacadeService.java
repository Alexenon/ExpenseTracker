package com.example.application.services.crypto;

import com.example.application.data.dtos.*;
import com.example.application.data.enums.SymbolIndentifier;
import com.example.application.data.models.InstrumentsProvider;
import com.example.application.data.requests.CreateTransactionRequest;
import com.example.application.data.requests.RegisterUserRequest;
import com.example.application.data.requests.UpdateTransactionRequest;
import com.example.application.data.requests.asset_watchers.CreateAssetWatcherRequest;
import com.example.application.data.requests.asset_watchers.UpdateAssetWatcherRequest;
import com.example.application.data.requests.portfolio.CreatePortfolioRequest;
import com.example.application.entities.User;
import com.example.application.entities.common.TransactionType;
import com.example.application.entities.crypto.*;
import com.example.application.services.SecurityService;
import com.example.application.services.UserService;
import com.example.application.utils.exceptions.InternalUnexpectedException;
import com.example.application.utils.fetchers.crypto_compare.response.AssetMetadata;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class InstrumentsFacadeService {

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
	public InstrumentsFacadeService(
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
	@Transactional(rollbackFor = Exception.class)
	public UserDTO createNewUser(RegisterUserRequest request) {
		User userEntity = userService.createNewUser(request);

		CreatePortfolioRequest defaultPortfolio = CreatePortfolioRequest.builder()
				.portfolioName("Main")
				.userId(userEntity.getId())
				.build();

		createPortfolio(defaultPortfolio);

		return UserDTO.mappedFrom(userEntity);
	}

	@Transactional
	public void deleteUser(Long userId) {
		log.info("Starting deleting user #{} in batch", userId);
		deletePortfolios(portfolioService.findByUserId(userId));
		userService.delete(userId);
		log.info("Finished deleting user #{} in batch", userId);
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

	@Transactional
	public void updateAssetComment(String assetSymbol, @Nullable String comment) {
		userAssetService.updateAssetComment(getAuthenticatedUser().getId(), assetSymbol, comment);
	}

	@Transactional
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

	@Transactional
	public void deleteTransaction(Long transactionId) {
		transactionService.delete(transactionId);
	}

	@Transactional
	public TransactionDTO transferTransaction(Long transactionId, Long portfolioId) {
		return transferTransaction(transactionId, portfolioId, false);
	}

	@Transactional
	public TransactionDTO transferTransaction(Long transactionId, Long portfolioId, boolean replace) {
		Portfolio portfolio = portfolioService.findById(portfolioId)
				.orElseThrow(() -> new IllegalArgumentException("There is no such portfolio with id: #" + transactionId));

		return new TransactionDTO(transactionService.transfer(transactionId, portfolio, replace));
	}

	@Transactional
	public TransactionDTO createTransaction(CreateTransactionRequest request) {
		log.info("Creating new transaction: {}", request);
		Transaction transaction = new Transaction();

		Asset asset = assetService.findBySymbol(request.getAssetSymbol())
				.orElseThrow(() -> new IllegalArgumentException("Cannot find asset: " + request.getAssetSymbol()));

		Portfolio portfolio = portfolioService.findById(request.getPortfolioId())
				.orElseThrow(() -> new IllegalArgumentException("Cannot find portfolio with id: #" + request.getPortfolioId()));

		transaction.setAsset(asset);
		transaction.setPortfolio(portfolio);
		transaction.setMarketPrice(request.getMarketPrice());
		transaction.setOrderQuantity(request.getOrderQuantity());
		transaction.setOrderTotalCost(request.getOrderTotalCost());
		transaction.setType(request.getType() != null ? request.getType() : TransactionType.BUY);
		transaction.setNote(request.getNote());
		transaction.setDateTime(request.getDateTime() != null ? request.getDateTime() : LocalDateTime.now());

		updateAssetBalance(transaction);
		Transaction savedEntity = transactionService.save(transaction);
		return new TransactionDTO(savedEntity);
	}

	@Transactional
	public TransactionDTO updateTransaction(UpdateTransactionRequest request) {
		log.info("Updating transaction: {}", request);
		Transaction transaction = transactionService.findById(request.getId())
				.orElseThrow(() -> new IllegalArgumentException("Cannot find transaction with id: #" + request.getId()));

		Asset asset = assetService.findBySymbol(request.getAssetSymbol())
				.orElseThrow(() -> new IllegalArgumentException("Cannot find asset: " + request.getAssetSymbol()));

		transaction.setAsset(asset);
		transaction.setMarketPrice(request.getMarketPrice());
		transaction.setOrderQuantity(request.getOrderQuantity());
		transaction.setOrderTotalCost(request.getOrderTotalCost());
		transaction.setType(request.getType());
		transaction.setNote(request.getNote());
		transaction.setDateTime(request.getDateTime());

		updateAssetBalance(transaction);
		Transaction savedEntity = transactionService.save(transaction);
		return new TransactionDTO(savedEntity);
	}
	//</editor-fold>

	//<editor-fold desc="ASSET WATCHERS">
	@Transactional
	public AssetWatcherDTO createAssetWatcher(CreateAssetWatcherRequest request) {
		Asset asset = assetService.findBySymbol(request.getAssetSymbol())
				.orElseThrow(() -> new IllegalArgumentException("Cannot find asset: " + request.getAssetSymbol()));

		Portfolio portfolio = portfolioService.findById(request.getPortfolioId())
				.orElseThrow(() -> new IllegalArgumentException("Cannot find portfolio with id: #" + request.getPortfolioId()));

		AssetWatcher newEntity = new AssetWatcher();
		newEntity.setAsset(asset);
		newEntity.setPortfolio(portfolio);
		newEntity.setTargetPrice(request.getTargetPrice());
		newEntity.setTargetAmount(request.getTargetAmount());
		newEntity.setTransactionType(request.getTransactionType());
		newEntity.setCompleted(request.isCompleted());

		AssetWatcher savedEntity = assetWatcherService.save(newEntity);
		return new AssetWatcherDTO(savedEntity);
	}

	@Transactional
	public AssetWatcherDTO updateAssetWatcher(UpdateAssetWatcherRequest request) {
		AssetWatcher entity = assetWatcherService.findById(request.getId())
				.orElseThrow(() -> new IllegalArgumentException("Cannot find assetWatcher: #" + request.getId() + " (deleted ?)"));

		Asset asset = assetService.findBySymbol(request.getAssetSymbol())
				.orElseThrow(() -> new IllegalArgumentException("Cannot find asset: " + request.getAssetSymbol()));

		entity.setAsset(asset);
		entity.setTargetPrice(request.getTargetPrice());
		entity.setTargetAmount(request.getTargetAmount());
		entity.setTransactionType(request.getTransactionType());
		entity.setCompleted(request.isCompleted());

		AssetWatcher savedEntity = assetWatcherService.save(entity);
		return new AssetWatcherDTO(savedEntity);
	}

	public List<AssetWatcher> getAssetWatchersByAsset(Long portfolioId, String assetSymbol) {
		return assetWatcherService.findBy(portfolioId, assetSymbol);
	}

	public List<AssetWatcherDTO> getAssetWatchersByAssetAndActionType(Long portfolioId,
																	  String assetSymbol,
																	  TransactionType type)
	{
		return assetWatcherService.findBy(portfolioId, assetSymbol, type)
				.stream()
				.map(AssetWatcherDTO::new)
				.toList();
	}

	public double getClosestBuyWatcherPrice(Long portfolioId, String assetSymbol) {
		return getAssetWatchersByAssetAndActionType(portfolioId, assetSymbol, TransactionType.BUY)
				.stream()
				.filter(w -> !w.isCompleted())
				.map(AssetWatcherDTO::getTargetPrice)
				.min(Comparator.naturalOrder())
				.orElse(0.0);
	}

	public double getClosestSellWatcherPrice(Long portfolioId, String assetSymbol) {
		return getAssetWatchersByAssetAndActionType(portfolioId, assetSymbol, TransactionType.SELL)
				.stream()
				.filter(w -> !w.isCompleted())
				.map(AssetWatcherDTO::getTargetPrice)
				.max(Comparator.naturalOrder())
				.orElse(0.0);
	}
	//</editor-fold>

	//<editor-fold desc="ASSET BALANCES">
	public List<AssetBalanceDTO> getPorfolioAssetBalances(Long portfolioId) {
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

	@Transactional
	private void updateAssetBalance(Transaction transaction) {
		Long assetId = transaction.getAsset().getId();
		Long portfolioId = transaction.getPortfolio().getId();

		Portfolio portfolio = portfolioService.findById(portfolioId)
				.orElseThrow(() -> new IllegalArgumentException("Cannot find portfolio: #" + portfolioId));

		Asset asset = assetService.findById(assetId)
				.orElseThrow(() -> new IllegalArgumentException("Cannot find asset: #" + assetId));

		AssetBalance assetBalance = assetBalanceService.findByPortfolioAndAsset(portfolio.getId(), asset.getSymbol())
				.orElseGet(() -> createAssetBalance(portfolio, asset));

		// Saving current avgBuyPrice before updating it
		transaction.setAvgBuyPriceAtMoment(assetBalance.getAvgBuyPrice());
		assetBalanceService.update(assetBalance, transaction);
	}

	@Transactional
	public AssetBalance createAssetBalance(Portfolio portfolio, Asset asset) {
		AssetBalance assetBalance = new AssetBalance();
		assetBalance.setPortfolio(portfolio);
		assetBalance.setAsset(asset);
		return assetBalanceService.save(assetBalance);
	}
	//</editor-fold>

	//<editor-fold desc="PORTFOLIOS">

	@NotNull
	@Transactional
	public PortfolioDTO createPortfolio(@NotNull CreatePortfolioRequest request) {
		User user = userService.findById(request.getUserId())
				.orElseThrow(() -> new IllegalArgumentException("User #" + request.getUserId() + " not found. (deleted ?)"));

		Portfolio portfolio = portfolioService.createPortfolio(request, user);
		userService.setPortfolioAsActive(user.getId(), portfolio);

		return new PortfolioDTO(portfolio);
	}

	@Transactional
	public void deletePortfolio(Long portfolioId) {
		log.info("Starting deleting portfolio #{} in batch", portfolioId);
		transactionService.deleteAllPorfolioTransactions(portfolioId);
		List<AssetBalance> assetBalances = assetBalanceService.findByPortfolio(portfolioId);
		assetBalanceService.deleteAll(assetBalances);
		portfolioService.delete(portfolioId);
		log.info("Finished deleting portfolio #{} in batch", portfolioId);
	}

	@Transactional
	public void deletePortfolios(List<Portfolio> portfolios) {
		int size = portfolios.size();
		log.info("Starting deleting all portfolios #{} in batch", size);

		for (Portfolio portfolio : portfolios) {
			transactionService.deleteAllPorfolioTransactions(portfolio.getId());
		}

		portfolioService.deleteAll(portfolios);
		log.info("Finished deleting all portfolios #{} in batch", size);
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
		User user = userService.findById(getAuthenticatedUser().getId())
				.orElseThrow(() -> new RuntimeException("User #" + getAuthenticatedUser().getId() + " not found"));

		return Optional.ofNullable(user.getActivePortfolio())
				.map(PortfolioDTO::new)
				.orElseThrow(() -> new InternalUnexpectedException(getAuthenticatedUser() + "doesn't have any active portfolio"));
	}

	public void setPortfolioAsActive(Long portfolioId) {
		portfolioService.setPortfolioAsActive(portfolioId);
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
	public UserDTO getAuthenticatedUser() {
		return UserDTO.mappedFrom(securityService.getAuthenticatedUser());
	}
}
