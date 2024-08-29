package com.example.application.services.crypto;

import com.example.application.data.enums.Symbols;
import com.example.application.data.models.crypto.CryptoTransaction;
import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.AssetWatcher;
import com.example.application.repositories.crypto.AssetRepository;
import com.example.application.repositories.crypto.AssetWatcherRepository;
import com.example.application.repositories.crypto.CryptoTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InstrumentsService {

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private AssetWatcherRepository assetWatcherRepository;

    @Autowired
    private CryptoTransactionRepository transactionRepository;

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
        return transactionRepository.findAll();
    }

    public List<CryptoTransaction> getTransactionsByAsset(Asset asset) {
        return transactionRepository.findByAsset(asset);
    }

    public CryptoTransaction saveTransaction(CryptoTransaction transaction) {
        transaction.setOrderQuantity(transaction.getOrderTotalCost() / transaction.getMarketPrice());
        return transactionRepository.save(transaction);
    }

    public void deleteTransaction(CryptoTransaction transaction) {
        transactionRepository.delete(transaction);
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
