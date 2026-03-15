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

import java.util.Objects;
import java.util.Optional;

@SuppressWarnings("DataFlowIssue")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
class AssetBalanceServiceTest extends AbstractTest {

	private static final double DELTA = 0.01;

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
		this.portfolio = Objects.requireNonNull(user.getActivePortfolio(), "user default portfolio");
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
		Transaction transaction = createTransaction(asset, 100_000, portfolioId);

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
	void checkBalanceAfterOneBuy() {
		CreateTransactionRequest buyRequest = buyTransaction(100_000, 1);

		Assertions.assertDoesNotThrow(() -> instrumentsFacadeService.createTransaction(buyRequest));

		AssetBalance assetBalance = assetBalanceService.findByPortfolioAndAsset(portfolio.getId(), asset.getId())
				.orElseThrow(() -> new EntityNotFoundException("Asset Balance not found"));

		Assertions.assertEquals(100_000, assetBalance.getCost(), "cost doesn't match");
		Assertions.assertEquals(1, assetBalance.getAmount(), "tokens amount doesn't match");

		Assertions.assertEquals(100_000, assetBalance.getAvgBuyPrice(), "avgBuyPrice doesn't match");
		Assertions.assertEquals(100_000, assetBalance.getTotalBuyCost(), "totalBuyCost amount doesn't match");
		Assertions.assertEquals(1, assetBalance.getTotalBoughtQuantity(), "totalBoughtQuantity amount doesn't match");

		Assertions.assertEquals(0, assetBalance.getAvgSellPrice(), "avgSellPrice doesn't match");
		Assertions.assertEquals(0, assetBalance.getTotalSellValue(), "totalSellValue amount doesn't match");
		Assertions.assertEquals(0, assetBalance.getTotalSoldQuantity(), "totalSoldQuantity amount doesn't match");
		Assertions.assertEquals(0, assetBalance.getTotalRealizedProfit(), "totalRealized doesn't match");
	}

	@Test
	void checkBalanceAfterMultipleBuys() {
		CreateTransactionRequest buyRequest1 = buyTransaction(100_000, 1);
		CreateTransactionRequest buyRequest2 = buyTransaction(80_000, 1);
		CreateTransactionRequest buyRequest3 = buyTransaction(60_000, 2);

		Assertions.assertDoesNotThrow(() -> instrumentsFacadeService.createTransaction(buyRequest1));
		Assertions.assertDoesNotThrow(() -> instrumentsFacadeService.createTransaction(buyRequest2));
		Assertions.assertDoesNotThrow(() -> instrumentsFacadeService.createTransaction(buyRequest3));

		AssetBalance assetBalance = assetBalanceService.findByPortfolioAndAsset(portfolio.getId(), asset.getId())
				.orElseThrow(() -> new EntityNotFoundException("Asset Balance not found"));

		Assertions.assertEquals(300_000, assetBalance.getCost(), "cost doesn't match");
		Assertions.assertEquals(4, assetBalance.getAmount(), "tokens amount doesn't match");

		Assertions.assertEquals(75_000, assetBalance.getAvgBuyPrice(), "avgBuyPrice doesn't match");
		Assertions.assertEquals(300_000, assetBalance.getTotalBuyCost(), "totalBuyCost amount doesn't match");
		Assertions.assertEquals(4, assetBalance.getTotalBoughtQuantity(), "totalBoughtQuantity amount doesn't match");

		Assertions.assertEquals(0, assetBalance.getAvgSellPrice(), "avgSellPrice doesn't match");
		Assertions.assertEquals(0, assetBalance.getTotalSellValue(), "totalSellValue amount doesn't match");
		Assertions.assertEquals(0, assetBalance.getTotalSoldQuantity(), "totalSoldQuantity amount doesn't match");
		Assertions.assertEquals(0, assetBalance.getTotalRealizedProfit(), "totalRealized doesn't match");
	}

	@Test
	void checkAssetBalanceAfterPartialSellTest() {
		CreateTransactionRequest buyRequest = buyTransaction(100_000, 1);
		CreateTransactionRequest sellRequest = sellTransaction(120_000, 0.5);

		Assertions.assertDoesNotThrow(() -> instrumentsFacadeService.createTransaction(buyRequest));
		Assertions.assertDoesNotThrow(() -> instrumentsFacadeService.createTransaction(sellRequest));

		AssetBalance assetBalance = assetBalanceService.findByPortfolioAndAsset(portfolio.getId(), asset.getId())
				.orElseThrow(() -> new EntityNotFoundException("Asset Balance not found"));

		Assertions.assertEquals(50_000, assetBalance.getCost(), "cost doesn't match");
		Assertions.assertEquals(0.5, assetBalance.getAmount(), "tokens amount doesn't match");

		Assertions.assertEquals(100_000, assetBalance.getAvgBuyPrice(), "avgBuyPrice doesn't match");
		Assertions.assertEquals(100_000, assetBalance.getTotalBuyCost(), "totalBuyCost amount doesn't match");
		Assertions.assertEquals(1, assetBalance.getTotalBoughtQuantity(), "totalBoughtQuantity amount doesn't match");

		Assertions.assertEquals(120_000, assetBalance.getAvgSellPrice(), "avgSellPrice doesn't match");
		Assertions.assertEquals(60_000, assetBalance.getTotalSellValue(), "totalSellValue amount doesn't match");
		Assertions.assertEquals(0.5, assetBalance.getTotalSoldQuantity(), "totalSoldQuantity amount doesn't match");
		Assertions.assertEquals(10_000, assetBalance.getTotalRealizedProfit(), "totalRealized doesn't match");

	}

	@Test
	void checkAssetBalanceAfterEntireSellTest() {
		CreateTransactionRequest buyRequest = buyTransaction(100_000, 1);
		CreateTransactionRequest sellRequest = sellTransaction(120_000, 1);

		Assertions.assertDoesNotThrow(() -> instrumentsFacadeService.createTransaction(buyRequest));
		Assertions.assertDoesNotThrow(() -> instrumentsFacadeService.createTransaction(sellRequest));

		AssetBalance assetBalance = assetBalanceService.findByPortfolioAndAsset(portfolio.getId(), asset.getId())
				.orElseThrow(() -> new EntityNotFoundException("Asset Balance not found"));

		Assertions.assertEquals(0, assetBalance.getCost(), "cost doesn't match");
		Assertions.assertEquals(0, assetBalance.getAmount(), "tokens amount doesn't match");

		Assertions.assertEquals(100_000, assetBalance.getAvgBuyPrice(), "avgBuyPrice doesn't match");
		Assertions.assertEquals(100_000, assetBalance.getTotalBuyCost(), "totalBuyCost amount doesn't match");
		Assertions.assertEquals(1, assetBalance.getTotalBoughtQuantity(), "totalBoughtQuantity amount doesn't match");

		Assertions.assertEquals(120_000, assetBalance.getAvgSellPrice(), "avgSellPrice doesn't match");
		Assertions.assertEquals(120_000, assetBalance.getTotalSellValue(), "totalSellValue amount doesn't match");
		Assertions.assertEquals(1, assetBalance.getTotalSoldQuantity(), "totalSoldQuantity amount doesn't match");
		Assertions.assertEquals(20_000, assetBalance.getTotalRealizedProfit(), "totalRealized doesn't match");

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

		Assertions.assertEquals(173_333.33, assetBalance.getCost(), "cost doesn't match");
		Assertions.assertEquals(2, assetBalance.getAmount(), "tokens amount doesn't match");

		Assertions.assertEquals(86_666.67, assetBalance.getAvgBuyPrice(), "avgBuyPrice doesn't match");
		Assertions.assertEquals(520_000, assetBalance.getTotalBuyCost(), "totalBuyCost amount doesn't match");
		Assertions.assertEquals(6, assetBalance.getTotalBoughtQuantity(), "totalBoughtQuantity amount doesn't match");

		Assertions.assertEquals(110_000, assetBalance.getAvgSellPrice(), "avgSellPrice doesn't match");
		Assertions.assertEquals(440_000, assetBalance.getTotalSellValue(), "totalSellValue amount doesn't match");
		Assertions.assertEquals(4, assetBalance.getTotalSoldQuantity(), "totalSoldQuantity amount doesn't match");
		Assertions.assertEquals(93_333.33, assetBalance.getTotalRealizedProfit(), "totalRealized doesn't match");

	}

	@Test
	void checkMultipleTransactionsSoldEntireTest() {
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

		Assertions.assertEquals(86666.67, assetBalance.getAvgBuyPrice(), DELTA, "avgBuyPrice doesn't match");
		Assertions.assertEquals(520_000, assetBalance.getTotalBuyCost(), "totalBuyCost amount doesn't match");
		Assertions.assertEquals(6, assetBalance.getTotalBoughtQuantity(), "totalBoughtQuantity amount doesn't match");

		Assertions.assertEquals(110_000, assetBalance.getAvgSellPrice(), "avgSellPrice doesn't match");
		Assertions.assertEquals(660_000, assetBalance.getTotalSellValue(), "totalSellValue amount doesn't match");
		Assertions.assertEquals(6, assetBalance.getTotalSoldQuantity(), "totalSoldQuantity amount doesn't match");
		Assertions.assertEquals(140_000, assetBalance.getTotalRealizedProfit(), "totalRealized doesn't match");
	}

//	@Test
//	void updateAssetBalanceIfSingleTransactionWasUpdated() {
//		CreateTransactionRequest createRequest = buyTransaction(100_000, 1);
//		TransactionDTO originalTransaction = instrumentsFacadeService.createTransaction(createRequest);
//
//		UpdateTransactionRequest updateRequest = new UpdateTransactionRequest(originalTransaction);
//		updateRequest.setMarketPrice(80_000);
//		updateRequest.setOrderQuantity(2);
//		TransactionDTO updatedTransaction = instrumentsFacadeService.updateTransaction(updateRequest);
//
//		AssetBalance assetBalance = assetBalanceService.findByPortfolioAndAsset(portfolio.getId(), asset.getId())
//				.orElseThrow(() -> new EntityNotFoundException("Asset Balance not found"));
//
//		Assertions.assertEquals(80_000, assetBalance.getCost(), "cost doesn't match");
//		Assertions.assertEquals(2, assetBalance.getAmount(), "tokens amount doesn't match");
//		Assertions.assertEquals(80_000, assetBalance.getAvgBuyPrice(), DELTA, "avgBuyPrice doesn't match");
//		Assertions.assertEquals(80_000, assetBalance.getTotalBuyCost(), "totalBuyCost amount doesn't match");
//		Assertions.assertEquals(2, assetBalance.getTotalBoughtQuantity(), "totalBoughtQuantity amount doesn't match");
//	}
//
//	@Test
//	void updateAssetBalanceIfMultipleTransactionsWereUpdated() {
//		CreateTransactionRequest createRequest1 = buyTransaction(100_000, 1);
//		CreateTransactionRequest createRequest2 = buyTransaction(100_000, 1);
//		CreateTransactionRequest createRequest3 = buyTransaction(80_000, 3);
//
//		TransactionDTO transaction1 = instrumentsFacadeService.createTransaction(createRequest1);
//		TransactionDTO transaction2 = instrumentsFacadeService.createTransaction(createRequest2);
//		TransactionDTO transaction3 = instrumentsFacadeService.createTransaction(createRequest3);
//
//		// Updating second transaction to have market price
//		UpdateTransactionRequest updateRequest = new UpdateTransactionRequest(transaction2);
//		updateRequest.setMarketPrice(80_000);
//		updateRequest.setOrderQuantity(2);
//		TransactionDTO updatedTransaction = instrumentsFacadeService.updateTransaction(updateRequest);
//
//		AssetBalance assetBalance = assetBalanceService.findByPortfolioAndAsset(portfolio.getId(), asset.getId())
//				.orElseThrow(() -> new EntityNotFoundException("Asset Balance not found"));
//
//		Assertions.assertEquals(173_333.33, assetBalance.getCost(), "cost doesn't match");
//		Assertions.assertEquals(2, assetBalance.getAmount(), "tokens amount doesn't match");
//
//		Assertions.assertEquals(86_666.67, assetBalance.getAvgBuyPrice(), "avgBuyPrice doesn't match");
//		Assertions.assertEquals(520_000, assetBalance.getTotalBuyCost(), "totalBuyCost amount doesn't match");
//		Assertions.assertEquals(6, assetBalance.getTotalBoughtQuantity(), "totalBoughtQuantity amount doesn't match");
//
//		Assertions.assertEquals(110_000, assetBalance.getAvgSellPrice(), "avgSellPrice doesn't match");
//		Assertions.assertEquals(440_000, assetBalance.getTotalSellValue(), "totalSellValue amount doesn't match");
//		Assertions.assertEquals(4, assetBalance.getTotalSoldQuantity(), "totalSoldQuantity amount doesn't match");
//		Assertions.assertEquals(93_333.33, assetBalance.getTotalRealizedProfit(), "totalRealized doesn't match");
//	}
//
//	@Test
//	void deleteAssetBalanceIfOnlyRemainingTransactionWasDeleted() {
//		CreateTransactionRequest createRequest = buyTransaction(100_000, 1);
//		TransactionDTO transaction = instrumentsFacadeService.createTransaction(createRequest);
//		instrumentsFacadeService.deleteTransaction(transaction.getId());
//
//		Optional<AssetBalance> assetBalance = assetBalanceService.findByPortfolioAndAsset(portfolio.getId(), asset.getId());
//		Assertions.assertTrue(assetBalance.isEmpty(), "Asset Balance should be deleted, as the remaining transaction was deleted");
//	}
//
//	@Test
//	void updateAssetBalanceIfOnlyTransactionWasDeleted() {
//		CreateTransactionRequest buyRequest1 = buyTransaction(100_000, 1);
//		CreateTransactionRequest buyRequest2 = buyTransaction(120_000, 1);
//		CreateTransactionRequest buyRequest3 = buyTransaction(200_000, 2);
//
//		TransactionDTO buyTransaction1 = instrumentsFacadeService.createTransaction(buyRequest1);
//		TransactionDTO buyTransaction2 = instrumentsFacadeService.createTransaction(buyRequest2);
//		TransactionDTO buyTransaction3 = instrumentsFacadeService.createTransaction(buyRequest3);
//		instrumentsFacadeService.deleteTransaction(buyTransaction3.getId());
//
//		AssetBalance assetBalance = assetBalanceService.findByPortfolioAndAsset(portfolio.getId(), asset.getId())
//				.orElseThrow(() -> new EntityNotFoundException("Asset Balance not found"));
//
//		Assertions.assertEquals(220_000, assetBalance.getCost(), "cost doesn't match");
//		Assertions.assertEquals(2, assetBalance.getAmount(), "tokens amount doesn't match");
//		Assertions.assertEquals(110_000, assetBalance.getAvgBuyPrice(), "avgBuyPrice doesn't match");
//		Assertions.assertEquals(220_000, assetBalance.getTotalBuyCost(), "totalBuyCost amount doesn't match");
//		Assertions.assertEquals(2, assetBalance.getTotalBoughtQuantity(), "totalBoughtQuantity amount doesn't match");
//	}
//
//	@Test
//	void updateAssetBalanceIfTransactionWasCopied() {
//		Portfolio secondPortfolio = createPortfolio("Second Portfolio", user.getId());
//
//		CreateTransactionRequest createRequest = buyTransaction(100_000, 1);
//		TransactionDTO originalTransaction = instrumentsFacadeService.createTransaction(createRequest);
//		TransactionDTO copiedTransaction = instrumentsFacadeService.transferTransaction(originalTransaction.getId(), secondPortfolio.getId());
//
//		Optional<AssetBalance> assetBalanceDefaultPortfolio = assetBalanceService.findByPortfolioAndAsset(portfolio.getId(), asset.getId());
//		Assertions.assertTrue(assetBalanceDefaultPortfolio.isEmpty(), "Asset Balance should be deleted, as the remaining transaction was deleted");
//
//		AssetBalance assetBalanceSecondPortfolio = assetBalanceService.findByPortfolioAndAsset(portfolio.getId(), asset.getId())
//				.orElseThrow(() -> new EntityNotFoundException("Asset Balance not found for second portfolio"));
//
//		Assertions.assertEquals(100_000, assetBalanceSecondPortfolio.getCost(),
//				"cost doesn't match for asset balance on second portfolio");
//		Assertions.assertEquals(1, assetBalanceSecondPortfolio.getAmount(),
//				"tokens amount doesn't match for asset balance on second portfolio");
//		Assertions.assertEquals(100_000, assetBalanceSecondPortfolio.getAvgBuyPrice(),
//				"avgBuyPrice doesn't match for asset balance on second portfolio");
//		Assertions.assertEquals(100_000, assetBalanceSecondPortfolio.getTotalBuyCost(),
//				"totalBuyCost amount doesn't match for asset balance on second portfolio");
//		Assertions.assertEquals(1, assetBalanceSecondPortfolio.getTotalBoughtQuantity(),
//				"totalBoughtQuantity amount doesn't match for asset balance on second portfolio");
//	}
//
//	@Test
//	void updateAssetBalanceIfTransactionWasReplaced() {
//		Portfolio secondPortfolio = createPortfolio("Second Portfolio", user.getId());
//
//		CreateTransactionRequest createRequest = buyTransaction(100_000, 1);
//		TransactionDTO originalTransaction = instrumentsFacadeService.createTransaction(createRequest);
//		TransactionDTO copiedTransaction = instrumentsFacadeService.transferTransaction(originalTransaction.getId(), secondPortfolio.getId(), true);
//
//		Optional<AssetBalance> assetBalanceDefaultPortfolio = assetBalanceService.findByPortfolioAndAsset(portfolio.getId(), asset.getId());
//
//		Assertions.assertTrue(assetBalanceDefaultPortfolio.isEmpty(), "Asset Balance should be deleted, as it was only transaction");
//
//		AssetBalance assetBalanceSecondPortfolio = assetBalanceService.findByPortfolioAndAsset(portfolio.getId(), asset.getId())
//				.orElseThrow(() -> new EntityNotFoundException("Asset Balance not found for second portfolio"));
//		Assertions.assertEquals(100_000, assetBalanceSecondPortfolio.getCost(),
//				"cost doesn't match for asset balance on second portfolio");
//		Assertions.assertEquals(1, assetBalanceSecondPortfolio.getAmount(),
//				"tokens amount doesn't match for asset balance on second portfolio");
//		Assertions.assertEquals(100_000, assetBalanceSecondPortfolio.getAvgBuyPrice(),
//				"avgBuyPrice doesn't match for asset balance on second portfolio");
//		Assertions.assertEquals(100_000, assetBalanceSecondPortfolio.getTotalBuyCost(),
//				"totalBuyCost amount doesn't match for asset balance on second portfolio");
//		Assertions.assertEquals(1, assetBalanceSecondPortfolio.getTotalBoughtQuantity(),
//				"totalBoughtQuantity amount doesn't match for asset balance on second portfolio");
//	}

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
