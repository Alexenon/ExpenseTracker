package com.example.application.services.crypto;

import com.example.application.entities.common.TransactionType;
import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.CryptoTransaction;
import com.example.application.entities.crypto.Wallet;
import com.example.application.repositories.crypto.CryptoTransactionRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class CryptoTransactionService {

    private final WalletBalanceService walletBalanceService;
    private final CryptoTransactionRepository transactionRepository;

    @Autowired
    public CryptoTransactionService(CryptoTransactionRepository transactionRepository, WalletBalanceService walletBalanceService) {
        this.walletBalanceService = walletBalanceService;
        this.transactionRepository = transactionRepository;
    }

    public List<CryptoTransaction> findBy(Wallet wallet) {
        return transactionRepository.findByWallet(wallet);
    }

    public List<CryptoTransaction> findBy(Wallet wallet, Asset asset) {
        return transactionRepository.findByWalletAndAsset(wallet, asset);
    }

    public List<CryptoTransaction> findBy(Wallet wallet, Asset asset, TransactionType type) {
        return transactionRepository.findByWalletAndAssetAndType(wallet, asset, type);
    }

    public List<CryptoTransaction> findBy(@NotNull Wallet wallet, @NotNull LocalDate from, @NotNull LocalDate to) {
        Objects.requireNonNull(wallet, "wallet");
        Objects.requireNonNull(from, "from");
        Objects.requireNonNull(to, "to");
        LocalDateTime fromDateTime = from.atStartOfDay();
        LocalDateTime toDateTime = to.plusDays(1).atStartOfDay();
        return transactionRepository.findByWalletAndDateTimeBetween(wallet, fromDateTime, toDateTime);
    }

    @Transactional
    public CryptoTransaction saveTransaction(@NotNull CryptoTransaction transaction) {
        Objects.requireNonNull(transaction, "transaction");
        updateTransactionAmount(transaction);
        walletBalanceService.updateWalletBalance(transaction);
        CryptoTransaction savedTransaction = transactionRepository.save(transaction);
        System.out.printf("Saved Transaction -> %s\n", savedTransaction);
        return savedTransaction;
    }

    public void deleteTransaction(@NotNull CryptoTransaction transaction) {
        transactionRepository.delete(Objects.requireNonNull(transaction, "transaction"));
    }

    /**
     * Sets order quantity in case it's missing in the transaction itself
     */
    private static void updateTransactionAmount(@NotNull CryptoTransaction transaction) {
        Assert.isTrue(transaction.getMarketPrice() > 0, "Price should be above 0");
        Assert.isTrue(transaction.getOrderTotalCost() > 0, "Order cost should be above 0");
        Assert.notNull(transaction.getDateTime(), "DateTime is missing");

        if (transaction.getOrderQuantity() > 0)
            return;

        double orderQuantity = transaction.getOrderTotalCost() / transaction.getMarketPrice();
        transaction.setOrderQuantity(orderQuantity);
    }

}
