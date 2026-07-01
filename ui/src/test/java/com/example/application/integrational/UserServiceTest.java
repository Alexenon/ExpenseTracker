package com.example.application.integrational;

import com.example.application.Application;
import com.example.application.data.enums.Categories;
import com.example.application.data.requests.RegisterUserRequest;
import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.Transaction;
import com.example.application.entities.expenses.Category;
import com.example.application.repositories.UserRepository;
import com.example.application.services.UserService;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.services.crypto.PortfolioService;
import com.example.application.services.expenses.CategoryService;
import com.example.application.utils.exceptions.auth.UsernameTakenException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
@Transactional
class UserServiceTest extends AbstractTest {

	private final UserService userService;
	private final UserRepository userRepository;
	private final PortfolioService portfolioService;
	private final CategoryService categoryService;

	@Autowired
	public UserServiceTest(UserService userService,
						   InstrumentsFacadeService instrumentsFacadeService,
						   UserRepository userRepository,
						   PortfolioService portfolioService,
						   CategoryService categoryService)
	{
		this.userService = userService;
		this.userRepository = userRepository;
		this.portfolioService = portfolioService;
		this.categoryService = categoryService;
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
	void userIsCreatedWithDefaultItemsSuccessfully() {
		RegisterUserRequest request = RegisterUserRequest.builder()
				.username("john")
				.email("john.weak@test.com")
				.password("password")
				.confirmPassword("password")
				.build();

		Long createdUserId = createUser("john", "john.weak@test.com").getId();

		validateDefaultUserPortfolio(createdUserId);
		validateDefaultUserCategories(createdUserId);
		validateDefaultUserTags(createdUserId);
	}

	@Test
	void shouldNotAllowDuplicateUsername() {
		Assertions.assertDoesNotThrow(() -> createUser("john", "john-weak@test.com"));
		Assertions.assertThrows(UsernameTakenException.class, () -> createUser("john", "john-weak@test.com"),
				"Creating a user with duplicate username should throw UsernameTakenException");
	}

	@Test
	void shouldNotAllowDuplicateEmail() {
		Assertions.assertDoesNotThrow(() -> createUser("john", "john-weak@test.com"));
		Assertions.assertThrows(UsernameTakenException.class, () -> createUser("michael", "john-weak@test.com"),
				"Creating a user with duplicate username should throw UsernameTakenException");
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
		User user = createUser("test", "test-email@domain.com");

		Portfolio secondPortfolio = createPortfolio("Second Portfolio", user.getId());
		Asset asset = createAsset("BTC", 100_000);
		Transaction transaction = createTransaction(asset, 80_000, 1, secondPortfolio.getId());

		instrumentsFacadeService.deleteUser(user.getId());
		Assertions.assertTrue(userRepository.findById(user.getId()).isEmpty(),
				"User after deletion is not removed from database");
		Assertions.assertIterableEquals(Collections.emptyList(), portfolioRepository.findByUser(user.getId()),
				"Portfolio after user deletion is not removed from database");
		Assertions.assertTrue(transactionRepository.findById(transaction.getId()).isEmpty(),
				"Transactions after user deletion is not removed from database");
		Assertions.assertTrue(expenseRepository.findByUser(user.getId()).isEmpty(),
				"Expenses after user deletion are not removed from database");
		Assertions.assertTrue(categoryRepository.findByUser(user.getId()).isEmpty(),
				"Categories after user deletion is not removed from database");
		Assertions.assertTrue(tagRepository.findByUser(user.getId()).isEmpty(),
				"Tags after user deletion is not removed from database");
	}

	private void validateDefaultUserPortfolio(Long userId) {
		List<Portfolio> userPortfolios = portfolioService.findByUser(userId);
		Assertions.assertEquals(1, userPortfolios.size(), "New user should have exactly one portfolio");
		Assertions.assertEquals("Main", userPortfolios.getFirst().getName(), "Invalid portfolio name");
	}

	private void validateDefaultUserCategories(Long userId) {
		List<String> userCategories = categoryService.findByUser(userId)
				.stream()
				.map(Category::getName)
				.toList();

		Assertions.assertIterableEquals(Categories.getAllCategoryNames(), userCategories,
				"New user's categories doesn't match");
	}

	private void validateDefaultUserTags(Long userId) {
		// TODO: [URGENT]
	}

}