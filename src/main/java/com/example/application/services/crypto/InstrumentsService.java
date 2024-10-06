package com.example.application.services.crypto;

import com.example.application.data.enums.Symbols;
import com.example.application.entities.User;
import com.example.application.entities.crypto.*;
import com.example.application.repositories.crypto.AssetRepository;
import com.example.application.repositories.crypto.WalletBalanceRepository;
import com.example.application.utils.fetchers.CryptoCompareFetcher;
import com.example.application.utils.fetchers.api_responses.AssetMetaDataApiResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InstrumentsService {

    private final WalletService walletService;
    private final AssetRepository assetRepository;
    private final AssetWatcherService assetWatcherService;
    private final CryptoTransactionService transactionService;
    private final WalletBalanceRepository walletBalanceRepository;

    @Autowired
    public InstrumentsService(WalletService walletService,
                              AssetRepository assetRepository,
                              CryptoTransactionService transactionService,
                              AssetWatcherService assetWatcherService,
                              WalletBalanceRepository walletBalanceRepository
    ) {
        this.walletService = walletService;
        this.assetRepository = assetRepository;
        this.transactionService = transactionService;
        this.assetWatcherService = assetWatcherService;
        this.walletBalanceRepository = walletBalanceRepository;
    }

    /*
     * ASSETS
     * */

    public List<Asset> getAllAssets() {
        return assetRepository.findAll();
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
        return assetWatcherService.save(assetWatcher);
    }

    public void deleteAssetWatcher(AssetWatcher assetWatcher) {
        assetWatcherService.delete(assetWatcher);
    }

    public List<AssetWatcher> getAssetWatchersByAsset(Asset asset) {
        return assetWatcherService.findBy(asset);
    }

    public List<AssetWatcher> getAssetWatchersByAsset(Wallet wallet, Asset asset) {
        return assetWatcherService.findBy(wallet, asset);
    }

    public List<AssetWatcher> getAssetWatchersByAssetAndActionType(Wallet wallet, Asset asset, AssetWatcher.ActionType actionType) {
        return assetWatcherService.findBy(wallet, asset, actionType);
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

            AssetMetaDataApiResp metaData = CryptoCompareFetcher.getCoinMetaData(symbol);
            System.out.printf("%s(\"%s\"),\n", metaData.getData().getSymbol(),
                    metaData.getData().getName());
        });

        System.out.println("Filled database");
    }

}
