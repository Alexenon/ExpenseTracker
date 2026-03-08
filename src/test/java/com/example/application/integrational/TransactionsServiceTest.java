package com.example.application.integrational;

import com.example.application.Application;
import com.example.application.data.dtos.PortfolioDTO;
import com.example.application.data.dtos.TransactionDTO;
import com.example.application.data.requests.CreateTransactionRequest;
import com.example.application.data.requests.portfolio.CreatePortfolioRequest;
import com.example.application.entities.User;
import com.example.application.entities.common.TransactionType;
import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.Portfolio;
import com.example.application.entities.crypto.Transaction;
import com.example.application.repositories.UserRepository;
import com.example.application.repositories.crypto.AssetBalanceRepository;
import com.example.application.repositories.crypto.AssetRepository;
import com.example.application.repositories.crypto.PortfolioRepository;
import com.example.application.repositories.crypto.TransactionRepository;
import com.example.application.services.UserService;
import com.example.application.services.crypto.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
class TransactionsServiceTest extends AbstractTest {

	private final InstrumentsFacadeService instrumentsFacadeService;
	private final UserService userService;
	private final PortfolioService portfolioService;
	private final AssetBalanceService assetBalanceService;

	private final TransactionRepository transactionRepository;
	private final UserRepository userRepository;
	private final AssetRepository assetRepository;
	private final PortfolioRepository portfolioRepository;
	private final AssetBalanceRepository assetBalanceRepository;
	private final TransactionService transactionService;

	private User user;
	private Portfolio portfolio;
	private Asset asset;

	@Autowired
	public TransactionsServiceTest(InstrumentsFacadeService instrumentsFacadeService,
								   UserService userService,
								   AssetService assetService,
								   PortfolioService portfolioService,
								   AssetBalanceService assetBalanceService,
								   AssetBalanceRepository assetBalanceRepository,
								   UserRepository userRepository,
								   AssetRepository assetRepository,
								   TransactionRepository transactionRepository,
								   PortfolioRepository portfolioRepository,
								   TransactionService transactionService)
	{
		super(instrumentsFacadeService, assetService);
		this.instrumentsFacadeService = instrumentsFacadeService;
		this.userService = userService;
		this.portfolioService = portfolioService;
		this.assetBalanceService = assetBalanceService;
		this.assetBalanceRepository = assetBalanceRepository;
		this.userRepository = userRepository;
		this.assetRepository = assetRepository;
		this.transactionRepository = transactionRepository;
		this.portfolioRepository = portfolioRepository;
		this.transactionService = transactionService;
	}

	@BeforeEach
	void setupUserAndPortfolio() {
		Long userId = createUser("user", "test-email@email.com");
		Long assetId = createAsset("BTC", 100_000);
		this.user = userService.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
		this.portfolio = user.getActivePortfolio();
		this.asset = assetService.findById(assetId).orElseThrow(() -> new EntityNotFoundException("Asset not found"));
	}

	@AfterEach
	void removeUser() {
		assetBalanceRepository.deleteAll();
		transactionRepository.deleteAll();
		portfolioRepository.deleteAll();
		userRepository.deleteAll();
		assetRepository.deleteAll();
		Assertions.assertTrue(userService.findById(user.getId()).isEmpty(), "User was not deleted");
		Assertions.assertTrue(portfolioService.findByUserId(user.getId()).isEmpty(), "Portfolios are not deleted");
		Assertions.assertTrue(assetBalanceService.findByPortfolio(portfolio.getId()).isEmpty(), "Asset balances are not deleted");
	}

	@Test
	void saveTransactionSuccessfully() {
		CreateTransactionRequest request = CreateTransactionRequest.builder()
				.assetSymbol("BTC")
				.type(TransactionType.BUY)
				.marketPrice(100_000.00)
				.orderQuantity(0.0001)
				.portfolioId(portfolio.getId())
				.build();

		TransactionDTO saved = instrumentsFacadeService.createTransaction(request);
		Assertions.assertNotNull(saved.getId(), "Transaction ID should be generated");
		Assertions.assertEquals("BTC", saved.getAssetSymbol(), "Asset symbol should match");
		Assertions.assertEquals(TransactionType.BUY, saved.getType(), "Transaction type should match");
		Assertions.assertEquals(portfolio.getId(), saved.getPortfolioId(), "Transaction should belong to portfolio");
	}

	@Test
	void shouldNotAllowTransactionWithoutPortfolio() {
		CreateTransactionRequest request = CreateTransactionRequest.builder()
				.assetSymbol("BTC")
				.type(TransactionType.BUY)
				.marketPrice(100_000.00)
				.orderQuantity(0.0001)
				.portfolioId(null)            // MISSING PORTFOLIO
				.build();

		Assertions.assertThrows(NullPointerException.class,
				() -> instrumentsFacadeService.createTransaction(request),
				"Transaction without portfolio should fail validation");
	}

	@Test
	void portfolioShouldHaveCreatedTransactions() {
		CreateTransactionRequest request = buyTransaction(100_000, 1);
		TransactionDTO saved = instrumentsFacadeService.createTransaction(request);
		List<Transaction> transactions = transactionService.findBy(portfolio.getId());

		Assertions.assertFalse(transactions.isEmpty(), "Portfolio should have transactions");
		Assertions.assertEquals(1, transactions.size(), "Portfolio should have exactly one transaction");
	}

	@Test
	void deleteShouldRemoveTransaction() {
		CreateTransactionRequest request = buyTransaction(100_000, 1);
		TransactionDTO transaction = instrumentsFacadeService.createTransaction(request);

		transactionService.delete(transaction.getId());

		Assertions.assertTrue(transactionService.findById(transaction.getId()).isEmpty(),
				"Transaction is still present in the database");
	}

	@Test
	void deleteShouldFailWhenTransactionDoesNotExist() {
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> transactionService.delete(999L),
				"Deleting non-existent transaction should fail");
	}

	@Test
	void transferTransactionShouldMoveToAnotherPortfolio() {
		CreatePortfolioRequest portfolioRequest = createPortfolioRequest("Trading");
		PortfolioDTO otherPorfolio = instrumentsFacadeService.createPortfolio(portfolioRequest);

		// Create transaction in default portfolio
		CreateTransactionRequest request = buyTransaction(100_000, 0.0001);
		TransactionDTO originalTransaction = instrumentsFacadeService.createTransaction(request);

		// Transfer to trading portfolio
		Transaction transferred = transactionService.transfer(originalTransaction.getId(), otherPorfolio.getId());

		Assertions.assertEquals(otherPorfolio.getId(), transferred.getPortfolio().getId(),
				"Transaction should be transferred to new portfolio");
		Assertions.assertTrue(transactionService.findBy(portfolio.getId()).isEmpty(),
				"Original portfolio should have no transactions");
		Assertions.assertEquals(1, transactionService.findBy(otherPorfolio.getId()).size(),
				"Target portfolio should have the transferred transaction");
	}

	//<editor-fold desc="Utils">
	private CreateTransactionRequest buyTransaction(double marketPrice, double orderQuantity) {
		return CreateTransactionRequest.builder()
				.assetSymbol(asset.getSymbol())
				.type(TransactionType.BUY)
				.marketPrice(marketPrice)
				.orderQuantity(orderQuantity)
				.portfolioId(portfolio.getId())
				.build();
	}

	private CreateTransactionRequest sellTransaction(double marketPrice, double orderQuantity) {
		return CreateTransactionRequest.builder()
				.assetSymbol(asset.getSymbol())
				.type(TransactionType.SELL)
				.marketPrice(marketPrice)
				.orderQuantity(orderQuantity)
				.portfolioId(portfolio.getId())
				.build();
	}

	private CreatePortfolioRequest createPortfolioRequest(String portoflioName) {
		return CreatePortfolioRequest.builder()
				.portfolioName(portoflioName)
				.userId(user.getId())
				.build();
	}
	//</editor-fold>

}
