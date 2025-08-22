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
import java.util.Objects;

import static com.example.application.utils.investment.ProfitCalculator.calculateNewAvgPrice;

@Service
public class WalletBalanceService {

    @Autowired
    private WalletBalanceRepository repository;

    @NotNull
    @Transactional(readOnly = true)
    public List<WalletBalance> getWalletBalancesByWalletWithNonZeroAmount(@NotNull Wallet wallet) {
        Objects.requireNonNull(wallet, "wallet");
        // TODO: FIND A WAY TO EXTRACT THIS FROM DATABASE WITHOUT ANY EXCEPTIONS
        return repository.findByWalletWithNonZeroAmount(wallet.getId());
    }

    @NotNull
    @Transactional(readOnly = true)
    public List<WalletBalance> getByWallet(@NotNull Wallet wallet) {
        return repository.findByWallet(Objects.requireNonNull(wallet, "wallet"));
    }

    @NotNull
    @Transactional(readOnly = true)
    public WalletBalance getByWalletAndAsset(@NotNull Wallet wallet, @NotNull Asset asset) {
        Objects.requireNonNull(wallet, "wallet");
        Objects.requireNonNull(asset, "asset");

        return repository.findByWalletAndAsset(wallet, asset)
                .orElseThrow(() -> new IllegalStateException("Wallet balance not found for %s asset".formatted(asset.getSymbol())));
    }

    @Transactional
    public WalletBalance save(@NotNull WalletBalance walletBalance) {
        Objects.requireNonNull(walletBalance, "walletBalance");

        double amount = walletBalance.getAmount();
        if (amount < 0)
            throw new InvalidBalanceAmount("Invalid balance amount: %f".formatted(amount));

        return repository.save(walletBalance);
    }

    @Transactional
    public WalletBalance updateWalletBalance(@NotNull CryptoTransaction transaction) {
        Objects.requireNonNull(transaction, "transaction");
        WalletBalance walletBalance = getByWalletAndAsset(transaction.getWallet(), transaction.getAsset());

        updateAvgBuySellPrice(walletBalance, transaction);
        walletBalance.setAmount(calculateAmountAfterSupply(walletBalance, transaction));
        walletBalance.setCost(calculateTotalCost(transaction, walletBalance));
        walletBalance.setLastTimeUpdated(LocalDateTime.now());

        return repository.save(walletBalance);
    }

    private static double calculateTotalCost(CryptoTransaction transaction, WalletBalance walletBalance) {
        double newCost = MathUtils.withSign(transaction.getOrderTotalCost(), transaction.isBuyTransaction());
        return NumberUtils.checkDouble(walletBalance.getCost() + newCost);
    }

    private static double calculateAmountAfterSupply(WalletBalance walletBalance, CryptoTransaction transaction) {
        double transactionAmount = NumberUtils.checkDouble(transaction.getOrderQuantity());

        if (transactionAmount <= 0)
            throw new InvalidBalanceAmount("Invalid transaction amount");

        double quantityToAdd = MathUtils.withSign(transactionAmount, transaction.isBuyTransaction());
        double tokensAmountAfterSupply = walletBalance.getAmount() + quantityToAdd;

        return NumberUtils.checkDouble(tokensAmountAfterSupply);
    }

    private static void updateAvgBuySellPrice(@NotNull WalletBalance walletBalance, @NotNull CryptoTransaction transaction) {
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
