package com.example.application;

import com.example.application.asset.*;
import com.example.application.asset_balance.AssetBalance;
import com.example.application.asset_balance.AssetBalanceDTO;
import com.example.application.asset_balance.AssetBalanceService;
import com.example.application.asset_watcher.*;
import com.example.application.category.*;
import com.example.application.data.models.InstrumentsProvider;
import com.example.application.expense.*;
import com.example.application.expense.projections.MonthlyExpensesProjection;
import com.example.application.fetchers.crypto_compare.response.AssetMetadata;
import com.example.application.portfolio.CreatePortfolioRequest;
import com.example.application.portfolio.Portfolio;
import com.example.application.portfolio.PortfolioDTO;
import com.example.application.portfolio.PortfolioService;
import com.example.application.tag.*;
import com.example.application.transaction.*;
import com.example.application.user.User;
import com.example.application.user.UserService;
import com.example.application.user.domain.RegisterUserRequest;
import com.example.application.user.domain.UpdateUserPasswordRequest;
import com.example.application.user.domain.UpdateUserRequest;
import com.example.application.user.domain.UserDTO;
import com.example.application.user_asset.UserAssetService;
import com.example.application.utils.EntityValidator;
import com.example.application.utils.SecurityService;
import com.example.application.views.components.custom.icons.MonoIcon;
import com.example.application.views.components.custom.icons.PictogramIcon;
import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
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

	private final ExpenseService expenseService;
	private final CategoryService categoryService;
	private final TagService tagService;

	private final EntityValidator validator;

	//<editor-fold desc="USERS">
	@Transactional(readOnly = true)
	public boolean isPasswordCorrect(String password) {
		return userService.isPasswordCorrect(password, getAuthenticatedUser().getId());
	}

	@Transactional
	public boolean changeUserPassword(UpdateUserPasswordRequest updateUserPasswordRequest) {
		return userService.changePassword(updateUserPasswordRequest);
	}

	@Transactional
	public UserDTO createUser(@Valid RegisterUserRequest request) {
		User userEntity = userService.createNewUser(request);
		Long userId = userEntity.getId();

		addDefaultUserPortfolio(userId);
		addDefaultUserCategories(userId);
		addDefaultUserTags(userId);

		return new UserDTO(userEntity);
	}

	@Transactional
	public UserDTO updateUser(@Valid UpdateUserRequest request) {
		return new UserDTO(userService.updateUser(request));
	}

	@Transactional
	public void deleteUser(Long userId) {
		log.info("Starting deleting user #{} in batch", userId);

		User user = userService.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException("User not found: #" + userId));

		// Removing user's active portfolio constraint & deleting all entities related with this user
		user.resetActivePortfolio();
		deleteUserPortfolios(userId);
		findUserCategories(userId).forEach(category -> categoryService.delete(category.getId()));
		findUserTags(userId).forEach(tag -> deleteTag(tag.getId()));

		userService.delete(userId);
		log.info("Finished deleting user #{} in batch", userId);
	}

	@Transactional(readOnly = true)
	public boolean isUsernameAvailable(String username) {
		return !userService.isUsernameTaken(username);
	}

	@Transactional(readOnly = true)
	public boolean isEmailAvailable(String email) {
		return !userService.isEmailTaken(email);
	}
	//</editor-fold>

	//<editor-fold desc="ASSETS">
	@Transactional(readOnly = true)
	public List<AssetDTO> getAllAssets() {
		return assetService.findAll()
				.stream()
				.map(AssetDTO::mappedFrom)
				.toList();
	}

	@Transactional(readOnly = true)
	public Optional<AssetDTO> getAssetBySymbol(@NotNull String symbol) {
		return assetService.findBySymbol(symbol)
				.map(AssetDTO::mappedFrom);
	}

	@Transactional(readOnly = true)
	public BigDecimal getAmountOfTokens(Long portfolioId, String assetSymbol) {
		return getAssetBalanceByAsset(portfolioId, assetSymbol)
				.map(AssetBalanceDTO::getAmount)
				.orElse(BigDecimal.ZERO);
	}

	@Nullable
	@Transactional(readOnly = true)
	public String getAssetComment(String assetSymbol) {
		return userAssetService.getAssetComment(getAuthenticatedUser().getId(), assetSymbol);
	}

	@Transactional(readOnly = true)
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

	@Transactional(readOnly = true)
	public List<AssetDTO> getAllAssetsEverBought(PortfolioDTO portfolio) {
		return getTransactions(portfolio.getId())
				.stream()
				.filter(TransactionDTO::isBuyTransaction)
				.map(TransactionDTO::getAssetSymbol)
				.distinct()
				.map(symbol -> getAssetBySymbol(symbol)
						.orElseThrow(() -> new InternalUnexpectedException("No asset with symbol " + symbol))
				).toList();
	}
	//</editor-fold>

	//<editor-fold desc="TRANSACTIONS">
	@Transactional(readOnly = true)
	public List<TransactionDTO> getTransactions(Long portfolioId) {
		return transactionService.findBy(portfolioId)
				.stream()
				.map(TransactionDTO::new)
				.toList();
	}

	@Transactional(readOnly = true)
	public List<TransactionDTO> getTransactions(Long portfolioId, LocalDate from, LocalDate to) {
		return transactionService.findBy(portfolioId, from, to)
				.stream()
				.map(TransactionDTO::new)
				.toList();
	}

	@Transactional(readOnly = true)
	public List<TransactionDTO> getTransactionsByAsset(Long portfolioId, String assetSymbol) {
		return transactionService.findBy(portfolioId, assetSymbol)
				.stream()
				.map(TransactionDTO::new)
				.toList();
	}

	@Transactional
	public TransactionDTO transferTransaction(Long transactionId, Long portfolioId) {
		return transferTransaction(transactionId, portfolioId, false);
	}

	@Transactional
	public TransactionDTO transferTransaction(Long transactionId, Long portfolioId, boolean replace) {
		Portfolio portfolio = portfolioService.findById(portfolioId)
				.orElseThrow(() -> new EntityNotFoundException("There is no such portfolio with id: #" + transactionId));

		return new TransactionDTO(transactionService.transfer(transactionId, portfolio, replace));
	}

	@Transactional
	public TransactionDTO createTransaction(@Valid CreateTransactionRequest request) {
		log.info("Creating new transaction: {}", request);
		Transaction transaction = new Transaction();

		Asset asset = assetService.findBySymbol(request.getAssetSymbol())
				.orElseThrow(() -> new EntityNotFoundException("Cannot find asset: " + request.getAssetSymbol()));

		Portfolio portfolio = portfolioService.findById(request.getPortfolioId())
				.orElseThrow(() -> new EntityNotFoundException("Cannot find portfolio with id: #" + request.getPortfolioId()));

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
	public TransactionDTO updateTransaction(@Valid UpdateTransactionRequest request) {
		log.info("Updating transaction: {}", request);
		Transaction transaction = transactionService.findById(request.getId())
				.orElseThrow(() -> new EntityNotFoundException("Cannot find transaction with id: #" + request.getId()));

		Asset asset = assetService.findBySymbol(request.getAssetSymbol())
				.orElseThrow(() -> new EntityNotFoundException("Cannot find asset: " + request.getAssetSymbol()));

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
	public AssetWatcherDTO createAssetWatcher(@Valid CreateAssetWatcherRequest request) {
		Asset asset = assetService.findBySymbol(request.getAssetSymbol())
				.orElseThrow(() -> new EntityNotFoundException("Cannot find asset: " + request.getAssetSymbol()));

		Portfolio portfolio = portfolioService.findById(request.getPortfolioId())
				.orElseThrow(() -> new EntityNotFoundException("Cannot find portfolio with id: #" + request.getPortfolioId()));

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
	public AssetWatcherDTO updateAssetWatcher(@Valid UpdateAssetWatcherRequest request) {
		AssetWatcher entity = assetWatcherService.findById(request.getId())
				.orElseThrow(() -> new EntityNotFoundException("Cannot find assetWatcher: #" + request.getId() + " (deleted ?)"));

		Asset asset = assetService.findBySymbol(request.getAssetSymbol())
				.orElseThrow(() -> new EntityNotFoundException("Cannot find asset: " + request.getAssetSymbol()));

		entity.setAsset(asset);
		entity.setTargetPrice(request.getTargetPrice());
		entity.setTargetAmount(request.getTargetAmount());
		entity.setTransactionType(request.getTransactionType());
		entity.setCompleted(request.isCompleted());

		AssetWatcher savedEntity = assetWatcherService.save(entity);
		return new AssetWatcherDTO(savedEntity);
	}

	@Transactional(readOnly = true)
	public List<AssetWatcherDTO> getAssetWatchersByAssetAndActionType(Long portfolioId,
																	  String assetSymbol,
																	  TransactionType type)
	{
		return assetWatcherService.findByPortfolioAndAssetAndTransactionType(portfolioId, assetSymbol, type)
				.stream()
				.map(AssetWatcherDTO::new)
				.toList();
	}

	@Transactional(readOnly = true)
	public BigDecimal getClosestBuyWatcherPrice(Long portfolioId, String assetSymbol) {
		return getAssetWatchersByAssetAndActionType(portfolioId, assetSymbol, TransactionType.BUY)
				.stream()
				.filter(w -> !w.isCompleted())
				.map(AssetWatcherDTO::getTargetPrice)
				.min(Comparator.naturalOrder())
				.orElse(BigDecimal.ZERO);
	}

	@Transactional(readOnly = true)
	public BigDecimal getClosestSellWatcherPrice(Long portfolioId, String assetSymbol) {
		return getAssetWatchersByAssetAndActionType(portfolioId, assetSymbol, TransactionType.SELL)
				.stream()
				.filter(w -> !w.isCompleted())
				.map(AssetWatcherDTO::getTargetPrice)
				.max(Comparator.naturalOrder())
				.orElse(BigDecimal.ZERO);
	}
	//</editor-fold>

	//<editor-fold desc="ASSET BALANCES">
	@Transactional(readOnly = true)
	public List<AssetBalanceDTO> getPortfolioAssetBalances(Long portfolioId) {
		return assetBalanceService.findByPortfolio(portfolioId)
				.stream()
				.map(AssetBalanceDTO::mappedFrom)
				.toList();
	}

	@Transactional(readOnly = true)
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
				.orElseThrow(() -> new EntityNotFoundException("Cannot find portfolio: #" + portfolioId));

		Asset asset = assetService.findById(assetId)
				.orElseThrow(() -> new EntityNotFoundException("Cannot find asset: #" + assetId));

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
		assetBalance.setAmount(BigDecimal.ZERO);
		assetBalance.setCost(BigDecimal.ZERO);
		assetBalance.setTotalBuyCost(BigDecimal.ZERO);
		assetBalance.setTotalBoughtQuantity(BigDecimal.ZERO);
		assetBalance.setTotalSellValue(BigDecimal.ZERO);
		assetBalance.setTotalSoldQuantity(BigDecimal.ZERO);
		assetBalance.setAvgSellPrice(BigDecimal.ZERO);
		assetBalance.setAvgBuyPrice(BigDecimal.ZERO);
		assetBalance.setTotalRealizedProfit(BigDecimal.ZERO);
		return assetBalanceService.save(assetBalance);
	}
	//</editor-fold>

	//<editor-fold desc="PORTFOLIOS">

	@Nonnull
	@Transactional
	public PortfolioDTO createPortfolio(@Valid CreatePortfolioRequest request) {
		log.info("Creating new portfolio: {}", request);
		validator.validate(request);
		User user = userService.findById(request.getUserId())
				.orElseThrow(() -> new EntityNotFoundException("User #" + request.getUserId() + " not found. (deleted ?)"));

		Portfolio portfolio = portfolioService.createPortfolio(request, user);
		userService.setPortfolioAsActive(user.getId(), portfolio);

		return new PortfolioDTO(portfolio);
	}

	@Transactional
	public void deletePortfolio(Long portfolioId) {
		log.info("Starting deleting portfolio #{} in batch", portfolioId);
		transactionService.deleteAllPorfolioTransactions(portfolioId);
		assetBalanceService.deleteAllForPortfolio(portfolioId);
		assetWatcherService.deleteAllForPortfolio(portfolioId);
		portfolioService.delete(portfolioId);
		log.info("Finished deleting portfolio #{} in batch", portfolioId);
	}

	@Transactional
	public void deleteUserPortfolios(Long userId) {
		List<Portfolio> userPortfolios = portfolioService.findByUser(userId);
		log.info("Starting deleting all {} user portfolios in batch, for user: #{}", userPortfolios.size(), userId);

		for (Portfolio portfolio : userPortfolios) {
			Long portfolioId = portfolio.getId();
			transactionService.deleteAllPorfolioTransactions(portfolioId);
			assetBalanceService.deleteAllForPortfolio(portfolioId);
			assetWatcherService.deleteAllForPortfolio(portfolioId);
		}

		portfolioService.deleteAllUserPortfolios(userId);
		log.info("Finished deleting all user portfolios in batch, for user: #{}", userId);
	}

	@Transactional(readOnly = true)
	public Optional<PortfolioDTO> getPortfolioByName(String name) {
		return portfolioService
				.findByNameAndUser(name, getAuthenticatedUser().getId())
				.map(PortfolioDTO::new);
	}

	@Transactional(readOnly = true)
	public List<PortfolioDTO> getUserPortfolios() {
		return portfolioService
				.findByUser(getAuthenticatedUser().getId())
				.stream()
				.map(PortfolioDTO::new)
				.toList();
	}

	@Nonnull
	@Transactional(readOnly = true)
	public PortfolioDTO getActivePortfolio() {
		User user = userService.findById(getAuthenticatedUser().getId())
				.orElseThrow(() -> new RuntimeException("User #" + getAuthenticatedUser().getId() + " not found"));

		return Optional.ofNullable(user.getActivePortfolio())
				.map(PortfolioDTO::new)
				.orElseThrow(() -> new InternalUnexpectedException(getAuthenticatedUser() + "doesn't have any active portfolio"));
	}

	@Transactional
	public void setPortfolioAsActive(Long portfolioId) {
		portfolioService.setPortfolioAsActive(portfolioId);
	}
	//</editor-fold>

	//<editor-fold desc="ASSET METADATA">
	public void updateAssetData() {
		Map<String, AssetMetadata> metadataMap = instrumentsProvider.getUpdatedAssetMetadata();

		metadataMap.forEach((symbol, metadata) -> {
					try {
						updateAssetData(SymbolIndentifier.valueOf(symbol), metadata);
					} catch (Exception ignored) {
					}
				}
		);

		log.info("Updated {} assets", metadataMap.size());
	}

	private void updateAssetData(SymbolIndentifier identifier, AssetMetadata metadata) {
		Optional<Asset> optionalAsset = assetService.findBySymbol(identifier.name());

		if (optionalAsset.isEmpty()) {
			CreateAssetRequest request = AssetConvertor.mapToCreateRequest(identifier, metadata);
			assetService.createNewAsset(request);
		} else {
			Long assetId = optionalAsset.get().getId();
			UpdateAssetRequest request = AssetConvertor.mapToUpdateRequest(assetId, metadata);
			assetService.updateAsset(request);
		}
	}
	//</editor-fold>

	///////////////////////////////////////////////     EXPENSES     ///////////////////////////////////////////////////

	//<editor-fold desc="EXPENSES">
	@Transactional(readOnly = true)
	public List<ExpenseDTO> findAllUserExpenses() {
		return findAllUserExpenses(getAuthenticatedUser().getId());
	}

	@Transactional(readOnly = true)
	public List<ExpenseDTO> findAllUserExpenses(Long userId) {
		return expenseService.findByUser(userId)
				.stream()
				.map(ExpenseDTO::new)
				.toList();
	}

	@Transactional
	public List<MonthlyExpensesProjection> getMonthlyUserExpenses() {
		return getMonthlyUserExpenses(LocalDate.now());
	}

	@Transactional
	public List<MonthlyExpensesProjection> getMonthlyUserExpenses(@NotNull LocalDate date) {
		return expenseService.findMonthlyExpensesByUser(getAuthenticatedUser().getUsername(), date);
	}

	@Transactional
	public Expense createExpense(@Valid CreateExpenseRequest request) {
		log.info("Creating new expense: {}", request);
		User user = userService.findById(request.getUserId())
				.orElseThrow(() -> new EntityNotFoundException("User not found: #" + request.getUserId()));

		String categoryName = request.getCategory();
		Category category = categoryService.findByNameAndUser(categoryName, request.getUserId())
				.orElseThrow(() -> new EntityNotFoundException("Category '%s' does not exist.".formatted(categoryName)));

		List<Tag> tags = request.getTags()
				.stream()
				.map(_ -> tagService.findByNameAndUserOrCreate(categoryName, user))
				.toList();

		Expense expense = new Expense();
		expense.setName(request.getName());
		expense.setDescription(request.getDescription());
		expense.setAmount(request.getAmount());
		expense.setCategory(category);
		expense.setTimestamp(request.getTimestamp());
		expense.setStartDate(request.getStartDate());
		expense.setExpireDate(request.getExpireDate());
		expense.setUser(user);
		expense.getTags().addAll(tags);

		return expenseService.saveExpense(expense);
	}

	@Transactional
	public Expense updateExpense(@Valid UpdateExpenseRequest request) {
		log.info("Updating expense: {}", request);
		Expense expense = expenseService.findById(request.getId())
				.orElseThrow(() -> new EntityNotFoundException("User cannot be found"));

		String categoryName = request.getCategory();
		Category category = categoryService.findByNameAndUser(categoryName, expense.getUser().getId())
				.orElseThrow(() -> new EntityNotFoundException("Category '%s' does not exist.".formatted(categoryName)));

		Set<Tag> tags = request.getTags()
				.stream()
				.map(tagName -> tagService.findByNameAndUserOrCreate(tagName, expense.getUser()))
				.collect(Collectors.toSet());

		expense.setName(request.getName());
		expense.setDescription(request.getDescription());
		expense.setAmount(request.getAmount());
		expense.setCategory(category);
		expense.setTags(tags);
		expense.setTimestamp(request.getTimestamp());
		expense.setStartDate(request.getStartDate());
		expense.setExpireDate(request.getExpireDate());

		return expenseService.saveExpense(expense);
	}

	@Transactional
	public void deleteExpense(Long expenseId) {
		expenseService.deleteExpenseById(expenseId);
	}

	//</editor-fold>

	//<editor-fold desc="CATEGORIES">
	@Transactional(readOnly = true)
	public Optional<Category> findCategoryById(Long id) {
		return categoryService.findById(id);
	}

	@Transactional(readOnly = true)
	public List<Category> findUserCategories(Long userId) {
		return categoryService.findByUser(userId);
	}

	@Transactional(readOnly = true)
	public List<CategoryDTO> findUserCategories() {
		return findUserCategories(getAuthenticatedUser().getId())
				.stream()
				.map(CategoryDTO::new)
				.toList();
	}

	@Transactional
	public CategoryDTO createCategory(@Valid CreateCategoryRequest request) {
		log.info("Creating category: {}", request);
		Long userId = request.getUserId();

		User user = userService.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException("Couldn't find user with id: #" + userId));

		String name = request.getName();
		String iconName = request.getIconName();

		if (categoryService.isNameTaken(name, userId))
			throw new CategoryNameAlreadyExistsException(name);

		Category category = new Category();
		category.setName(name);
		category.setUser(user);
		category.setIconName(iconName);

		Category savedCategory = categoryService.save(category);
		return new CategoryDTO(savedCategory);
	}

	@Transactional
	public CategoryDTO updateCategory(@Valid UpdateCategoryRequest request) {
		log.info("Updating category: {}", request);
		Category category = findCategoryById(request.getId())
				.orElseThrow(() -> new EntityNotFoundException("Couldn't find category with id: #" + request.getId()));

		Long userId = category.getUser().getId();

		if (!category.getName().equals(request.getName()) && categoryService.isNameTaken(request.getName(), userId))
			throw new CategoryNameAlreadyExistsException(request.getIconName());

		category.setName(request.getName());
		category.setIconName(request.getIconName());

		Category updatedCategory = categoryService.save(category);
		return new CategoryDTO(updatedCategory);
	}

	@Transactional
	public void deleteCategory(Long categoryId) throws DeleteCategoryWithExpensesException {
		if (!expenseService.findByCategory(categoryId).isEmpty())
			throw new DeleteCategoryWithExpensesException("Cannot delete category, because it's used in other expenses");

		categoryService.delete(categoryId);
	}

	public List<MonoIcon> getCategoryIcons() {
		return Arrays.stream(PictogramIcon.values())
				.map(PictogramIcon::create)
				.toList();
	}

	//</editor-fold>

	//<editor-fold desc="TAGS">
	@Transactional(readOnly = true)
	public List<TagDTO> findUserTags() {
		return findUserTags(getAuthenticatedUser().getId());
	}

	@Transactional(readOnly = true)
	public List<TagDTO> findUserTags(Long userId) {
		return tagService.findByUser(userId)
				.stream()
				.map(TagDTO::new)
				.toList();
	}

	@Transactional
	public TagDTO createTag(@Valid CreateTagRequest request) {
		User user = userService.findById(request.getUserId())
				.orElseThrow(() -> new EntityNotFoundException("Cannot find user with id: #" + request.getUserId()));

		if (tagService.isNameTaken(request.getName(), user.getId()))
			throw new CategoryNameAlreadyExistsException(request.getName());

		Tag tag = new Tag(request.getName(), user);
		Tag createdTag = tagService.save(tag);
		return new TagDTO(createdTag);
	}

	@Transactional
	public TagDTO updateTag(@Valid UpdateTagRequest request) {
		Tag tag = tagService.findById(request.getId())
				.orElseThrow(() -> new EntityNotFoundException("Cannot find tag with id: #" + request.getId()));

		Long userId = tag.getUser().getId();
		String oldTagName = tag.getName();
		String newTagName = request.getName();

		if (!oldTagName.equals(newTagName) && tagService.isNameTaken(newTagName, userId))
			throw new TagNameAlreadyExistsException(newTagName);

		tag.setName(newTagName);
		Tag updatedTag = tagService.save(tag);
		return new TagDTO(updatedTag);
	}

	@Transactional
	public void deleteTag(Long tagId) {
		List<Expense> expensesWithTag = expenseService.findByTag(tagId);

		Tag tag = tagService.findById(tagId)
				.orElseThrow(() -> new EntityNotFoundException("Couldn't find tag: #" + tagId));

		expensesWithTag.forEach(e -> expenseService.removeExpenseTag(e, tag));

		tagService.delete(tagId);
	}

	@Transactional(readOnly = true)
	public boolean isTagNameAvailable(String tagName) {
		return !tagService.isNameTaken(tagName, getAuthenticatedUser().getId());
	}
	//</editor-fold>

	///////////////////////////////////////////////     OTHERS     /////////////////////////////////////////////////////

	private void addDefaultUserPortfolio(Long userId) {
		createPortfolio(new CreatePortfolioRequest("Main", userId));
	}

	private void addDefaultUserTags(Long userId) {
		for (DefaultTags tag : DefaultTags.values()) {
			CreateTagRequest createTagRequest = new CreateTagRequest(tag.getDisplayName(), userId);
			createTag(createTagRequest);
		}
	}

	private void addDefaultUserCategories(Long userId) {
		for (DefaultCategories category : DefaultCategories.values()) {
			CreateCategoryRequest createCategoryRequest = CreateCategoryRequest.builder()
					.name(category.getDisplayName())
					.iconName(category.getIconName())
					.userId(userId)
					.build();

			createCategory(createCategoryRequest);
		}
	}

	@Nonnull
	@Transactional(readOnly = true)
	public UserDTO getAuthenticatedUser() {
		return new UserDTO(securityService.getAuthenticatedUser());
	}
}
