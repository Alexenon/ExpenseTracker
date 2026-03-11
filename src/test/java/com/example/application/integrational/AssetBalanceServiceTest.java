package com.example.application.integrational;

import com.example.application.Application;
import com.example.application.data.requests.CreateTransactionRequest;
import com.example.application.entities.User;
import com.example.application.entities.common.TransactionType;
import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.AssetBalance;
import com.example.application.entities.crypto.Portfolio;
import com.example.application.entities.crypto.Transaction;
import com.example.application.services.UserService;
import com.example.application.services.crypto.AssetBalanceService;
import com.example.application.services.crypto.AssetService;
import com.example.application.services.crypto.PortfolioService;
import com.example.application.services.crypto.TransactionService;
import com.example.application.utils.exceptions.InvalidBalanceAmountException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

@SuppressWarnings("DataFlowIssue")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
class AssetBalanceServiceTest extends AbstractTest {

	private final UserService userService;
	private final PortfolioService portfolioService;
	private final TransactionService transactionService;
	private final AssetBalanceService assetBalanceService;

	private User user;
	private Portfolio portfolio;
	private Asset asset;

	@Autowired
	public AssetBalanceServiceTest(
			UserService userService,
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
		this.asset = createAsset("BTC", 100_000);
		this.portfolio = user.getActivePortfolio();
	}

	@AfterEach
	void removeUser() {
		assetBalanceRepository.deleteAll();
		transactionRepository.deleteAll();
		portfolioRepository.deleteAll();

		userService.delete(user.getId());
		Assertions.assertTrue(userService.findById(user.getId()).isEmpty(), "User was not deleted");
		Assertions.assertTrue(portfolioService.findByUserId(user.getId()).isEmpty(), "Portfolios are not deleted");
		Assertions.assertTrue(assetBalanceService.findByPortfolio(portfolio.getId()).isEmpty(),
				"Asset balances are not deleted");
	}

	@Test
	void createNewAssetBalanceSuccessfully() {
		AssetBalance created = instrumentsFacadeService.createAssetBalance(portfolio, asset);

		Assertions.assertNotNull(created.getId(), "AssetBalance ID should be generated");
		Assertions.assertEquals(portfolio.getId(), created.getPortfolio().getId(), "Should belong to correct portfolio");
		Assertions.assertEquals("BTC", created.getAsset().getSymbol(), "Asset symbol should match");
		Assertions.assertEquals(0.0, created.getAmount(), "Initial amount should be 0");
		Assertions.assertEquals(0.0, created.getAvgBuyPrice(), "Initial avgBuyPrice should be 0");
	}

	@Test
	void findByPortfolioAndAssetShouldReturnExistingBalance() {
		AssetBalance created = assetBalanceService.createNew(portfolio, asset);

		AssetBalance found = assetBalanceService.findByPortfolioAndAsset(portfolio.getId(), asset.getSymbol())
				.orElseThrow(() -> new EntityNotFoundException("Asset balance not found"));

		Assertions.assertEquals(created.getId(), found.getId(), "Should find existing asset balance");
		Assertions.assertEquals(asset.getSymbol(), found.getAsset().getSymbol(), "Asset symbol should match");
	}

	@Test
	void updateShouldModifyExistingBalance() {
		Long portfolioId = portfolio.getId();
		Asset asset = createAsset("BTC", 100_000);
		Transaction transaction = createTransaction("BTC", 100_000, portfolioId);

		Assertions.assertTrue(transactionService.findById(transaction.getId()).isPresent(), "Transaction was not created");

		AssetBalance assetBalance = assetBalanceService.findByPortfolioAndAsset(portfolioId, asset.getId())
				.orElseThrow(() -> new EntityNotFoundException("Asset Balance is missing"));

		Assertions.assertTrue(assetBalanceService.findById(assetBalance.getId()).isPresent(),
				"Asset Balance is missing in the database");
	}

	@Test
	void sellingAllTokensShouldKeepAssetBalanceInDatabase() {
		CreateTransactionRequest buyRequest = buyTransaction(100_000, 1);
		CreateTransactionRequest sellRequest = sellTransaction(120_000, 1);

		instrumentsFacadeService.createTransaction(buyRequest);
		instrumentsFacadeService.createTransaction(sellRequest);

		Optional<AssetBalance> assetBalance = assetBalanceService.findByPortfolioAndAsset(portfolio.getId(), asset.getId());
		Assertions.assertTrue(assetBalance.isPresent(),
				"Asset Balance after selling entire asset amount is deleted from database => " + assetBalance);
	}

	@Test
	void invalidTransactionShouldNotCreateAssetBalance() {
		CreateTransactionRequest invalidRequest = buyTransaction(100_000, -100);

		Assertions.assertThrows(InvalidBalanceAmountException.class, () -> instrumentsFacadeService.createTransaction(invalidRequest),
				"Invalid transaction was created");

		Assertions.assertTrue(assetBalanceRepository.findByPortfolioAndAsset(portfolio.getId(), asset.getId()).isEmpty(),
				"Asset balance was created for invalid transaction");
	}

	@Test
	void assetAmountBalanceCannotBeLessThanZeroAfterSellTransaction() {
		CreateTransactionRequest buyRequest = buyTransaction(100_000, 1);
		CreateTransactionRequest sellRequest = sellTransaction(120_000, 2);

		Assertions.assertDoesNotThrow(() -> instrumentsFacadeService.createTransaction(buyRequest));
		Assertions.assertThrows(InvalidBalanceAmountException.class, () -> instrumentsFacadeService.createTransaction(sellRequest),
				"Should not allow selling transaction with amount more than in portfolio");
	}

	@Test
	void checkAssetBalanceAfterSellOfAsset() {
		CreateTransactionRequest buyRequest = buyTransaction(100_000, 1);
		CreateTransactionRequest sellRequest = sellTransaction(120_000, 0.5);

		Assertions.assertDoesNotThrow(() -> instrumentsFacadeService.createTransaction(buyRequest));
		Assertions.assertDoesNotThrow(() -> instrumentsFacadeService.createTransaction(sellRequest));

		AssetBalance assetBalance = assetBalanceService.findByPortfolioAndAsset(portfolio.getId(), asset.getId())
				.orElseThrow(() -> new EntityNotFoundException("Asset Balance not found"));

		Assertions.assertEquals(50_000, assetBalance.getCost(), "cost doesn't match");
		Assertions.assertEquals(0.5, assetBalance.getAmount(), "tokens amount doesn't match");
		Assertions.assertEquals(100_000, assetBalance.getAvgBuyPrice(), "avgBuyPrice doesn't match");
		Assertions.assertEquals(120_000, assetBalance.getAvgSellPrice(), "avgSellPrice doesn't match");
		Assertions.assertEquals(10_000, assetBalance.getTotalRealized(), "totalRealized doesn't match");
	}

	void checkMultipleTransactionsSoldPartialTest() {
		CreateTransactionRequest buyRequest1 = buyTransaction(100_000, 1);
		CreateTransactionRequest buyRequest2 = buyTransaction(90_000, 2);
		CreateTransactionRequest buyRequest3 = buyTransaction(80_000, 3);

		CreateTransactionRequest sellRequest1 = sellTransaction(100_000, 2);
		CreateTransactionRequest sellRequest2 = sellTransaction(120_000, 2);

		Assertions.assertDoesNotThrow(() -> instrumentsFacadeService.createTransaction(buyRequest1));
		Assertions.assertDoesNotThrow(() -> instrumentsFacadeService.createTransaction(buyRequest2));
		Assertions.assertDoesNotThrow(() -> instrumentsFacadeService.createTransaction(buyRequest3));

		Assertions.assertDoesNotThrow(() -> instrumentsFacadeService.createTransaction(sellRequest1));
		Assertions.assertDoesNotThrow(() -> instrumentsFacadeService.createTransaction(sellRequest2));

		AssetBalance assetBalance = assetBalanceService.findByPortfolioAndAsset(portfolio.getId(), asset.getId())
				.orElseThrow(() -> new EntityNotFoundException("Asset Balance not found"));

		Assertions.assertEquals(90_000, assetBalance.getCost(), "cost doesn't match");
		Assertions.assertEquals(2, assetBalance.getAmount(), "tokens amount doesn't match");
		Assertions.assertEquals(86666.67, assetBalance.getAvgBuyPrice(), 1e-4, "avgBuyPrice doesn't match");
		Assertions.assertEquals(110_000, assetBalance.getAvgSellPrice(), "avgSellPrice doesn't match");
		Assertions.assertEquals(10_000, assetBalance.getTotalRealized(), "totalRealized doesn't match");
	}


	@Test
	void checkMultipleTransactionsSoldAllTest() {
		CreateTransactionRequest buyRequest1 = buyTransaction(100_000, 1);
		CreateTransactionRequest buyRequest2 = buyTransaction(90_000, 2);
		CreateTransactionRequest buyRequest3 = buyTransaction(80_000, 3);

		CreateTransactionRequest sellRequest1 = sellTransaction(100_000, 3);
		CreateTransactionRequest sellRequest2 = sellTransaction(120_000, 3);

		Assertions.assertDoesNotThrow(() -> instrumentsFacadeService.createTransaction(buyRequest1));
		Assertions.assertDoesNotThrow(() -> instrumentsFacadeService.createTransaction(buyRequest2));
		Assertions.assertDoesNotThrow(() -> instrumentsFacadeService.createTransaction(buyRequest3));

		Assertions.assertDoesNotThrow(() -> instrumentsFacadeService.createTransaction(sellRequest1));
		Assertions.assertDoesNotThrow(() -> instrumentsFacadeService.createTransaction(sellRequest2));

		AssetBalance assetBalance = assetBalanceService.findByPortfolioAndAsset(portfolio.getId(), asset.getId())
				.orElseThrow(() -> new EntityNotFoundException("Asset Balance not found"));

		Assertions.assertEquals(0, assetBalance.getCost(), "cost doesn't match");
		Assertions.assertEquals(0, assetBalance.getAmount(), "tokens amount doesn't match");
		Assertions.assertEquals(86666.67, assetBalance.getAvgBuyPrice(), 1e-4, "avgBuyPrice doesn't match");
		Assertions.assertEquals(110_000, assetBalance.getAvgSellPrice(), "avgSellPrice doesn't match");
		Assertions.assertEquals(10_000, assetBalance.getTotalRealized(), "totalRealized doesn't match");
	}

	@Test
	void findByNonExistentPortfolioShouldReturnEmpty() {
		Assertions.assertTrue(assetBalanceService.findByPortfolio(999L).isEmpty(),
				"Non-existent portfolio should return no balances");
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
	//</editor-fold>


}
