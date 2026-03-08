package com.example.application.integrational;

import com.example.application.Application;
import com.example.application.data.dtos.PortfolioDTO;
import com.example.application.data.requests.CreateTransactionRequest;
import com.example.application.data.requests.RegisterUserRequest;
import com.example.application.data.requests.portfolio.CreatePortfolioRequest;
import com.example.application.entities.User;
import com.example.application.entities.common.TransactionType;
import com.example.application.entities.crypto.Portfolio;
import com.example.application.services.UserService;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.services.crypto.PortfolioService;
import com.example.application.utils.exceptions.InvalidDataException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SuppressWarnings("DataFlowIssue")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
class PortfolioServiceTest extends AbstractTest {

	private final InstrumentsFacadeService instrumentsFacadeService;
	private final UserService userService;
	private final PortfolioService portfolioService;

	private User user;

	@Autowired
	public PortfolioServiceTest(InstrumentsFacadeService instrumentsFacadeService, UserService userService, PortfolioService portfolioService) {
		this.instrumentsFacadeService = instrumentsFacadeService;
		this.userService = userService;
		this.portfolioService = portfolioService;
	}

	@BeforeEach
	void setupUser() {
		RegisterUserRequest request = RegisterUserRequest.builder()
				.username("john")
				.email("john@test.com")
				.password("password")
				.confirmPassword("password")
				.build();

		Long userId = instrumentsFacadeService.createNewUser(request).getId();
		user = userService.findById(userId).orElseThrow(() -> new EntityNotFoundException("No such user"));
		Assertions.assertTrue(userService.findById(user.getId()).isPresent(), "User was not created");
		Assertions.assertTrue(portfolioService.findByNameAndUser("Main", user.getId()).isPresent(),
				"Created user doesn't have default portfolio attached");
	}

	@AfterEach
	void removeUser() {
		userService.delete(user.getId());
		Assertions.assertTrue(userService.findById(user.getId()).isEmpty(), "User was not deleted");
		Assertions.assertTrue(portfolioService.findByUserId(user.getId()).isEmpty(), "Portfolios are not deleted");
	}

	@Test
	void savePortfolioSuccessfully() {
		CreatePortfolioRequest request = CreatePortfolioRequest.builder()
				.portfolioName("Crypto")
				.userId(user.getId())
				.build();

		PortfolioDTO saved = portfolioService.createPortfolio(request);
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

		Assertions.assertDoesNotThrow(() -> portfolioService.createPortfolio(request),
				"First portfolio was not created");

		Assertions.assertThrows(
				IllegalArgumentException.class,
				() -> portfolioService.createPortfolio(request),
				"Duplicate portfolio names for same user should not be allowed"
		);
	}

	@Test
	void userShouldHaveCreatedPortfolio() {
		CreatePortfolioRequest request = CreatePortfolioRequest.builder()
				.portfolioName("Crypto")
				.userId(user.getId())
				.build();

		PortfolioDTO saved = portfolioService.createPortfolio(request);
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

		PortfolioDTO newPortfolio = portfolioService.createPortfolio(request);
		portfolioService.setPortfolioAsActive(newPortfolio.getId());

		String activePortfolioName = userService.findById(user.getId())
				.orElseThrow(() -> new EntityNotFoundException("User not found (deleted ?)"))
				.getActivePortfolio()
				.getName();
		Assertions.assertEquals("Trading", activePortfolioName, "Active portfolio should be updated");
	}

	@Test
	void deletePortfolioShouldDeleteAssetBalancesAndTransactionsAssociated() {
		CreatePortfolioRequest request = createPortfolioRequest("Seond portfolio");
		PortfolioDTO portfolio = portfolioService.createPortfolio(request);

		createAsset("BTC", 100_000);

		portfolioService.delete(portfolio.getId());

	}

	@Test
	void deleteShouldRemovePortfolioWhenMultipleExist() {
		CreatePortfolioRequest request = CreatePortfolioRequest.builder()
				.portfolioName("Crypto")
				.userId(user.getId())
				.build();

		PortfolioDTO newPortfolio = portfolioService.createPortfolio(request);
		portfolioService.delete(newPortfolio.getId());

		List<Portfolio> portfolios = portfolioService.findByUserId(user.getId());

		Assertions.assertEquals(1, portfolios.size(), "Only default portfolio should remain");
	}

	@Test
	void deleteShouldFailWhenDeletingLastPortfolio() {
		List<Portfolio> portfolios = portfolioService.findByUserId(user.getId());
		Portfolio defaultPortfolio = portfolios.getFirst();
		Assertions.assertThrows(
				InvalidDataException.class,
				() -> portfolioService.delete(defaultPortfolio.getId()),
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

	private CreateTransactionRequest buyTransaction(double marketPrice, double orderQuantity) {
		return CreateTransactionRequest.builder()
				.assetSymbol(asset.getSymbol())
				.type(TransactionType.BUY)
				.marketPrice(marketPrice)
				.orderQuantity(orderQuantity)
				.portfolioId(portfolio.getId())
				.build();
	}
	//</editor-fold>

}
