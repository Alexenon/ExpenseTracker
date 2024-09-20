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

    public String getName() {
        return assetInfo.getName();
    }

    public String getSymbol() {
        return assetInfo.getSymbol();
    }

    public double getPrice() {
        return assetInfo.getPriceUsd();
    }

    public double getChangesLast24hPercentage() {
        return assetInfo.getSpotMoving24HourChangePercentageUsd();
    }

    public double getAverageBuyPrice() {
        double totalCost = calculateTotalCostForBuyTransactions();
        double totalQuantity = calculateTotalQuantityForBuyTransactions();

        return calculateAveragePrice(totalCost, totalQuantity);
    }

    public double getAveragePriceForRemainingTokens() {
        double totalCost = 0;
        double totalQuantity = 0;

        for (CryptoTransaction transaction : transactions) {
            if (transaction.isBuyTransaction()) {
                totalCost += calculateCost(transaction);
                totalQuantity += transaction.getOrderQuantity();
            } else if (transaction.isSellTransaction()) {
                totalCost = adjustCostForSale(transaction, totalCost, totalQuantity);
                totalQuantity -= transaction.getOrderQuantity();
            }
        }

        return calculateAveragePrice(totalCost, totalQuantity);
    }

    public double getAverageSellPrice() {
        double totalSellCost = calculateTotalCostForSellTransactions();
        double totalQuantitySold = calculateTotalQuantityForSellTransactions();

        return calculateAveragePrice(totalSellCost, totalQuantitySold);
    }

    private double calculateTotalCostForBuyTransactions() {
        return transactions.stream()
                .filter(CryptoTransaction::isBuyTransaction)
                .mapToDouble(this::calculateCost)
                .sum();
    }

    private double calculateTotalQuantityForBuyTransactions() {
        return transactions.stream()
                .filter(CryptoTransaction::isBuyTransaction)
                .mapToDouble(CryptoTransaction::getOrderQuantity)
                .sum();
    }

    private double calculateTotalCostForSellTransactions() {
        return transactions.stream()
                .filter(CryptoTransaction::isSellTransaction)
                .mapToDouble(this::calculateCost)
                .sum();
    }

    private double calculateTotalQuantityForSellTransactions() {
        return transactions.stream()
                .filter(CryptoTransaction::isSellTransaction)
                .mapToDouble(CryptoTransaction::getOrderQuantity)
                .sum();
    }

    private double calculateCost(CryptoTransaction transaction) {
        return transaction.getOrderQuantity() * transaction.getMarketPrice();
    }

    private double calculateAveragePrice(double totalCost, double totalQuantity) {
        if (totalQuantity == 0)
            return 0;

        return totalCost / totalQuantity;
    }

    private double adjustCostForSale(CryptoTransaction transaction, double totalCost, double totalQuantity) {
        double quantitySold = transaction.getOrderQuantity();
        double averagePriceBeforeSale = calculateAveragePrice(totalCost, totalQuantity);
        return totalCost - (quantitySold * averagePriceBeforeSale);
    }


}
