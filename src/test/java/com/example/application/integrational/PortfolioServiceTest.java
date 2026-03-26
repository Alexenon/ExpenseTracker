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
import com.example.application.services.UserService;
import com.example.application.services.crypto.*;
import com.example.application.utils.exceptions.InvalidDataException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

@SuppressWarnings({"DataFlowIssue", "SameParameterValue"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
class PortfolioServiceTest extends AbstractTest {

	private final InstrumentsFacadeService instrumentsFacadeService;
	private final UserService userService;
	private final PortfolioService portfolioService;
	private final TransactionService transactionService;
	private final AssetBalanceService assetBalanceService;

	private User user;

	@Autowired
	public PortfolioServiceTest(InstrumentsFacadeService instrumentsFacadeService,
								UserService userService,
								PortfolioService portfolioService,
								TransactionService transactionService,
								AssetBalanceService assetBalanceService,
								AssetService assetService)
	{
		this.instrumentsFacadeService = instrumentsFacadeService;
		this.userService = userService;
		this.transactionService = transactionService;
		this.assetBalanceService = assetBalanceService;
		this.portfolioService = portfolioService;
	}

	@BeforeEach
	void setupUser() {
		this.user = createUser("john", "john@test.com");
		Assertions.assertTrue(portfolioService.findByNameAndUser("Main", user.getId()).isPresent(),
				"Created user doesn't have default portfolio attached");
	}

	@AfterEach
	void removeUser() {
		portfolioRepository.deleteAll();
		transactionRepository.deleteAll();
		assetBalanceRepository.deleteAll();
		assetRepository.deleteAll();
		userRepository.deleteAll();
	}

	@Test
	void savePortfolioSuccessfully() {
		CreatePortfolioRequest request = CreatePortfolioRequest.builder()
				.portfolioName("Crypto")
				.userId(user.getId())
				.build();

		PortfolioDTO saved = instrumentsFacadeService.createPortfolio(request);
		Assertions.assertNotNull(saved.getId(), "Portfolio ID should be generated");
		Assertions.assertEquals("Crypto", saved.getName(), "Portfolio name should match");
		Assertions.assertEquals(user.getId(), saved.getUserId(), "Portfolio should belong to the user");
	}

	@Test
	void shouldNotAllowDuplicatePortfolioNameForUser() {
		CreatePortfolioRequest request = CreatePortfolioRequest.builder()
				.portfolioName("Crypto")
				.userId(user.getId())
				.build();

		Assertions.assertDoesNotThrow(() -> instrumentsFacadeService.createPortfolio(request),
				"First portfolio was not created");

		Assertions.assertThrows(
				IllegalArgumentException.class,
				() -> instrumentsFacadeService.createPortfolio(request),
				"Duplicate portfolio names for same user should not be allowed"
		);
	}

	@Test
	void userShouldHaveCreatedPortfolio() {
		CreatePortfolioRequest request = CreatePortfolioRequest.builder()
				.portfolioName("Crypto")
				.userId(user.getId())
				.build();

		PortfolioDTO saved = instrumentsFacadeService.createPortfolio(request);
		List<Portfolio> portfolios = portfolioService.findByUserId(user.getId());
		Assertions.assertFalse(portfolios.isEmpty(), "User should have portfolios");
		Assertions.assertEquals(2, portfolios.size(), "User should have default + new portfolio");
	}

	@Test
	void setPortfolioAsActiveShouldUpdateUser() {
		CreatePortfolioRequest request = CreatePortfolioRequest.builder()
				.portfolioName("Trading")
				.userId(user.getId())
				.build();

		PortfolioDTO newPortfolio = instrumentsFacadeService.createPortfolio(request);
		portfolioService.setPortfolioAsActive(newPortfolio.getId());

		String activePortfolioName = userService.findById(user.getId())
				.orElseThrow(() -> new EntityNotFoundException("User not found (deleted ?)"))
				.getActivePortfolio()
				.getName();
		Assertions.assertEquals("Trading", activePortfolioName, "Active portfolio should be updated");
	}

	@Test
	void deletePortfolioShouldDeleteAssetBalancesAndTransactionsAssociated() {
		CreatePortfolioRequest request = createPortfolioRequest("Second portfolio");

		PortfolioDTO secondPortfolio = instrumentsFacadeService.createPortfolio(request);

		// Creating asset and transaction on that asset
		Asset asset = createAsset("BTC", 100_000);
		CreateTransactionRequest transactionRequest = buyTransaction(asset, 100_000, 1, secondPortfolio.getId());
		TransactionDTO transaction = instrumentsFacadeService.createTransaction(transactionRequest);

		instrumentsFacadeService.deletePortfolio(secondPortfolio.getId());

		Assertions.assertTrue(portfolioService.findById(secondPortfolio.getId()).isEmpty(), "Portfolio was not deleted");
		Assertions.assertTrue(transactionService.findById(transaction.getId()).isEmpty(), "Transaction was not deleted");
		Assertions.assertTrue(assetBalanceService.findByPortfolio(secondPortfolio.getId()).isEmpty(), "Asset Balance was not deleted");
	}

	@Test
	void deleteShouldRemovePortfolioWhenMultipleExist() {
		CreatePortfolioRequest request = CreatePortfolioRequest.builder()
				.portfolioName("Crypto")
				.userId(user.getId())
				.build();

		PortfolioDTO newPortfolio = instrumentsFacadeService.createPortfolio(request);
		instrumentsFacadeService.deletePortfolio(newPortfolio.getId());

		List<Portfolio> portfolios = portfolioService.findByUserId(user.getId());

		Assertions.assertEquals(1, portfolios.size(), "Only default portfolio should remain");
	}

	@Test
	void deleteShouldFailWhenDeletingLastPortfolio() {
		List<Portfolio> portfolios = portfolioService.findByUserId(user.getId());
		Portfolio defaultPortfolio = portfolios.getFirst();
		Assertions.assertThrows(
				InvalidDataException.class,
				() -> instrumentsFacadeService.deletePortfolio(defaultPortfolio.getId()),
				"Should not allow deleting the last remaining portfolio - 'Main'"
		);
	}

	//<editor-fold desc="UTILS">
	private CreatePortfolioRequest createPortfolioRequest(String portoflioName) {
		return CreatePortfolioRequest.builder()
				.portfolioName(portoflioName)
				.userId(user.getId())
				.build();
	}

	private CreateTransactionRequest buyTransaction(Asset asset, double marketPrice, double orderQuantity, long portfolioId) {
		return CreateTransactionRequest.builder()
				.assetSymbol(asset.getSymbol())
				.type(TransactionType.BUY)
				.marketPrice(BigDecimal.valueOf(marketPrice))
				.orderQuantity(BigDecimal.valueOf(orderQuantity))
				.portfolioId(portfolioId)
				.build();
	}
	//</editor-fold>

}
