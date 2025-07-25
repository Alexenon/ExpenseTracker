package com.example.application.services.crypto;

import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.CryptoTransaction;
import com.example.application.entities.crypto.Wallet;
import com.example.application.entities.crypto.WalletBalance;
import com.example.application.repositories.crypto.WalletBalanceRepository;
import com.example.application.utils.common.lang.MathUtils;
import com.example.application.utils.common.lang.NumberUtils;
import com.example.application.utils.exceptions.InvalidBalanceAmount;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.application.utils.investment.ProfitCalculator.calculateNewAvgPrice;

@Service
public class WalletBalanceService {

	@Autowired
	private WalletBalanceRepository repository;

	@Transactional(readOnly = true)
	public WalletBalance getWalletBalancesByWalletAndAsset(Wallet wallet, Asset asset) {
		return repository.findByWalletAndAsset(wallet, asset).orElseThrow();
	}

	// TODO: FIND A WAY TO EXTRACT THIS FROM DATABASE WITHOUT ANY EXCEPTIONS
	@Transactional(readOnly = true)
	public List<WalletBalance> getWalletBalancesByWalletWithNonZeroAmount(Wallet wallet) {
		return repository.findByWalletWithNonZeroAmount(wallet.getId());
	}

	@Transactional(readOnly = true)
	public List<WalletBalance> getByWallet(Wallet wallet) {
		return repository.findByWallet(wallet);
	}

	@Transactional(readOnly = true)
	public WalletBalance getByWalletAndAsset(Wallet wallet, Asset asset) {
		return repository.findByWalletAndAsset(wallet, asset)
				.orElseThrow(() -> new IllegalStateException("Wallet balance not found for %s asset".formatted(asset.getSymbol())));
	}

	@Transactional
	public WalletBalance save(WalletBalance walletBalance) {
		return repository.save(walletBalance);
	}

	@Transactional
	public WalletBalance updateWalletBalance(CryptoTransaction transaction) {
		WalletBalance walletBalance = getWalletBalancesByWalletAndAsset(transaction.getWallet(), transaction.getAsset());

		updateAvgBuySellPrice(walletBalance, transaction);
		walletBalance.setAmount(calculateAmountAfterSupply(walletBalance, transaction));
		walletBalance.setCost(calculateTotalCost(transaction, walletBalance));
		walletBalance.setLastTimeUpdated(LocalDateTime.now());

		repository.save(walletBalance);
		return walletBalance;
	}

	private static double calculateTotalCost(CryptoTransaction transaction, WalletBalance walletBalance) {
		double newCost = MathUtils.withSign(transaction.getOrderTotalCost(), transaction.isBuyTransaction());
		return NumberUtils.checkDouble(walletBalance.getCost() + newCost);
	}

	public double calculateAmountAfterSupply(WalletBalance walletBalance, CryptoTransaction transaction) {
		double transactionAmount = NumberUtils.checkDouble(transaction.getOrderQuantity());

		if (transactionAmount <= 0)
			throw new InvalidBalanceAmount("Invalid transaction amount");

		double quantityToAdd = MathUtils.withSign(transactionAmount, transaction.isBuyTransaction());
		double tokensAmountAfterSupply = walletBalance.getAmount() + quantityToAdd;

		if (tokensAmountAfterSupply < 0)
			throw new InvalidBalanceAmount("The amount of tokens cannot be negative.");

		return NumberUtils.checkDouble(tokensAmountAfterSupply);
	}

	public static void updateAvgBuySellPrice(@NotNull WalletBalance walletBalance, @NotNull CryptoTransaction transaction) {
		double marketPrice = transaction.getMarketPrice();
		double previousAmount = walletBalance.getAmount();
		double orderQuantity = transaction.getOrderQuantity();

		if (transaction.isBuyTransaction()) {
			walletBalance.setAvgBuyPrice(
					calculateNewAvgPrice(walletBalance.getAvgBuyPrice(), previousAmount, orderQuantity, marketPrice)
			);
		} else {
			walletBalance.setAvgSellPrice(
					calculateNewAvgPrice(walletBalance.getAvgSellPrice(), previousAmount, orderQuantity, marketPrice)
			);
		}
	}


}
