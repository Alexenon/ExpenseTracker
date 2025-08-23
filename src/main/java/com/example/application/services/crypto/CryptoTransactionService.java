package com.example.application.services.crypto;

import com.example.application.entities.common.TransactionType;
import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.CryptoTransaction;
import com.example.application.entities.crypto.Wallet;
import com.example.application.entities.crypto.WalletBalance;
import com.example.application.repositories.crypto.CryptoTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public CryptoTransaction saveTransaction(CryptoTransaction transaction) {
        Objects.requireNonNull(transaction, "transaction");
        updateTransactionAmount(transaction);
        walletBalanceService.updateWalletBalance(transaction);
        CryptoTransaction savedTransaction = transactionRepository.save(transaction);
        System.out.printf("Saved Transaction -> %s\n", savedTransaction);
        return savedTransaction;
    }

    // Sets order quantity in case it's missing in the transaction itself -> TODO: SHOULD BE NOT ALLOWED IDEALLY
    private static void updateTransactionAmount(CryptoTransaction transaction) {
        double orderQuantity = transaction.getOrderQuantity() > 0
                ? transaction.getOrderQuantity()
                : transaction.getOrderTotalCost() / transaction.getMarketPrice();
        transaction.setOrderQuantity(orderQuantity);
    }

    public WalletBalance findWalletBalanceByTransaction(CryptoTransaction transaction) {
        return walletBalanceService.getByWalletAndAsset(transaction.getWallet(), transaction.getAsset());
    }

    public void deleteTransaction(CryptoTransaction transaction) {
        transactionRepository.delete(transaction);
    }

}
