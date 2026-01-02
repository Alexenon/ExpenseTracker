package com.example.application.services.crypto;

import com.example.application.entities.User;
import com.example.application.entities.crypto.Portfolio;
import com.example.application.repositories.crypto.PortfolioRepository;
import com.example.application.services.UserService;
import com.example.application.utils.common.lang.StringUtils;
import com.example.application.utils.exceptions.InternalUnexpectedException;
import com.example.application.utils.exceptions.InvalidDataException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PortfolioService {

	private final UserService userService;
	private final PortfolioRepository portfolioRepository;

	private Portfolio save(@NotNull Portfolio portfolio) {
		try {
			preValidation(portfolio);
			portfolio.setLastTimeUpdated(LocalDateTime.now());
			Portfolio savedPortfolio = portfolioRepository.save(portfolio);
			postValidation(savedPortfolio);
			log.info("Saved successfully {}", savedPortfolio);
			return savedPortfolio;
		} catch (Exception e) {
			log.error("Failed to save {}, cause: {}", portfolio, e.getMessage());
			throw new InternalUnexpectedException(e);
		}
	}

	public void delete(Portfolio portfolio) {
		List<Portfolio> otherPortfolios = portfolio.getUser()
				.getPortfolios()
				.stream()
				.filter(userPortfolio -> userPortfolio != portfolio)
				.toList();

		if (otherPortfolios.isEmpty())
			throw new InvalidDataException("Cannot delete the last remaining portfolio");

		portfolioRepository.delete(Objects.requireNonNull(portfolio, "portflio"));
	}

	public Optional<Portfolio> findById(long id) {
		return portfolioRepository.findById(id);
	}

	public List<Portfolio> findByUser(@NotNull User user) {
		return portfolioRepository.findByUser(Objects.requireNonNull(user, "user"));
	}

	public Optional<Portfolio> findByNameAndUser(@NotNull String portfolioName, @NotNull User user) {
		Objects.requireNonNull(user, "user");
		Objects.requireNonNull(user.getId(), "userId");
		Objects.requireNonNull(portfolioName, "name");
		return portfolioRepository.findByNameAndUser(portfolioName, user.getId());
	}

	@Transactional
	public Portfolio addPortfolio(String name, long userId) {
		User user = userService.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));

		if (portfolioRepository.findByNameAndUser(name, userId).isPresent())
			throw new IllegalArgumentException("Portfolio name already exists for user");

		Portfolio portfolio = newPortfolio(name, user);
		setActivePortfolio(userId, portfolio.getId());

		return portfolio;
	}

	@NotNull
	@Transactional
	private Portfolio newPortfolio(String name, User user) {
		Portfolio portfolio = new Portfolio();
		portfolio.setName(name);
		portfolio.setUser(user);
		return save(portfolio);
	}

	@Transactional
	public Portfolio setActivePortfolio(long userId, long portfolioId) {
		User user = userService.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));

		Portfolio portfolio = findById(portfolioId)
				.orElseThrow(() -> new IllegalArgumentException("Portfolio not found"));

		if (!isOwnedByUser(portfolio))
			throw new IllegalArgumentException("Portfolio not owned by user");

		String oldActivePortfolioName = Optional.ofNullable(user.getActivePortfolio())
				.map(Portfolio::getName)
				.orElse(null);

		user.setActivePortfolio(portfolio);

		log.info("Updated active portfolio from '{}' to '{}', for {}", oldActivePortfolioName, portfolio.getName(), user);
		return portfolio;
	}

	private boolean isOwnedByUser(Portfolio portfolio) {
		User user = portfolio.getUser();
		if (user == null)
			throw new InternalUnexpectedException("Portfolio user is missing");

		return user.getPortfolios()
				.stream()
				.anyMatch(p -> p.equals(portfolio));
	}

	@NotNull
	public Portfolio getLatestCreatedPortfolio(@NotNull User user) {
		long userId = Objects.requireNonNull(user, "user").getId();
		Portfolio latestUpdatedPortfolio = portfolioRepository.findLatestUpdatedPortfolio(userId);

		if (latestUpdatedPortfolio == null)
			throw new InternalUnexpectedException("User %s doesn't have any portfolios".formatted(user));

		return latestUpdatedPortfolio;
	}

	private void preValidation(Portfolio portfolio) {
		Objects.requireNonNull(portfolio, "portfolio");
		User user = portfolio.getUser();
		String portfolioName = portfolio.getName();

		Assert.notNull(user, "Portfolio -> user is missing");
		Assert.notNull(portfolio.getLastTimeUpdated(), "Portfolio -> lastTimeUpdated is missing");
		Assert.notNull(portfolio.getTimeCreatedAt(), "Portfolio -> date creation is missing");
		Assert.isTrue(StringUtils.isNotBlank(portfolioName), "Portfolio -> name is missing");

		if (findByNameAndUser(portfolioName, user).isPresent())
			throw new IllegalArgumentException("Duplicate portfolio name for %s".formatted(user));
	}

	/**
	 * Post validation is required, for validation like has active portfolio changed, is it valid, and so on.
	 */
	private void postValidation(Portfolio portfolio) {
		User user = portfolio.getUser();
		String portfolioName = portfolio.getName();

//		Assert.isTrue(findByNameAndUser(portfolioName, user).isEmpty(), "Duplicate portfolio name '%s', for %s".formatted(portfolioName, user));

		if (!user.isNew()) {
			Assert.notEmpty(user.getPortfolios(), "User doesn't have any portfolios");
			Assert.notNull(user.getActivePortfolio(), "User -> active portfolio is missing");
		}
	}

}