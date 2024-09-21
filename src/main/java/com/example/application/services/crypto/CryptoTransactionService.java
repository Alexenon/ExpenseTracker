package com.example.application.services.crypto;

import com.example.application.data.models.crypto.CryptoTransaction;
import com.example.application.data.models.crypto.WalletBalance;
import com.example.application.entities.crypto.Asset;
import com.example.application.repositories.crypto.CryptoTransactionRepository;
import com.example.application.repositories.crypto.WalletBalanceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CryptoTransactionService {

    private final WalletBalanceRepository walletBalanceRepository;
    private final CryptoTransactionRepository transactionRepository;

    public CryptoTransactionService(WalletBalanceRepository walletBalanceRepository,
                                    CryptoTransactionRepository transactionRepository) {
        this.walletBalanceRepository = walletBalanceRepository;
        this.transactionRepository = transactionRepository;
    }

    public List<CryptoTransaction> getTransactions() {
        return transactionRepository.findAll();
    }

    public List<CryptoTransaction> getTransactionsByAsset(Asset asset) {
        return transactionRepository.findByAsset(asset);
    }

    public CryptoTransaction saveTransaction(CryptoTransaction transaction) {
        transaction.setOrderQuantity(transaction.getOrderTotalCost() / transaction.getMarketPrice());

        CryptoTransaction savedTransaction = transactionRepository.save(transaction);
        System.out.printf("Saved Transaction -> %s\n", savedTransaction);

        // Find the WalletBalance for the asset in the wallet
        WalletBalance walletBalance = walletBalanceRepository.findByWalletAndAsset(
                        transaction.getWallet(), transaction.getAsset())
                .orElseThrow(() -> new IllegalStateException("Wallet balance not found"));

        // Update the wallet balance amount
        if (transaction.isSellTransaction()) {
            if (walletBalance.getAmount() < transaction.getOrderQuantity()) {
                throw new IllegalArgumentException("Insufficient balance to complete the sell transaction.");
            }

            walletBalance.setAmount(walletBalance.getAmount() - transaction.getOrderQuantity());
        } else if (transaction.isBuyTransaction()) {
            walletBalance.setAmount(walletBalance.getAmount() + transaction.getOrderQuantity());
        }

        walletBalanceRepository.save(walletBalance);

        return savedTransaction;
    }

    public void deleteTransaction(CryptoTransaction transaction) {
        transactionRepository.delete(transaction);
    }


}
