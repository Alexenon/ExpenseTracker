package com.example.application.users;

import com.example.application.Application;
import com.example.application.data.dtos.UserDTO;
import com.example.application.data.requests.RegisterUserRequest;
import com.example.application.entities.User;
import com.example.application.entities.crypto.Portfolio;
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

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
@Transactional
class UserServiceTest {

	private final UserService userService;
	private final InstrumentsFacadeService instrumentsFacadeService;
	private final UserRepository userRepository;
	private final PortfolioService portfolioService;

	@Autowired
	public UserServiceTest(UserService userService,
						   InstrumentsFacadeService instrumentsFacadeService,
						   UserRepository userRepository,
						   PortfolioService portfolioService)
	{
		this.userService = userService;
		this.instrumentsFacadeService = instrumentsFacadeService;
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

		Assertions.assertTrue(
				userRepository.findByUsernameIgnoreCase("john").isPresent(),
				"User should be retrievable from database"
		);
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

		Assertions.assertEquals(
				1,
				userPortfolios.size(),
				"User should have exactly one portfolio"
		);

		Portfolio mainPortfolioFirst = userPortfolios.getFirst();

		Assertions.assertEquals(
				"Main",
				mainPortfolioFirst.getName(),
				"Default portfolio name should be 'Main'"
		);
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
				() -> userService.createNewUser(request),
				"Mismatched passwords should throw IllegalArgumentException"
		);

		Assertions.assertTrue(
				userRepository.findByUsernameIgnoreCase("john").isEmpty(),
				"User should not be persisted when validation fails"
		);
	}
}