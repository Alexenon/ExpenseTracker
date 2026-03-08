package com.example.application.integrational;

import com.example.application.data.requests.CreateTransactionRequest;
import com.example.application.data.requests.RegisterUserRequest;
import com.example.application.entities.common.TransactionType;
import com.example.application.entities.crypto.Asset;
import com.example.application.services.crypto.AssetService;
import com.example.application.services.crypto.InstrumentsFacadeService;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.util.Objects;

public abstract class AbstractTest {

	protected InstrumentsFacadeService instrumentsFacadeService;
	protected AssetService assetService;

	@Autowired
	public AbstractTest(InstrumentsFacadeService instrumentsFacadeService, AssetService assetService) {
		this.instrumentsFacadeService = instrumentsFacadeService;
		this.assetService = assetService;
	}

	protected Long createUser(String username, String email) {
		RegisterUserRequest request = RegisterUserRequest.builder()
				.username(username)
				.email(email)
				.password("password")
				.confirmPassword("password")
				.build();

		return instrumentsFacadeService.createNewUser(request).getId();
	}

	protected Long createUser() {
		return createUser("test", "test-email@test.com");
	}

	protected Long createAsset(String symbol, double price) {
		Asset asset = new Asset();
		asset.setSymbol(symbol);
		asset.setMarketPrice(price);
		asset.setFullName("Some full name");
		asset.setTotalMarketCap(BigInteger.ZERO);
		asset.setTotalSupply(BigInteger.ZERO);

		Asset saved = Objects.requireNonNull(assetService.save(asset), "Asset was not saved");
		return saved.getId();
	}

	protected Long createTransaction(String assetSymbol, TransactionType type, double marketPrice, double orderQuantity, long portfolioId) {
		CreateTransactionRequest request = CreateTransactionRequest.builder()
				.assetSymbol(assetSymbol)
				.type(type)
				.marketPrice(marketPrice)
				.orderQuantity(orderQuantity)
				.portfolioId(portfolioId)
				.build();

		return instrumentsFacadeService.createTransaction(request).getId();
	}

	protected Long createTransaction(String assetSymbol, double marketPrice, double orderQuantity, long portfolioId) {
		return createTransaction(assetSymbol, TransactionType.BUY, marketPrice, orderQuantity, portfolioId);
	}

	protected Long createTransaction(String assetSymbol, double marketPrice, long portfolioId) {
		return createTransaction(assetSymbol, TransactionType.BUY, marketPrice, 1.0, portfolioId);
	}


}
