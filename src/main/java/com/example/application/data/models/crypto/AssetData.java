package com.example.application.data.models.crypto;

import com.example.application.entities.crypto.Asset;
import com.example.application.utils.fetchers.api_responses.AssetInfo;
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

    public double getPrice() {
        return assetInfo.getPriceUsd();
    }

    public double getChangesLast24hPercentage() {
        return assetInfo.getSpotMoving24HourChangePercentageUsd();
    }
    
    public double getAverageBuyPrice() {
        double totalCost = transactions.stream()
                .filter(CryptoTransaction::isBuyTransaction)
                .mapToDouble(CryptoTransaction::getAmount)
                .sum();

        double totalQuantity = transactions.stream()
                .filter(CryptoTransaction::isBuyTransaction)
                .mapToDouble(CryptoTransaction::getAmount)
                .sum();

        return totalQuantity == 0 ? 0 : totalCost / totalQuantity;
    }

    public double getAverageSellPrice() {
        return 0;
    }


}
