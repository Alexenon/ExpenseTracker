package com.example.application.services.crypto;

import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.CryptoTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/*
    TODO: LONG TERM -> Implement next methods
       - Profit Loss of the assets compared to the CURRENT price (realized / unrealized)
       - Profit Loss of the assets compared to the ALL TIME price (realized / unrealized) -> ALL TIME takes care all the bought/sold
       - Portfolio Diversity
       - % in Market, how much tokens had been sold and how much are still holding
       - Time Holding
       - Portfolio Avg Time Holding
* */

@Component
public class PortfolioPerformanceTracker {

    private final InstrumentsFacadeService instrumentsFacadeService;

    @Autowired
    public PortfolioPerformanceTracker(InstrumentsFacadeService instrumentsFacadeService) {
        this.instrumentsFacadeService = instrumentsFacadeService;
    }

    public double getAverageBuyPrice(Asset asset) {
        List<CryptoTransaction> transactions = instrumentsFacadeService.getTransactionsByAsset(asset);
        double totalCost = calculateTotalCostForBuyTransactions(transactions);
        double totalQuantity = calculateTotalQuantityForBuyTransactions(transactions);

        return calculateAveragePrice(totalCost, totalQuantity);
    }

    public double getAverageSellPrice(Asset asset) {
        List<CryptoTransaction> transactions = instrumentsFacadeService.getTransactionsByAsset(asset);
        double totalSellCost = calculateTotalCostForSellTransactions(transactions);
        double totalQuantitySold = calculateTotalQuantityForSellTransactions(transactions);

        return calculateAveragePrice(totalSellCost, totalQuantitySold);
    }

    public double getAveragePriceForRemainingTokens(Asset asset) {
        List<CryptoTransaction> transactions = instrumentsFacadeService.getTransactionsByAsset(asset);
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

    public double getPortfolioCost(List<CryptoTransaction> transactions) {
        return transactions.stream()
                .collect(Collectors.groupingBy(CryptoTransaction::getAsset,
                        Collectors.summingDouble(t -> (t.isBuyTransaction() ? 1 : -1) * t.getOrderQuantity() * t.getMarketPrice())))
                .values().stream().mapToDouble(Double::doubleValue).sum();
    }

    public double getTotalAssetCost(List<CryptoTransaction> transactions) {
        return calculateTotalCostForBuyTransactions(transactions);
    }

    public double getTotalAssetWorth(Asset asset) {
        return instrumentsFacadeService.getAmountOfTokens(asset) * instrumentsFacadeService.getAssetPrice(asset);
    }

    public double getAssetDiversityPercentage(Asset asset) {
        return 0;
    }

    public double getPortfolioWorth() {
        return instrumentsFacadeService.getWalletBalances().stream()
                .mapToDouble(balance -> balance.getAmount() * instrumentsFacadeService.getAssetPrice(balance.getAsset()))
                .sum();
    }

    // TODO: getPortfolioCost
    public double getPortfolioCost() {
        return 0;
    }

    /*
     * Helper methods to calculate
     * */

    private double calculateTotalCostForBuyTransactions(List<CryptoTransaction> transactions) {
        return transactions.stream()
                .filter(CryptoTransaction::isBuyTransaction)
                .mapToDouble(this::calculateCost)
                .sum();
    }

    private double calculateTotalQuantityForBuyTransactions(List<CryptoTransaction> transactions) {
        return transactions.stream()
                .filter(CryptoTransaction::isBuyTransaction)
                .mapToDouble(CryptoTransaction::getOrderQuantity)
                .sum();
    }

    private double calculateTotalCostForSellTransactions(List<CryptoTransaction> transactions) {
        return transactions.stream()
                .filter(CryptoTransaction::isSellTransaction)
                .mapToDouble(this::calculateCost)
                .sum();
    }

    private double calculateTotalQuantityForSellTransactions(List<CryptoTransaction> transactions) {
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
