package com.example.application.services.crypto;

import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.CryptoTransaction;
import com.example.application.entities.crypto.Wallet;
import com.example.application.entities.crypto.WalletBalance;
import com.example.application.repositories.crypto.CryptoTransactionRepository;
import com.example.application.repositories.crypto.WalletBalanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CryptoTransactionService {

    private final WalletBalanceRepository walletBalanceRepository;
    private final CryptoTransactionRepository transactionRepository;

    @Autowired
    public CryptoTransactionService(WalletBalanceRepository walletBalanceRepository,
                                    CryptoTransactionRepository transactionRepository) {
        this.walletBalanceRepository = walletBalanceRepository;
        this.transactionRepository = transactionRepository;
    }

    public List<CryptoTransaction> findBy(Wallet wallet) {
        return transactionRepository.findByWallet(wallet);
    }

    public List<CryptoTransaction> findBy(Wallet wallet, Asset asset) {
        return transactionRepository.findByWalletAndTradedAsset(wallet, asset);
    }

    public List<CryptoTransaction> findBy(Wallet wallet, Asset asset, CryptoTransaction.Type type) {
        return transactionRepository.findByWalletAndTradedAssetAndType(wallet, asset, type);
    }

    public CryptoTransaction saveTransaction(CryptoTransaction transaction) {
        updateTransactionAmountIfRequired(transaction);

        CryptoTransaction savedTransaction = transactionRepository.save(transaction);
        System.out.printf("Saved Transaction -> %s\n", savedTransaction);

        WalletBalance walletBalance = findWalletBalanceByTransaction(transaction);
        processTransaction(walletBalance, savedTransaction);

        return savedTransaction;
    }

    private static void updateTransactionAmountIfRequired(CryptoTransaction transaction) {
        if (transaction.getOrderQuantity() == 0) {
            transaction.setOrderQuantity(transaction.getOrderTotalCost() / transaction.getMarketPrice());
        }
    }

    private void processTransaction(WalletBalance walletBalance, CryptoTransaction transaction) {
        double processAmount = transaction.isBuyTransaction()
                ? transaction.getOrderQuantity()
                : -transaction.getOrderQuantity();

        double newBalance = walletBalance.getAmount() + processAmount;

        if (newBalance < 0)
            throw new IllegalArgumentException("Insufficient balance to fill the transaction.");

        walletBalance.setAmount(newBalance);
        walletBalanceRepository.save(walletBalance);
    }

    public WalletBalance findWalletBalanceByTransaction(CryptoTransaction transaction) {
        return walletBalanceRepository
                .findByWalletAndAsset(transaction.getWallet(), transaction.getTradedAsset())
                .orElseThrow(() -> new IllegalStateException("Wallet balance not found"));
    }

    public void deleteTransaction(CryptoTransaction transaction) {
        transactionRepository.delete(transaction);
    }

}
