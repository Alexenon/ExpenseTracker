package com.example.application.services.crypto;

import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.CryptoTransaction;
import com.example.application.utils.common.MathUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.example.application.utils.investment.ProfitUtils.ONE_HUNDRED_PERCENT;

/*
    TODO: LONG TERM -> Implement next methods
       - % in Market, how much tokens had been sold and how much are still holding
       - ROI
       - Cumulative Profit Loss
       [!!!] Make methods like `calculateTotalQuantityForBuyTransactions()`, to have 2 paramets
                1. List of transactions
                2. Asset or asset filter
            To be 100% that there will be no error on calculating such stuff
            ALSO: Don't allow to get by List<Transaction> ----> getAverageBuy(asset, startDate, endDate)

       TODO:
          [!] Divide stastics in 3 categories
                - overall statistics (starting from first day of trading)
                - current statistics (just for the assets that are in user portfolio)
                - flexible statistics (For a range period: LAST 30 DAYS, 90 DAYS, 180 DAYS, 360 DAYS)
* */

@Service
public class PortfolioPerformanceTracker {

    private final InstrumentsFacadeService instrumentsFacadeService;

    @Autowired
    public PortfolioPerformanceTracker(InstrumentsFacadeService instrumentsFacadeService) {
        this.instrumentsFacadeService = instrumentsFacadeService;
    }

    public double getAverageBuyPrice(Asset asset) {
        if (asset == null)
            return 0;

        List<CryptoTransaction> transactions = instrumentsFacadeService.getTransactionsByAsset(asset);
        double totalCost = calculateTotalCostForBuyTransactions(transactions);
        double totalQuantity = calculateTotalQuantityForBuyTransactions(transactions);
        return calculateAveragePrice(totalCost, totalQuantity);
    }

    public double getAverageSellPrice(Asset asset) {
        if (asset == null)
            return 0;

        List<CryptoTransaction> transactions = instrumentsFacadeService.getTransactionsByAsset(asset);
        double totalSellCost = calculateTotalCostForSellTransactions(transactions);
        double totalQuantitySold = calculateTotalQuantityForSellTransactions(transactions);
        return calculateAveragePrice(totalSellCost, totalQuantitySold);
    }

    public double getAveragePriceForRemainingTokens(Asset asset) {
        if (asset == null)
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

    //region ASSET STATS
    public double getAssetTotalWorth(Asset asset) {
        return instrumentsFacadeService.getAmountOfTokens(asset) * instrumentsFacadeService.getAssetMarketPrice(asset);
    }

    public double getAssetTotalCost(Asset asset) {
        return calculateTotalCostForBuyTransactions(instrumentsFacadeService.getTransactionsByAsset(asset));
    }

    public double getAssetTotalProfit(Asset asset) {
        return getAssetRealizedProfit(asset) + getAssetUnrealizedProfit(asset);
    }

    public double getAssetTotalNetProfit(Asset asset) {
        return getAssetTotalProfit(asset) - getAssetTotalCost(asset);
    }

    public double getAssetRealizedProfit(Asset asset) {
        double quantitySold = instrumentsFacadeService.getTransactionsByAsset(asset)
                .stream()
                .filter(CryptoTransaction::isSellTransaction)
                .mapToDouble(CryptoTransaction::getOrderQuantity)
                .sum();

        return (getAverageSellPrice(asset) - getAverageBuyPrice(asset)) * quantitySold;
    }

    public double getAssetUnrealizedProfit(Asset asset) {
        return getAssetTotalWorth(asset);
    }

    public double getAssetProfitPercentage(Asset asset) {
        return getAssetTotalWorth(asset) / getAssetTotalCost(asset);
    }

    public String getAssetBuySellRatio(Asset asset) {
        List<CryptoTransaction> transactions = instrumentsFacadeService.getTransactionsByAsset(asset);
        double buyCost = calculateTotalCostForBuyTransactions(transactions);
        double sellCost = calculateTotalCostForSellTransactions(transactions);
        double totalCost = buyCost + sellCost;

        double buyRatio = buyCost / totalCost * ONE_HUNDRED_PERCENT;
        double sellRatio = sellCost / totalCost * ONE_HUNDRED_PERCENT;

        return String.format("%d : %d", Math.round(buyRatio), Math.round(sellRatio));
    }

    public double getAssetAverageHoldingDays(Asset asset) {
        List<CryptoTransaction> sortedAssetTransactions = instrumentsFacadeService.getTransactionsByAsset(asset)
                .stream()
                .sorted(Comparator.comparing(CryptoTransaction::getDate))
                .toList();

        List<CryptoTransaction> buyTransactionsTillSale = new ArrayList<>();
        double totalWeightedHoldingTime = 0;
        double totalSoldQuantity = 0;

        for (CryptoTransaction transaction : sortedAssetTransactions) {
            if (transaction.isBuyTransaction()) {
                buyTransactionsTillSale.add(transaction);
                continue;
            }

            // Process sell transactions
            double quantityToSell = transaction.getOrderQuantity();
            long weightedHoldingTime = 0;

            for (CryptoTransaction buyTransaction : buyTransactionsTillSale) {
                if (quantityToSell <= 0) break;

                double buyQuantity = buyTransaction.getOrderQuantity();
                double soldQuantityFromThisBuy = Math.min(buyQuantity, quantityToSell);
                weightedHoldingTime += (long) (getHoldingTimeInDays(buyTransaction, transaction) * soldQuantityFromThisBuy);
                quantityToSell -= soldQuantityFromThisBuy;
            }

            totalWeightedHoldingTime += weightedHoldingTime;
            totalSoldQuantity += transaction.getOrderQuantity();
        }

        if (totalSoldQuantity == 0 && !buyTransactionsTillSale.isEmpty()) {
            LocalDate firstBoughtDate = buyTransactionsTillSale.getFirst().getDate();
            return getHoldingTimeInDays(firstBoughtDate, LocalDate.now());
        }

        return totalWeightedHoldingTime / totalSoldQuantity;
    }

    /**
     * @return the asset diversity percentage in the portfolio, range (0 - 100)%
     */
    public int getAssetDiversityPercentage(Asset asset) {
        return Math.toIntExact(Math.round(getAssetTotalWorth(asset) / getPortfolioWorth() * ONE_HUNDRED_PERCENT));
    }
    //endregion

    //region PORTFOLIO STATS
    private long getHoldingTimeInDays(CryptoTransaction buyTransaction, CryptoTransaction sellTransaction) {
        return getHoldingTimeInDays(buyTransaction.getDate(), sellTransaction.getDate());
    }

    private long getHoldingTimeInDays(LocalDate buyDate, LocalDate sellDate) {
        return ChronoUnit.DAYS.between(buyDate, sellDate);
    }

    /**
     * How much is estimated the worth of all holding assets (Overall Unrealized profit)
     */
    public double getPortfolioWorth() {
        return instrumentsFacadeService.getWalletBalances()
                .stream()
                .mapToDouble(balance -> balance.getAmount() * instrumentsFacadeService.getAssetMarketPrice(balance.getAsset()))
                .sum();
    }

    /**
     * How much was invested in all holding assets at this moment
     */
    public double getPortfolioCost() {
        return instrumentsFacadeService.getAllTransactions()
                .stream()
                .mapToDouble(t -> (t.isBuyTransaction() ? 1 : -1) * t.getOrderTotalCost())
                .sum();
    }

    public double getPortfolioProfit() {
        return getPortfolioWorth() - getPortfolioCost();
    }

    public double getPortfolioRealizedProfit() {
        return instrumentsFacadeService.getAllAssetsEverBought()
                .stream()
                .mapToDouble(this::getAssetRealizedProfit)
                .sum();
    }

    public double getPortfolioUnrealizedProfit() {
        return getPortfolioWorth() - getPortfolioCost();
    }

    public double getPortfolioAverageHoldingDays() {
        return instrumentsFacadeService.getAllAssetsEverBought()
                .stream()
                .mapToDouble(this::getAssetAverageHoldingDays)
                .average()
                .orElse(0);
    }

    public String getPortfolioBuySellRatio() {
        List<CryptoTransaction> transactions = instrumentsFacadeService.getAllTransactions();
        double buyCost = calculateTotalCostForBuyTransactions(transactions);
        double sellCost = calculateTotalCostForSellTransactions(transactions);
        double totalCost = buyCost + sellCost;

        double buyRatio = buyCost / totalCost * ONE_HUNDRED_PERCENT;
        double sellRatio = sellCost / totalCost * ONE_HUNDRED_PERCENT;

        return String.format("%d : %d", Math.round(buyRatio), Math.round(sellRatio));
    }

    public double getPortfolioProfitPercentage() {
        return getPortfolioWorth() / getPortfolioCost() * ONE_HUNDRED_PERCENT - ONE_HUNDRED_PERCENT;
    }
    //endregion

    //region CALCULATION METHODS
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
        return MathUtils.safeZeroDivision(totalCost, totalQuantity);
    }

    private double adjustCostForSale(CryptoTransaction transaction, double totalCost, double totalQuantity) {
        double quantitySold = transaction.getOrderQuantity();
        double averagePriceBeforeSale = calculateAveragePrice(totalCost, totalQuantity);
        return totalCost - (quantitySold * averagePriceBeforeSale);
    }
    //endregion

}
