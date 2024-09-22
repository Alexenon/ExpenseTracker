package com.example.application.services.crypto;

import com.example.application.data.enums.Symbols;
import com.example.application.entities.User;
import com.example.application.entities.crypto.*;
import com.example.application.repositories.crypto.AssetRepository;
import com.example.application.repositories.crypto.AssetWatcherRepository;
import com.example.application.repositories.crypto.WalletBalanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InstrumentsService {

    private final WalletService walletService;
    private final AssetRepository assetRepository;
    private final CryptoTransactionService transactionService;
    private final AssetWatcherRepository assetWatcherRepository;
    private final WalletBalanceRepository walletBalanceRepository;

    @Autowired
    public InstrumentsService(WalletService walletService,
                              AssetRepository assetRepository,
                              CryptoTransactionService transactionService,
                              AssetWatcherRepository assetWatcherRepository,
                              WalletBalanceRepository walletBalanceRepository
    ) {
        this.walletService = walletService;
        this.assetRepository = assetRepository;
        this.transactionService = transactionService;
        this.assetWatcherRepository = assetWatcherRepository;
        this.walletBalanceRepository = walletBalanceRepository;
    }

    public Asset getAssetBySymbol(String symbolName) {
        return assetRepository.findBySymbol(symbolName.toUpperCase());
    }

    public Asset getAssetBySymbol(Symbols symbol) {
        return assetRepository.findBySymbol(symbol.name());
    }

    /*
     * AssetWatcher
     * */

    public AssetWatcher saveAssetWatcher(AssetWatcher assetWatcher) {
        return assetWatcherRepository.save(assetWatcher);
    }

    public void deleteAssetWatcher(AssetWatcher assetWatcher) {
        assetWatcherRepository.delete(assetWatcher);
    }

    public List<AssetWatcher> getAssetWatchersByAsset(Asset asset) {
        return assetWatcherRepository.findBy(asset);
    }

    public List<AssetWatcher> getAssetWatchersByAsset(Wallet wallet, Asset asset) {
        return assetWatcherRepository.findBy(wallet, asset);
    }

    public List<AssetWatcher> getAssetWatchersByAssetAndActionType(Wallet wallet, Asset asset, AssetWatcher.ActionType actionType) {
        return assetWatcherRepository.findBy(wallet, asset, actionType);
    }

    /*
     * Transactions
     * */

    public CryptoTransaction saveTransaction(CryptoTransaction transaction) {
        return transactionService.saveTransaction(transaction);
    }

    public void deleteTransaction(CryptoTransaction transaction) {
        transactionService.deleteTransaction(transaction);
    }

    public List<CryptoTransaction> getTransactionsBy(Wallet wallet) {
        return transactionService.findBy(wallet);
    }

    public List<CryptoTransaction> getTransactionsBy(Asset asset) {
        return transactionService.findBy(asset);
    }

    public List<CryptoTransaction> getTransactionsBy(CryptoTransaction.TransactionType type) {
        return transactionService.findBy(type);
    }

    public List<CryptoTransaction> getTransactionsBy(Wallet wallet, Asset asset) {
        return transactionService.findBy(wallet, asset);
    }

    public List<CryptoTransaction> getTransactionsBy(Wallet wallet, Asset asset, CryptoTransaction.TransactionType type) {
        return transactionService.findBy(wallet, asset, type);
    }

    /*
     * WALLETS
     * */

    public Wallet getWalletByUser(User user) {
        return walletService.getWalletByUser(user);
    }

    /*
    * WALLET BALANCES
    * */

    public WalletBalance getWalletBalancesByWalletAndAsset(Wallet wallet, Asset asset) {
        return walletBalanceRepository.findByWalletAndAsset(wallet, asset).orElseThrow();
    }

    public List<WalletBalance> getWalletBalancesByWallet(Wallet wallet) {
        return walletBalanceRepository.findByWallet(wallet);
    }

    /*
     * OTHERS
     * */

    public void updateDatabase() {
        Symbols.getAll().forEach(symbol -> {
            if (assetRepository.findBySymbol(symbol) == null) {
                assetRepository.save(new Asset(symbol));
            }
        });

        System.out.println("Filled database");
    }

}
