package com.example.application.integrational;

import com.example.application.Application;
import com.example.application.data.requests.CreateTransactionRequest;
import com.example.application.entities.User;
import com.example.application.entities.common.TransactionType;
import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.AssetBalance;
import com.example.application.entities.crypto.Portfolio;
import com.example.application.services.UserService;
import com.example.application.services.crypto.AssetBalanceService;
import com.example.application.services.crypto.PortfolioService;
import com.example.application.utils.exceptions.InternalUnexpectedException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Objects;

@SuppressWarnings("SameParameterValue")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
class AssetBalanceServiceTest extends AbstractTest {

	private final UserService userService;
	private final PortfolioService portfolioService;
	private final AssetBalanceService assetBalanceService;

	private User user;
	private Portfolio portfolio;
	private Asset asset;

	@Autowired
	public AssetBalanceServiceTest(
			UserService userService,
			PortfolioService portfolioService,
			AssetBalanceService assetBalanceService)
	{
		this.userService = userService;
		this.portfolioService = portfolioService;
		this.assetBalanceService = assetBalanceService;
	}

	@BeforeEach
	void setupUserAndPortfolio() {
		this.user = createUser("user", "test-email@email.com");
		this.asset = createAsset("BTC", 100_000);
		this.portfolio = Objects.requireNonNull(user.getActivePortfolio(), "user default portfolio");
	}

	@AfterEach
	void removeUser() {
		try {
			assetBalanceRepository.deleteAll();
			transactionRepository.deleteAll();
			portfolioRepository.deleteAll();
			assetRepository.deleteAll();
			instrumentsFacadeService.deleteUser(user.getId());
		} catch (Exception e) {
			throw new InternalUnexpectedException("Couldn't clear database properly", e);
		}

		Assertions.assertTrue(userService.findById(user.getId()).isEmpty());
		Assertions.assertTrue(portfolioService.findByUser(user.getId()).isEmpty());
		Assertions.assertTrue(assetBalanceService.findByPortfolio(portfolio.getId()).isEmpty());
	}

	@Test
	void createNewAssetBalanceSuccessfully() {
		AssetBalance created = instrumentsFacadeService.createAssetBalance(portfolio, asset);

		Assertions.assertNotNull(created.getId());
		Assertions.assertEquals(portfolio.getId(), created.getPortfolio().getId());
		Assertions.assertEquals("BTC", created.getAsset().getSymbol());

		assertBigDecimalEquals(BigDecimal.ZERO, created.getAmount(), "Initial amount should be 0");
		assertBigDecimalEquals(BigDecimal.ZERO, created.getAvgBuyPrice(), "Initial avgBuyPrice should be 0");
	}

	@Test
	void checkBalanceAfterOneBuy() {
		CreateTransactionRequest buyRequest = buyTransaction(100_000, 1);
		Assertions.assertDoesNotThrow(() -> instrumentsFacadeService.createTransaction(buyRequest));

		AssetBalance assetBalance = assetBalanceService
				.findByPortfolioAndAsset(portfolio.getId(), asset.getId())
				.orElseThrow();

		assertBigDecimalEquals(BigDecimal.valueOf(100_000), assetBalance.getCost(), "cost");
		assertBigDecimalEquals(BigDecimal.ONE, assetBalance.getAmount(), "amount");

		assertBigDecimalEquals(BigDecimal.valueOf(100_000), assetBalance.getAvgBuyPrice(), "avgBuyPrice");
		assertBigDecimalEquals(BigDecimal.valueOf(100_000), assetBalance.getTotalBuyCost(), "totalBuyCost");
		assertBigDecimalEquals(BigDecimal.ONE, assetBalance.getTotalBoughtQuantity(), "totalBoughtQuantity");

		assertBigDecimalEquals(BigDecimal.ZERO, assetBalance.getAvgSellPrice(), "avgSellPrice");
		assertBigDecimalEquals(BigDecimal.ZERO, assetBalance.getTotalSellValue(), "totalSellValue");
		assertBigDecimalEquals(BigDecimal.ZERO, assetBalance.getTotalSoldQuantity(), "totalSoldQuantity");
		assertBigDecimalEquals(BigDecimal.ZERO, assetBalance.getTotalRealizedProfit(), "totalRealizedProfit");
	}

	@Test
	void checkAssetBalanceAfterEntireSellTest() {

		CreateTransactionRequest buyRequest = buyTransaction(100_000, 1);
		CreateTransactionRequest sellRequest = sellTransaction(120_000, 1);

		Assertions.assertDoesNotThrow(() -> instrumentsFacadeService.createTransaction(buyRequest));
		Assertions.assertDoesNotThrow(() -> instrumentsFacadeService.createTransaction(sellRequest));

		AssetBalance assetBalance = assetBalanceService
				.findByPortfolioAndAsset(portfolio.getId(), asset.getId())
				.orElseThrow();

		assertBigDecimalEquals(BigDecimal.ZERO, assetBalance.getCost(), "cost");
		assertBigDecimalEquals(BigDecimal.ZERO, assetBalance.getAmount(), "amount");

		assertBigDecimalEquals(BigDecimal.valueOf(100_000), assetBalance.getAvgBuyPrice(), "avgBuyPrice");
		assertBigDecimalEquals(BigDecimal.valueOf(100_000), assetBalance.getTotalBuyCost(), "totalBuyCost");
		assertBigDecimalEquals(BigDecimal.ONE, assetBalance.getTotalBoughtQuantity(), "totalBoughtQuantity");

		assertBigDecimalEquals(BigDecimal.valueOf(120_000), assetBalance.getAvgSellPrice(), "avgSellPrice");
		assertBigDecimalEquals(BigDecimal.valueOf(120_000), assetBalance.getTotalSellValue(), "totalSellValue");
		assertBigDecimalEquals(BigDecimal.ONE, assetBalance.getTotalSoldQuantity(), "totalSoldQuantity");
		assertBigDecimalEquals(BigDecimal.valueOf(20_000), assetBalance.getTotalRealizedProfit(), "totalRealizedProfit");
	}

	@Test
	void findByNonExistentPortfolioShouldReturnEmpty() {
		Assertions.assertTrue(assetBalanceService.findByPortfolio(999L).isEmpty());
	}

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
}