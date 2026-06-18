package com.example.application.integrational;

import com.example.application.Application;
import com.example.application.data.dtos.PortfolioDTO;
import com.example.application.data.dtos.TransactionDTO;
import com.example.application.data.requests.CreateTransactionRequest;
import com.example.application.data.requests.UpdateTransactionRequest;
import com.example.application.data.requests.portfolio.CreatePortfolioRequest;
import com.example.application.entities.User;
import com.example.application.entities.common.TransactionType;
import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.Portfolio;
import com.example.application.entities.crypto.Transaction;
import com.example.application.services.UserService;
import com.example.application.services.crypto.AssetBalanceService;
import com.example.application.services.crypto.AssetService;
import com.example.application.services.crypto.PortfolioService;
import com.example.application.services.crypto.TransactionService;
import com.example.application.utils.exceptions.InternalUnexpectedException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@SuppressWarnings("SameParameterValue")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
class TransactionsServiceTest extends AbstractTest {

	private final UserService userService;
	private final PortfolioService portfolioService;
	private final AssetBalanceService assetBalanceService;
	private final TransactionService transactionService;

	private User user;
	private Portfolio portfolio;
	private Asset asset;

	@Autowired
	public TransactionsServiceTest(UserService userService,
								   AssetService assetService,
								   PortfolioService portfolioService,
								   AssetBalanceService assetBalanceService,
								   TransactionService transactionService)
	{
		this.userService = userService;
		this.portfolioService = portfolioService;
		this.assetBalanceService = assetBalanceService;
		this.transactionService = transactionService;
	}

	@BeforeEach
	void setupUserAndPortfolio() {
		this.user = createUser("user", "test-email@email.com");
		this.portfolio = user.getActivePortfolio();
		this.asset = createAsset("BTC", 100_000);

	}

	@AfterEach
	void removeUser() {
		try {
			assetBalanceRepository.deleteAll();
			transactionRepository.deleteAll();
			portfolioRepository.deleteAll();
			categoryRepository.deleteAll();
			tagRepository.deleteAll();
			userRepository.deleteAll();
			assetRepository.deleteAll();
		} catch (Exception e) {
			throw new InternalUnexpectedException("Couldn't clear database properly", e);
		}
		Assertions.assertTrue(userService.findById(user.getId()).isEmpty(), "User was not deleted");
		Assertions.assertTrue(portfolioService.findByUser(user.getId()).isEmpty(), "User portfolios are not deleted");
		Assertions.assertTrue(categoryRepository.findByUser(user.getId()).isEmpty(), "User categories are not deleted");
		Assertions.assertTrue(tagRepository.findByUser(user.getId()).isEmpty(), "User tags are not deleted");
		Assertions.assertTrue(assetBalanceService.findByPortfolio(portfolio.getId()).isEmpty(), "User asset balances are not deleted");
	}

	@Test
	void saveTransactionSuccessfully() {
		CreateTransactionRequest request = CreateTransactionRequest.builder()
				.assetSymbol("BTC")
				.type(TransactionType.BUY)
				.marketPrice(BigDecimal.valueOf(100_000.00))
				.orderQuantity(BigDecimal.valueOf(0.0001))
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
				.marketPrice(BigDecimal.valueOf(100_000.00))
				.orderQuantity(BigDecimal.valueOf(0.0001))
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
	void deleteTransactionDoesNotExist() {
		Assertions.assertDoesNotThrow(() -> transactionService.delete(999L),
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
		TransactionDTO transferred = instrumentsFacadeService.transferTransaction(originalTransaction.getId(), otherPorfolio.getId());

		Assertions.assertEquals(otherPorfolio.getId(), transferred.getPortfolioId(),
				"Transaction should be transferred to new portfolio");
		Assertions.assertTrue(transactionService.findBy(portfolio.getId()).isEmpty(),
				"Original portfolio should have no transactions");
		Assertions.assertEquals(1, transactionService.findBy(otherPorfolio.getId()).size(),
				"Target portfolio should have the transferred transaction");
	}

	@Test
	void singleTransactionModifyTest() {
		// UNIT-TEST
		CreateTransactionRequest request = buyTransaction(100_000, 1);
		TransactionDTO originalTransaction = instrumentsFacadeService.createTransaction(request);
		LocalDateTime timeCreated = transactionService.findById(originalTransaction.getId())
				.orElseThrow()
				.getLastTimeUpdated();

		UpdateTransactionRequest updateRequest = new UpdateTransactionRequest(originalTransaction);
		updateRequest.setMarketPrice(BigDecimal.valueOf(80_000));
		updateRequest.setOrderQuantity(BigDecimal.valueOf(2));
		updateRequest.setDateTime(LocalDateTime.MAX);

		TransactionDTO updatedTransaction = instrumentsFacadeService.updateTransaction(updateRequest);
		LocalDateTime timeUpdated = transactionService.findById(originalTransaction.getId())
				.orElseThrow()
				.getLastTimeUpdated();

		Assertions.assertEquals(BigDecimal.valueOf(80_000), updatedTransaction.getMarketPrice(), "Market price was not updated");
		Assertions.assertEquals(BigDecimal.valueOf(2), updatedTransaction.getOrderQuantity(), "orderQuantity was not updated");
		Assertions.assertEquals(LocalDateTime.MAX, updatedTransaction.getDateTime(), "dateTime was not updated");
		Assertions.assertTrue(timeCreated != timeUpdated && timeUpdated.isAfter(timeCreated), "lastTimeUpdated is not correct");
	}

	//<editor-fold desc="Utils">
	private CreateTransactionRequest buyTransaction(double marketPrice, double orderQuantity) {
		return CreateTransactionRequest.builder()
				.assetSymbol(asset.getSymbol())
				.type(TransactionType.BUY)
				.marketPrice(BigDecimal.valueOf(marketPrice))
				.orderQuantity(BigDecimal.valueOf(orderQuantity))
				.portfolioId(portfolio.getId())
				.build();
	}

	private CreateTransactionRequest sellTransaction(double marketPrice, double orderQuantity) {
		return CreateTransactionRequest.builder()
				.assetSymbol(asset.getSymbol())
				.type(TransactionType.SELL)
				.marketPrice(BigDecimal.valueOf(marketPrice))
				.orderQuantity(BigDecimal.valueOf(orderQuantity))
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
