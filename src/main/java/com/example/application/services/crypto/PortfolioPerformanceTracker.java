package com.example.application.services.crypto;

import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.CryptoTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/*
    TODO: LONG TERM -> Implement next methods
       - % in Market, how much tokens had been sold and how much are still holding
       - Time Holding + Portfolio Avg Time Holding
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
        double totalCost = 0;
        double totalQuantity = 0;

        for (CryptoTransaction transaction : instrumentsFacadeService.getTransactionsByAsset(asset)) {
            if (transaction.isBuyTransaction()) {
                totalCost += transactionCost(transaction);
                totalQuantity += transaction.getOrderQuantity();
            } else if (transaction.isSellTransaction()) {
                totalCost = adjustCostForSale(transaction, totalCost, totalQuantity);
                totalQuantity -= transaction.getOrderQuantity();
            }
        }

        return calculateAveragePrice(totalCost, totalQuantity);
    }

    public double getAssetTotalWorth(Asset asset) {
        return instrumentsFacadeService.getAmountOfTokens(asset) * instrumentsFacadeService.getAssetPrice(asset);
    }

    public double getAssetTotalCost(Asset asset) {
        return calculateTotalCostForBuyTransactions(instrumentsFacadeService.getTransactionsByAsset(asset));
    }

    public double getAssetProfit(Asset asset) {
        return getAssetTotalWorth(asset) - getAssetTotalCost(asset);
    }

    public double getAssetProfitPercentage(Asset asset) {
        return getAssetTotalWorth(asset) / getAssetTotalCost(asset) * 100 - 100;
    }

    public double getPortfolioWorth() {
        return instrumentsFacadeService.getWalletBalances().stream()
                .mapToDouble(balance -> balance.getAmount() * instrumentsFacadeService.getAssetPrice(balance.getAsset()))
                .sum();
    }

    public double getPortfolioCost() {
        return instrumentsFacadeService.getAllTransactions().stream()
                .collect(Collectors.groupingBy(CryptoTransaction::getAsset,
                        Collectors.summingDouble(t -> (t.isBuyTransaction() ? 1 : -1) * transactionCost(t))))
                .values().stream().mapToDouble(Double::doubleValue).sum();
    }

    public double getPortfolioProfit() {
        return getPortfolioWorth() - getPortfolioCost();
    }

    public double getPortfolioProfitPercentage() {
        return getPortfolioWorth() / getPortfolioCost() * 100 - 100;
    }

    /**
     * @return the asset diversity percentage in the portfolio, range (0 - 100)%
     */
    public int getAssetDiversityPercentage(Asset asset) {
        return Math.toIntExact(Math.round(getAssetTotalWorth(asset) / getPortfolioWorth() * 100));
    }

    /*
     * Helper methods to calculate
     * */

    private double calculateTotalCostForBuyTransactions(List<CryptoTransaction> transactions) {
        return transactions.stream()
                .filter(CryptoTransaction::isBuyTransaction)
                .mapToDouble(this::transactionCost)
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
                .mapToDouble(this::transactionCost)
                .sum();
    }

    private double calculateTotalQuantityForSellTransactions(List<CryptoTransaction> transactions) {
        return transactions.stream()
                .filter(CryptoTransaction::isSellTransaction)
                .mapToDouble(CryptoTransaction::getOrderQuantity)
                .sum();
    }

    private double transactionCost(CryptoTransaction transaction) {
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
