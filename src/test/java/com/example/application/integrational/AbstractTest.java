package com.example.application.integrational;

import com.example.application.data.dtos.PortfolioDTO;
import com.example.application.data.dtos.TransactionDTO;
import com.example.application.data.dtos.UserDTO;
import com.example.application.data.requests.CreateTransactionRequest;
import com.example.application.data.requests.RegisterUserRequest;
import com.example.application.data.requests.asset.CreateAssetRequest;
import com.example.application.data.requests.portfolio.CreatePortfolioRequest;
import com.example.application.entities.User;
import com.example.application.entities.common.TransactionType;
import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.Portfolio;
import com.example.application.entities.crypto.Transaction;
import com.example.application.repositories.UserRepository;
import com.example.application.repositories.crypto.AssetBalanceRepository;
import com.example.application.repositories.crypto.AssetRepository;
import com.example.application.repositories.crypto.PortfolioRepository;
import com.example.application.repositories.crypto.TransactionRepository;
import com.example.application.repositories.expenses.CategoryRepository;
import com.example.application.repositories.expenses.TagRepository;
import com.example.application.services.crypto.AssetService;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.utils.exceptions.InternalUnexpectedException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.BigInteger;

public abstract class AbstractTest {

	@Autowired
	protected UserRepository userRepository;
	@Autowired
	protected AssetRepository assetRepository;
	@Autowired
	protected PortfolioRepository portfolioRepository;
	@Autowired
	protected TransactionRepository transactionRepository;
	@Autowired
	protected AssetBalanceRepository assetBalanceRepository;
	@Autowired
	protected InstrumentsFacadeService instrumentsFacadeService;
	@Autowired
	protected AssetService assetService;
	/* --------------------------------------
	 * 				EXPENSES
	 * ------------------------------------ */
	@Autowired
	protected TagRepository tagRepository;
	@Autowired
	protected CategoryRepository categoryRepository;

	@BeforeEach
	protected void beforeTest() {
		try {
			assetBalanceRepository.deleteAll();
			transactionRepository.deleteAll();
			portfolioRepository.deleteAll();
			categoryRepository.deleteAll();
			tagRepository.deleteAll();
			assetRepository.deleteAll();
			userRepository.deleteAll();
		} catch (Exception e) {
			throw new InternalUnexpectedException("Couldn't clear database properly", e);
		}
	}

	protected User createUser(String username, String email) {
		RegisterUserRequest request = RegisterUserRequest.builder()
				.username(username)
				.email(email)
				.password("password")
				.confirmPassword("password")
				.build();

		UserDTO dto = instrumentsFacadeService.createNewUser(request);
		return userRepository.findById(dto.getId())
				.orElseThrow(() -> new EntityNotFoundException("User was not created"));
	}

	protected User createUser() {
		return createUser("test", "test-email@test.com");
	}

	protected Asset createAsset(String symbol, double price) {
		CreateAssetRequest request = new CreateAssetRequest();
		request.setSymbol(symbol);
		request.setMarketPrice(BigDecimal.valueOf(price));
		request.setFullName("Some full name");
		request.setSummaryDescription("Some summary description");
		request.setImageUrl("https://test-url.com");
		request.setTotalMarketCap(BigInteger.ZERO);
		request.setTotalSupply(BigInteger.ZERO);
		request.setTodayVolume(BigInteger.ZERO);
		request.setChangePercentage(BigDecimal.ZERO);
		request.setCirculationSupply(BigInteger.ZERO);

		return assetService.createNewAsset(request);
	}

	protected Transaction createTransaction(Asset asset, TransactionType type, double marketPrice, double orderQuantity, long portfolioId) {
		CreateTransactionRequest request = CreateTransactionRequest.builder()
				.assetSymbol(asset.getSymbol())
				.type(type)
				.marketPrice(BigDecimal.valueOf(marketPrice))
				.orderQuantity(BigDecimal.valueOf(orderQuantity))
				.portfolioId(portfolioId)
				.build();

		TransactionDTO dto = instrumentsFacadeService.createTransaction(request);
		return transactionRepository.findById(dto.getId())
				.orElseThrow(() -> new EntityNotFoundException("Transaction was not created"));
	}

	protected Transaction createTransaction(Asset asset, double marketPrice, double orderQuantity, long portfolioId) {
		return createTransaction(asset, TransactionType.BUY, marketPrice, orderQuantity, portfolioId);
	}

	protected Transaction createTransaction(Asset asset, double marketPrice, long portfolioId) {
		return createTransaction(asset, TransactionType.BUY, marketPrice, 1.0, portfolioId);
	}

	protected Portfolio createPortfolio(String name, long userId) {
		CreatePortfolioRequest request = CreatePortfolioRequest.builder()
				.portfolioName("Crypto")
				.userId(userId)
				.build();

		PortfolioDTO dto = instrumentsFacadeService.createPortfolio(request);
		return portfolioRepository.findById(dto.getId())
				.orElseThrow(() -> new EntityNotFoundException("Portfolio was not created"));
	}

	protected void assertBigDecimalEquals(BigDecimal expected, BigDecimal actual, String field) {
		Assertions.assertEquals(
				0,
				actual.compareTo(expected),
				field + " doesn't match. Expected: " + expected + " actual: " + actual
		);
	}

}
