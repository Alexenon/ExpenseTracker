package com.example.application.services.crypto;

import com.example.application.components.EntityValidator;
import com.example.application.data.requests.portfolio.CreatePortfolioRequest;
import com.example.application.entities.User;
import com.example.application.entities.crypto.Portfolio;
import com.example.application.repositories.crypto.PortfolioRepository;
import com.example.application.utils.exceptions.InternalUnexpectedException;
import com.example.application.utils.exceptions.InvalidDataException;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PortfolioService {

	private final PortfolioRepository portfolioRepository;
	private final EntityValidator validator;

	//<editor-fold desc="SEARCH">
	public Optional<Portfolio> findById(@NotNull Long portfolioId) {
		return portfolioRepository.findById(Objects.requireNonNull(portfolioId, "portfolioId"));
	}

	public List<Portfolio> findByUser(@NotNull Long userId) {
		return portfolioRepository.findByUser(Objects.requireNonNull(userId, "userId"));
	}

	public Optional<Portfolio> findByNameAndUser(@NotNull String portfolioName, @NotNull Long userId) {
		Objects.requireNonNull(userId, "userId");
		Objects.requireNonNull(portfolioName, "name");
		return portfolioRepository.findByNameAndUser(portfolioName, userId);
	}
	//</editor-fold>

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

	@Nonnull
	public Portfolio findLatestUpdatedPortfolio(@NotNull Long userId) {
		Objects.requireNonNull(userId, "userId");
		Portfolio latestUpdatedPortfolio = portfolioRepository.findLatestUpdatedPortfolio(userId);

		return Optional.ofNullable(latestUpdatedPortfolio)
				.orElseThrow(() -> new InternalUnexpectedException("User #%d doesn't have any portfolios".formatted(userId)));
	}

	@Nonnull
	@Transactional
	public Portfolio createPortfolio(@NotNull CreatePortfolioRequest request, User user) {
		log.info("Creating new portfolio: {}", request);
		Objects.requireNonNull(request, "request");

		String portfolioName = request.getPortfolioName();
		if (findByNameAndUser(portfolioName, request.getUserId()).isPresent())
			throw new IllegalArgumentException("Portfolio '" + portfolioName + "' already exists for " + user);

		Portfolio portfolio = new Portfolio();
		portfolio.setName(portfolioName);
		portfolio.setUser(user);
		return save(portfolio);
	}

	@Transactional
	public Portfolio save(@NotNull Portfolio portfolio) {
		validator.validate(portfolio);

		try {
			portfolio.setLastTimeUpdated(LocalDateTime.now());
			Portfolio savedPortfolio = portfolioRepository.save(portfolio);
			log.info("Saved successfully {}", savedPortfolio);
			return savedPortfolio;
		} catch (Exception e) {
			log.error("Failed to save {}", portfolio, e);
			throw new InternalUnexpectedException(e);
		}
	}

	@Transactional
	public void delete(Long portfolioId) {
		log.info("Deleting portfolio: #{}", portfolioId);
		Portfolio portfolio = findById(portfolioId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid portfolio: #%d".formatted(portfolioId)));

		User user = portfolio.getUser();
		List<Portfolio> userPortfolios = findByUser(user.getId());

		if (userPortfolios.size() <= 1)
			throw new InvalidDataException("Cannot delete the last remaining portfolio");

		Portfolio latestUpdatedPortfolio = findLatestUpdatedPortfolio(user.getId());
		setPortfolioAsActive(latestUpdatedPortfolio.getId());

		user.getPortfolios().remove(portfolio);

		try {
			portfolioRepository.deleteById(portfolioId);
			log.info("Portfolio #{}, was deleted successfully", portfolioId);
		} catch (Exception e) {
			throw new InternalUnexpectedException(e);
		}
	}

	@Transactional
	public void deleteAll(@NotNull List<Portfolio> portfolios) {
		int size = portfolios.size();
		log.info("Deleting {} portfolios", size);
		try {
			portfolioRepository.deleteAll(portfolios);
		} catch (Exception e) {
			log.error("Failed to delete {} portfolios", size, e);
			throw new InternalUnexpectedException(e);
		}
		log.info("Deleted successfully {} portfolios", size);
	}

	@Transactional
	public void deleteAllUserPortfolios(Long userId) {
		deleteAll(findByUser(userId));
	}

}