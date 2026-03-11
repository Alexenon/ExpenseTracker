package com.example.application.integrational;

import com.example.application.Application;
import com.example.application.data.requests.RegisterUserRequest;
import com.example.application.entities.User;
import com.example.application.services.UserService;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.services.crypto.PortfolioService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class UserAssetBalanceTest extends AbstractTest {

	private final UserService userService;
	private final PortfolioService portfolioService;

	private User user;

	@Autowired
	public UserAssetBalanceTest(InstrumentsFacadeService instrumentsFacadeService, UserService userService, PortfolioService portfolioService) {
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

}
