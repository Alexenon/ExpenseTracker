package com.example.application.integrational;

import com.example.application.Application;
import com.example.application.data.dtos.UserDTO;
import com.example.application.data.requests.RegisterUserRequest;
import com.example.application.entities.User;
import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.Portfolio;
import com.example.application.entities.crypto.Transaction;
import com.example.application.repositories.UserRepository;
import com.example.application.services.UserService;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.services.crypto.PortfolioService;
import com.example.application.utils.exceptions.auth.UsernameTakenException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SuppressWarnings("DataFlowIssue")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
@Transactional
class UserServiceTest extends AbstractTest {

	private final UserService userService;
	private final UserRepository userRepository;
	private final PortfolioService portfolioService;

	@Autowired
	public UserServiceTest(UserService userService,
						   InstrumentsFacadeService instrumentsFacadeService,
						   UserRepository userRepository,
						   PortfolioService portfolioService)
	{
		this.userService = userService;
		this.userRepository = userRepository;
		this.portfolioService = portfolioService;
	}

	@BeforeEach
	void databaseShouldBeEmptyBeforeTest() {
		Assertions.assertEquals(0, userRepository.count(), "Database should be empty before each test");
	}

	@Test
	void userCreatedSuccessfullyTest() {
		RegisterUserRequest request = RegisterUserRequest.builder()
				.username("john")
				.email("john@test.com")
				.password("password")
				.confirmPassword("password")
				.build();

		User user = userService.createNewUser(request);

		Assertions.assertNotNull(user.getId(), "User ID should be generated");
		Assertions.assertEquals("john", user.getUsername(), "Username should match request");
		Assertions.assertEquals("john@test.com", user.getEmail(), "Email should match request");
		Assertions.assertNotEquals("password", user.getPassword(), "Password should be hashed");
		Assertions.assertFalse(user.getRoles().isEmpty(), "User should have at least one role");
		Assertions.assertNotNull(user.getLastTimeUpdated(), "Last update timestamp should be set");
		Assertions.assertNotNull(user.getTimeCreatedAt(), "Creation timestamp should be set");

		Assertions.assertTrue(userRepository.findByUsernameIgnoreCase("john").isPresent(),
				"User should be retrievable from database");
	}

	@Test
	void userIsCreatedWithPortfolioSuccessfully() {
		RegisterUserRequest request = RegisterUserRequest.builder()
				.username("john")
				.email("john.weak@test.com")
				.password("password")
				.confirmPassword("password")
				.build();

		UserDTO createdUser = instrumentsFacadeService.createNewUser(request);

		List<Portfolio> userPortfolios = portfolioService.findByUserId(createdUser.getId());

		Assertions.assertEquals(1, userPortfolios.size(),
				"User should have exactly one portfolio");
		Assertions.assertEquals("Main", userPortfolios.getFirst().getName(),
				"Default portfolio name should be 'Main'");
	}

	@Test
	void shouldNotAllowDuplicateUsername() {
		RegisterUserRequest request1 = RegisterUserRequest.builder()
				.username("john")
				.email("john.weak@test.com")
				.password("password")
				.confirmPassword("password")
				.build();

		userService.createNewUser(request1);

		RegisterUserRequest request2 = RegisterUserRequest.builder()
				.username("john")
				.email("john.weak@test.com")
				.password("password")
				.confirmPassword("password")
				.build();

		Assertions.assertThrows(
				UsernameTakenException.class,
				() -> userService.createNewUser(request2),
				"Creating a user with duplicate username should throw UsernameTakenException"
		);
	}

	@Test
	void userIsNotCreatedDueInvalidDataTest() {
		RegisterUserRequest request = RegisterUserRequest.builder()
				.username("john")
				.email("john.weak@test.com")
				.password("password")
				.confirmPassword("password2")
				.build();

		Assertions.assertThrows(
				IllegalArgumentException.class,
				() -> instrumentsFacadeService.createNewUser(request),
				"Mismatched passwords should throw IllegalArgumentException"
		);
		Assertions.assertTrue(userRepository.findByUsernameIgnoreCase("john").isEmpty(),
				"User should not be added to database with invalid fields");
		Assertions.assertEquals(0, portfolioRepository.count(),
				"No portfolio should be created, because user was not created");
	}

	@Test
	void deleteUserTest() {
		User user = createUser("test", "test-email.com");
		Long defaultUserPortfolioId = user.getActivePortfolio().getId();

		instrumentsFacadeService.deleteUser(user.getId());

		Assertions.assertTrue(userRepository.findById(user.getId()).isEmpty(),
				"User after deletion is not removed from database");
		Assertions.assertTrue(portfolioRepository.findById(defaultUserPortfolioId).isEmpty(),
				"User portfolio is not removed from database");
	}

	@Test
	void deleteUserWithPortfolioAndTransactionsTest() {
		User user = createUser("test", "test-email.com");

		Portfolio secondPortfolio = createPortfolio("Second Portfolio", user.getId());
		Asset asset = createAsset("BTC", 100_000);
		Transaction transaction = createTransaction(asset.getSymbol(), 80_000, 1, secondPortfolio.getId());

		instrumentsFacadeService.deleteUser(user.getId());
		Assertions.assertTrue(userRepository.findById(user.getId()).isEmpty(),
				"User after deletion is not removed from database");
		Assertions.assertTrue(portfolioRepository.findById(secondPortfolio.getId()).isEmpty(),
				"User portfolio after user deletion is not removed from database");
		Assertions.assertTrue(transactionRepository.findById(transaction.getId()).isEmpty(),
				"User transaction after user deletion is not removed from database");
	}

}