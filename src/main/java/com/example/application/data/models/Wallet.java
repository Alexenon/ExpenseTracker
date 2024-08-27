package com.example.application.data.models;

import com.example.application.data.models.crypto.CryptoTransaction;
import com.example.application.entities.User;
import com.example.application.entities.crypto.Asset;

import java.util.ArrayList;
import java.util.List;

public class Wallet {

    private final List<Asset> assets;
    private final List<CryptoTransaction> transactions;
    private final User user;

    public Wallet() {
        this.assets = new ArrayList<>();
        this.transactions = new ArrayList<>();
        this.user = null;
    }

    public void addTransaction(CryptoTransaction transaction) {
        transactions.add(transaction);
        Asset transactionAsset = getAsset(transaction.getAssetReceived());
//        transactionAsset.setHoldingAmount(transaction.getAmount());
    }


    // TODO: Format Asset.equals() method
    public Asset getAsset(Asset asset) {
        return assets.stream()
                .filter(a -> asset.getSymbol().equals(a.getSymbol()))
                .findFirst()
                .orElseThrow();
    }


//    @Override
//    public int hashCode() {
//        return super.hashCode();
//    }
}
