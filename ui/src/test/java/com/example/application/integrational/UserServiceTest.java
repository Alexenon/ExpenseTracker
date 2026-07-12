package com.example.application.integrational;

import com.example.application.Application;
import com.example.application.InstrumentsFacadeService;
import com.example.application.asset.Asset;
import com.example.application.category.Categories;
import com.example.application.category.Category;
import com.example.application.category.CategoryService;
import com.example.application.portfolio.Portfolio;
import com.example.application.portfolio.PortfolioService;
import com.example.application.transaction.Transaction;
import com.example.application.user.User;
import com.example.application.user.UserRepository;
import com.example.application.user.UserService;
import com.example.application.user.domain.RegisterUserRequest;
import com.example.application.user.domain.UpdateUserPasswordRequest;
import com.example.application.user.domain.UpdateUserRequest;
import com.example.application.user.domain.UserDTO;
import com.example.application.user.exceptions.EmailTakenException;
import com.example.application.user.exceptions.UsernameTakenException;
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
	void createUserShouldNotAllowDuplicateUsername() {
		Assertions.assertDoesNotThrow(() -> createUser("john", "john-weak@test.com"));
		Assertions.assertThrows(UsernameTakenException.class, () -> createUser("john", "john-weak@test.com"),
				"Creating user with duplicate username should throw UsernameTakenException");

		// Updating user's username with an existing username
		User secondUser = createUser("brain", "brian@test.com");
		UpdateUserRequest updateUserRequest = new UpdateUserRequest(new UserDTO(secondUser));
		updateUserRequest.setUsername("john");
		Assertions.assertThrows(UsernameTakenException.class, () -> instrumentsFacadeService.updateUser(updateUserRequest),
				"Updating user with an existing username should throw exception");
	}

	@Test
	void createUserShouldNotAllowDuplicateEmail() {
		Assertions.assertDoesNotThrow(() -> createUser("john", "john-weak@test.com"));
		Assertions.assertThrows(EmailTakenException.class, () -> createUser("michael", "john-weak@test.com"),
				"Creating a user with duplicate username should throw UsernameTakenException");

		User secondUser = createUser("brain", "brian@test.com");
		UpdateUserRequest updateUserRequest = new UpdateUserRequest(new UserDTO(secondUser));
		updateUserRequest.setEmail("john-weak@test.com");
		Assertions.assertThrows(EmailTakenException.class, () -> instrumentsFacadeService.updateUser(updateUserRequest),
				"Updating user with an existing email should throw exception");
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
				() -> instrumentsFacadeService.createUser(request),
				"Mismatched passwords should throw IllegalArgumentException"
		);
		Assertions.assertTrue(userRepository.findByUsernameIgnoreCase("john").isEmpty(),
				"User should not be added to database with invalid fields");
		Assertions.assertEquals(0, portfolioRepository.count(),
				"No portfolio should be created, because user was not created");
	}

	//<editor-fold desc="UPDATE">
	@Test
	void updateUserSuccessfully() {
		RegisterUserRequest request = RegisterUserRequest.builder()
				.username("john")
				.email("john.weak@test.com")
				.password("password")
				.confirmPassword("password")
				.build();

		UserDTO user = instrumentsFacadeService.createUser(request);
		UpdateUserRequest updateUserRequest = new UpdateUserRequest(user);
		updateUserRequest.setUsername("brian");
		updateUserRequest.setEmail("brian@test.com");
		Assertions.assertDoesNotThrow(() -> instrumentsFacadeService.updateUser(updateUserRequest),
				"Updating user shouldn't throw any exceptions");

		User updatedUser = userService.findById(user.getId()).orElseThrow();
		Assertions.assertEquals("brian", updatedUser.getUsername());
		Assertions.assertEquals("brian@test.com", updatedUser.getEmail());
	}

	@Test
	void updateUserShouldNotAllowDuplicateUsername() {
		User firstUser = createUser("john", "john-weak@test.com");
		User secondUser = createUser("brain", "brian@test.com");
		UpdateUserRequest updateUserRequest = new UpdateUserRequest(new UserDTO(secondUser));
		updateUserRequest.setUsername("john");
		Assertions.assertThrows(UsernameTakenException.class, () -> instrumentsFacadeService.updateUser(updateUserRequest),
				"Updating user with an existing username should throw exception");
	}

	@Test
	void updateUserShouldNotAllowDuplicateEmail() {
		User firstUser = createUser("john", "john-weak@test.com");
		User secondUser = createUser("brain", "brian@test.com");
		UpdateUserRequest updateUserRequest = new UpdateUserRequest(new UserDTO(secondUser));
		updateUserRequest.setEmail("john-weak@test.com");
		Assertions.assertThrows(EmailTakenException.class, () -> instrumentsFacadeService.updateUser(updateUserRequest),
				"Updating user with an existing email should throw exception");
	}

	@Test
	void updatePasswordSuccessfully() {
		RegisterUserRequest request = RegisterUserRequest.builder()
				.username("john")
				.email("john.weak@test.com")
				.password("password")
				.confirmPassword("password")
				.build();

		UserDTO user = instrumentsFacadeService.createUser(request);
		User foundUser = userService.findById(user.getId()).orElseThrow();
		String oldPassword = foundUser.getPassword();

		UpdateUserPasswordRequest updateUserPasswordRequest = new UpdateUserPasswordRequest(user.getId(), "newPassword");
		instrumentsFacadeService.changeUserPassword(updateUserPasswordRequest);
		foundUser = userService.findById(user.getId()).orElseThrow();
		String newPassword = foundUser.getPassword();

		Assertions.assertNotEquals(oldPassword, newPassword, "Password after update should be changed");
		Assertions.assertNotEquals(newPassword, "newPassword", "Password after update should be hashed");
	}
	//</editor-fold>

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