package com.example.application.services.crypto;

import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.CryptoTransaction;
import com.example.application.entities.crypto.Wallet;
import com.example.application.entities.crypto.WalletBalance;
import com.example.application.repositories.crypto.WalletBalanceRepository;
import com.example.application.utils.common.MathUtils;
import com.example.application.utils.exceptions.InvalidBalanceAmount;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WalletBalanceService {

    @Autowired
    private WalletBalanceRepository repository;

    public WalletBalance getWalletBalancesByWalletAndAsset(Wallet wallet, Asset asset) {
        return repository.findByWalletAndAsset(wallet, asset).orElseThrow();
    }

    // TODO: FIND A WAY TO EXTRACT THIS FROM DATABASE WITHOUT EXCEPTION
    public List<WalletBalance> getWalletBalancesByWalletWithNonZeroAmount(Wallet wallet) {
        return repository.findByWalletWithNonZeroAmount(wallet.getId());
    }

    @NotNull
    public WalletBalance updateWalletBalance(@NotNull Wallet wallet, @NotNull CryptoTransaction transaction) {
        Asset asset = transaction.getAsset();
        WalletBalance walletBalance = getWalletBalancesByWalletAndAsset(wallet, asset);

        updateAvgBuySellPrice(walletBalance, transaction);
        walletBalance.setAmount(calculateBalanceAfterSupply(walletBalance, transaction));
        walletBalance.setCost(calculateTotalCost(transaction, walletBalance));
        walletBalance.setLastTimeUpdated(LocalDateTime.now());

        repository.save(walletBalance);
        return walletBalance;
    }

    private static double calculateTotalCost(CryptoTransaction transaction, WalletBalance walletBalance) {
        return walletBalance.getCost() + MathUtils.withSign(transaction.getOrderTotalCost(), transaction.isBuyTransaction());
    }

    public double calculateBalanceAfterSupply(WalletBalance walletBalance, CryptoTransaction transaction) {
        double quantityToAdd = MathUtils.withSign(transaction.getOrderQuantity(), transaction.isBuyTransaction());
        double tokensAmountAfterSupply = walletBalance.getAmount() + quantityToAdd;

        if (tokensAmountAfterSupply < 0)
            throw new InvalidBalanceAmount("The amount of tokens cannot be negative.");

        if (tokensAmountAfterSupply >= Double.MAX_VALUE)
            throw new InvalidBalanceAmount("The amount of tokens is too high.");

        return tokensAmountAfterSupply;
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

    public static double calculateNewAvgPrice(double prevAvg, double prevAmount, double newAmount, double newPrice) {
        double totalValue = (prevAmount * prevAvg) + (newAmount * newPrice);
        double totalAmount = prevAmount + newAmount;
        return MathUtils.safeZeroDivision(totalValue, totalAmount);
    }

}
