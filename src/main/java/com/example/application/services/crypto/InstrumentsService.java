package com.example.application.services.crypto;

import com.example.application.data.enums.Symbols;
import com.example.application.data.models.crypto.CryptoTransaction;
import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.AssetWatcher;
import com.example.application.repositories.crypto.AssetRepository;
import com.example.application.repositories.crypto.AssetWatcherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InstrumentsService {

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private AssetWatcherRepository assetWatcherRepository;

    @Autowired
    private CryptoTransactionService transactionService;

    public Asset getAssetBySymbol(String symbolName) {
        return assetRepository.findBySymbol(symbolName.toUpperCase());
    }

    public Asset getAssetBySymbol(Symbols symbol) {
        return assetRepository.findBySymbol(symbol.name());
    }

    /*
     * AssetWatcher
     * */

    public List<AssetWatcher> getAssetWatchersByAsset(Asset asset) {
        return assetWatcherRepository.findByAsset(asset);
    }

    public List<AssetWatcher> getAssetWatchersByAssetAndActionType(Asset asset, AssetWatcher.ActionType actionType) {
        return assetWatcherRepository.findByAssetAndActionType(asset, actionType);
    }

    public AssetWatcher saveAssetWatcher(AssetWatcher assetWatcher) {
        return assetWatcherRepository.save(assetWatcher);
    }

    public void deleteAssetWatcher(AssetWatcher assetWatcher) {
        assetWatcherRepository.delete(assetWatcher);
    }

    /*
     * Transactions
     * */

    public List<CryptoTransaction> getTransactions() {
        return transactionService.getTransactions();
    }

    public List<CryptoTransaction> getTransactionsByAsset(Asset asset) {
        return transactionService.getTransactionsByAsset(asset);
    }

    public CryptoTransaction saveTransaction(CryptoTransaction transaction) {
        return transactionService.saveTransaction(transaction);
    }

    public void deleteTransaction(CryptoTransaction transaction) {
        transactionService.deleteTransaction(transaction);
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
