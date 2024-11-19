package com.example.application.services.crypto;

import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.CryptoTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/*
    TODO: LONG TERM -> Implement next methods
       - % in Market, how much tokens had been sold and how much are still holding
       - Total Time Holding + Asset Avg Time Holding
       - ROI
       - Cumulative Profit Loss
* */

@Component
public class PortfolioPerformanceTracker {

    private final InstrumentsFacadeService instrumentsFacadeService;

    @Autowired
    public PortfolioPerformanceTracker(InstrumentsFacadeService instrumentsFacadeService) {
        this.instrumentsFacadeService = instrumentsFacadeService;
    }

    public double getAverageBuyPrice(Asset asset) {
        if(asset == null)
            return 0;

        List<CryptoTransaction> transactions = instrumentsFacadeService.getTransactionsByAsset(asset);
        double totalCost = calculateTotalCostForBuyTransactions(transactions);
        double totalQuantity = calculateTotalQuantityForBuyTransactions(transactions);

        return calculateAveragePrice(totalCost, totalQuantity);
    }

    public double getAverageSellPrice(Asset asset) {
        if(asset == null)
            return 0;

        List<CryptoTransaction> transactions = instrumentsFacadeService.getTransactionsByAsset(asset);
        double totalSellCost = calculateTotalCostForSellTransactions(transactions);
        double totalQuantitySold = calculateTotalQuantityForSellTransactions(transactions);

        return calculateAveragePrice(totalSellCost, totalQuantitySold);
    }

    public double getAveragePriceForRemainingTokens(Asset asset) {
        if(asset == null)
            return 0;

        double totalCost = 0;
        double totalQuantity = 0;
        for (CryptoTransaction transaction : instrumentsFacadeService.getTransactionsByAsset(asset)) {
            if (transaction.isBuyTransaction()) {
                totalCost += transaction.getOrderTotalCost();
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
        List<CryptoTransaction> transactions = instrumentsFacadeService.getTransactionsByAsset(asset);
        return calculateTotalCostForBuyTransactions(transactions) - calculateTotalCostForSellTransactions(transactions);
    }

    public double getAssetProfit(Asset asset) {
        return getAssetTotalWorth(asset) - getAssetTotalCost(asset);
    }

    public double getAssetProfitPercentage(Asset asset) {
        return getAssetTotalWorth(asset) / getAssetTotalCost(asset);
    }

    public double getAssetAverageHoldingDays(Asset asset) {
        long totalHoldingTime = 0;
        int countSellTransactions = 0;

        List<CryptoTransaction> transactions = instrumentsFacadeService.getTransactionsByAsset(asset);

        List<CryptoTransaction> buyTransactions = transactions.stream()
                .filter(t -> t.isBuyTransaction() && t.getAsset().equals(asset))
                .sorted(Comparator.comparing(CryptoTransaction::getDate))
                .toList();

        // Iterate through buy transactions and match them with sell transactions
        for (int i = 0; i < buyTransactions.size(); i++) {
            CryptoTransaction buyTransaction = buyTransactions.get(i);

            for (int j = i; j < transactions.size(); j++) {
                CryptoTransaction sellTransaction = transactions.get(j);

                if (sellTransaction.isSellTransaction() && sellTransaction.getAsset().equals(buyTransaction.getAsset())) {
                    long holdingTime = getHoldingTimeInDays(buyTransaction, sellTransaction);
                    totalHoldingTime += holdingTime;
                    countSellTransactions++;
                    break; // Move to the next buy transaction
                }
            }
        }

        return countSellTransactions == 0 ? totalHoldingTime : (double) totalHoldingTime / countSellTransactions;
    }

    public long getHoldingTimeInDays(CryptoTransaction buyTransaction, CryptoTransaction sellTransaction) {
        if (sellTransaction.isBuyTransaction() && sellTransaction.isSellTransaction()) {
            return ChronoUnit.DAYS.between(buyTransaction.getDate(), sellTransaction.getDate());
        }
        return 0;
    }


    public double getPortfolioWorth() {
        return instrumentsFacadeService.getWalletBalances().stream()
                .mapToDouble(balance -> balance.getAmount() * instrumentsFacadeService.getAssetPrice(balance.getAsset()))
                .sum();
    }

    public double getPortfolioCost() {
        return instrumentsFacadeService.getAllTransactions().stream()
                .collect(Collectors.groupingBy(CryptoTransaction::getAsset,
                        Collectors.summingDouble(t -> (t.isBuyTransaction() ? 1 : -1) * t.getOrderTotalCost())))
                .values().stream().mapToDouble(Double::doubleValue).sum();
    }

    public double getPortfolioProfit() {
        return getPortfolioWorth() - getPortfolioCost();
    }

    public double getPortfolioRealizedProfit() {
        return getPortfolioWorth() - getPortfolioCost();
    }

    public double getPortfolioUnrealizedProfit() {
        return getPortfolioWorth() - getPortfolioCost();
    }

    public double getPortfolioAverageHoldingDays() {
        return instrumentsFacadeService.getAllTransactions()
                .stream()
                .filter(CryptoTransaction::isBuyTransaction)
                .map(CryptoTransaction::getAsset)
                .distinct()
                .mapToDouble(this::getAssetAverageHoldingDays)
                .average()
                .orElse(0);
    }

    public String getPortfolioBuySellRatio() {
        List<CryptoTransaction> transactions = instrumentsFacadeService.getAllTransactions();
        double buyCost = calculateTotalCostForBuyTransactions(transactions);
        double sellCost = calculateTotalCostForSellTransactions(transactions);
        double totalCost = buyCost + sellCost;

        double buyRatio = buyCost / totalCost * 100;
        double sellRatio = sellCost / totalCost * 100;

        return String.format("%d : %d", Math.round(buyRatio), Math.round(sellRatio));
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
                .mapToDouble(CryptoTransaction::getOrderTotalCost)
                .sum();
    }

    private double calculateTotalCostForSellTransactions(List<CryptoTransaction> transactions) {
        return transactions.stream()
                .filter(CryptoTransaction::isSellTransaction)
                .mapToDouble(CryptoTransaction::getOrderTotalCost)
                .sum();
    }

    private double calculateTotalQuantityForBuyTransactions(List<CryptoTransaction> transactions) {
        return transactions.stream()
                .filter(CryptoTransaction::isBuyTransaction)
                .mapToDouble(CryptoTransaction::getOrderQuantity)
                .sum();
    }

    private double calculateTotalQuantityForSellTransactions(List<CryptoTransaction> transactions) {
        return transactions.stream()
                .filter(CryptoTransaction::isSellTransaction)
                .mapToDouble(CryptoTransaction::getOrderQuantity)
                .sum();
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
