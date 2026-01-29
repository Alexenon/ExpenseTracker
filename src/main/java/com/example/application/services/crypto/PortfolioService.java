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

	//<editor-fold desc="SEARCH">
	public Optional<Portfolio> findById(@NotNull Long id) {
		return portfolioRepository.findById(Objects.requireNonNull(id, "id"));
	}

	public List<Portfolio> findByUserId(@NotNull Long userId) {
		return portfolioRepository.findByUser(Objects.requireNonNull(userId, "userId"));
	}

	public Optional<Portfolio> findByNameAndUser(@NotNull String portfolioName, @NotNull Long userId) {
		Objects.requireNonNull(userId, "userId");
		Objects.requireNonNull(portfolioName, "name");
		return portfolioRepository.findByNameAndUser(portfolioName, userId);
	}
	//</editor-fold>

	public void delete(Long portfolioId) {
		Portfolio portfolio = findById(portfolioId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid portfolio: #%d".formatted(portfolioId)));

		List<Portfolio> otherPortfolios = portfolio.getUser()
				.getPortfolios()
				.stream()
				.filter(userPortfolio -> userPortfolio != portfolio)
				.toList();

		if (otherPortfolios.isEmpty())
			throw new InvalidDataException("Cannot delete the last remaining portfolio");

		portfolioRepository.delete(Objects.requireNonNull(portfolio, "portflio"));
	}

	@Transactional
	public void setPortfolioAsActive(Long portfolioId) {
		Portfolio portfolio = findById(portfolioId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid portfolio: #%d".formatted(portfolioId)));

		User user = portfolio.getUser();

		String oldActivePortfolioName = Optional.ofNullable(user.getActivePortfolio())
				.map(Portfolio::getName)
				.orElse(null);

		if (portfolio.getName().equals(oldActivePortfolioName))
			return;

		user.setActivePortfolio(portfolio);
		log.info("Updated activePortfolio from '{}' to '{}', for {}", oldActivePortfolioName, portfolio.getName(), user);
	}

	@NotNull
	public Portfolio getLatestCreatedPortfolio(@NotNull Long userId) {
		Objects.requireNonNull(userId, "userId");
		Portfolio latestUpdatedPortfolio = portfolioRepository.findLatestUpdatedPortfolio(userId);

		return Optional.ofNullable(latestUpdatedPortfolio)
				.orElseThrow(() -> new InternalUnexpectedException("User #%d doesn't have any portfolios".formatted(userId)));
	}

	@Transactional
	public Portfolio save(@NotNull Portfolio portfolio) {
		try {
			preValidation(portfolio);
			portfolio.setLastTimeUpdated(LocalDateTime.now());
			Portfolio savedPortfolio = portfolioRepository.save(portfolio);
			postValidation(savedPortfolio);
			log.info("Saved successfully {}", savedPortfolio);
			return savedPortfolio;
		} catch (Exception e) {
			log.error("Failed to save {}", portfolio, e);
			throw new InternalUnexpectedException(e);
		}
	}

	private void preValidation(Portfolio portfolio) {
		Objects.requireNonNull(portfolio, "portfolio");
		User user = portfolio.getUser();
		String portfolioName = portfolio.getName();

		Assert.isTrue(StringUtils.isNotBlank(portfolioName), "Portfolio -> name is missing");
		Assert.notNull(user, "Portfolio -> user is missing");
		Assert.notNull(portfolio.getLastTimeUpdated(), "Portfolio -> lastTimeUpdated is missing");
		Assert.notNull(portfolio.getTimeCreatedAt(), "Portfolio -> date creation is missing");

		if (findByNameAndUser(portfolioName, user.getId()).isPresent())
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