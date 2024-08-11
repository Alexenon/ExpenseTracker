package com.example.application.data.models.crypto;

import com.example.application.entities.crypto.Asset;
import com.example.application.utils.responses.AssetInfo;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AssetData {

    private Asset asset;
    private AssetInfo assetInfo;
    private List<CryptoTransaction> transactions;

    public AssetData(Asset asset, AssetInfo assetInfo) {
        this.asset = asset;
        this.assetInfo = assetInfo;
        this.transactions = new ArrayList<>();
    }
    
    public double getAverageBuyPrice() {
        double totalCost = transactions.stream()
                .filter(transaction -> transaction.getType().equals(CryptoTransaction.TransactionType.BUY))
                .mapToDouble(CryptoTransaction::getAmountUSD)
                .sum();

        double totalQuantity = transactions.stream()
                .filter(transaction -> transaction.getType().equals(CryptoTransaction.TransactionType.BUY))
                .mapToDouble(CryptoTransaction::getQuantity)
                .sum();

        return totalQuantity == 0 ? 0 : totalCost / totalQuantity;
    }

    public double getAverageSellPrice() {
        return 0;
    }


}
