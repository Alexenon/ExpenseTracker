package com.example.application.services.crypto;

import com.example.application.entities.User;
import com.example.application.entities.crypto.Portfolio;
import com.example.application.repositories.crypto.PortfolioRepository;
import com.example.application.utils.common.lang.StringUtils;
import com.example.application.utils.exceptions.InternalUnexpectedException;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class PortfolioService {

	private final PortfolioRepository portfolioRepository;

	@Autowired
	public PortfolioService(PortfolioRepository portfolioRepository) {
		this.portfolioRepository = portfolioRepository;
	}

	@NotNull
	@Transactional
	public Portfolio createNewPortfolio(String name, User user) {
		Portfolio portfolio = new Portfolio();
		portfolio.setName(name);
		portfolio.setUser(user);
		return save(portfolio);
	}

	public Portfolio save(@NotNull Portfolio portfolio) {
		try {
			validate(portfolio);
			portfolio.setLastTimeUpdated(LocalDateTime.now());
			Portfolio savedPortfolio = portfolioRepository.save(portfolio);
			log.info("Saved successfully {}", savedPortfolio);
			return savedPortfolio;
		} catch (Exception e) {
			log.error("Failed to save {}, cause: {}", portfolio, e.getMessage());
			throw new InternalUnexpectedException(e);
		}
	}

	public void delete(Portfolio portfolio) {
		portfolioRepository.delete(Objects.requireNonNull(portfolio, "portflio"));
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

	private void validate(Portfolio portfolio) {
		Objects.requireNonNull(portfolio, "portfolio");
		User user = portfolio.getUser();
		String portfolioName = portfolio.getName();

		Assert.isTrue(StringUtils.isNotBlank(portfolioName), "portfolio name is missing");
		Assert.isTrue(user != null, "portfolio user is missing");
		Assert.isTrue(portfolio.getLastTimeUpdated() != null, "portfolio lastTimeUpdated is missing");
		Assert.isTrue(portfolio.getCreatedAt() != null, "portfolio date creation is missing");
		Assert.isTrue(findByNameAndUser(portfolioName, user).isEmpty(), "Duplicate portfolio name for same user: %s".formatted(portfolioName));
	}

}