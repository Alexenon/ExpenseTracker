package com.example.application.services.crypto;

import com.example.application.data.models.InstrumentsProvider;
import com.example.application.entities.crypto.*;
import com.example.application.services.SecurityService;
import com.example.application.services.UserService;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/*
    REFACTOR: Rename
        - Assets -> AssetServiceFacade         UserAssetsService
        - Expenses -> ExpensesServiceFacade    UserExpensesService
*/

/**
 * Service that provides information just for authenticated user and guest user
 * and hides other information that user is not supposed to have
 */
@Slf4j
@Service
public class InstrumentsFacadeService {

    private final SecurityService securityService;
    private final InstrumentsService instrumentsService;

    @Autowired
    public InstrumentsFacadeService(UserService userService,
                                    SecurityService securityService,
                                    InstrumentsService instrumentsService,
                                    InstrumentsProvider instrumentsProvider)
    {
        this.securityService = securityService;
        this.instrumentsService = instrumentsService;
    }

    //<editor-fold desc="ASSET">
    public List<Asset> getAllAssets() {
        return instrumentsService.getAllAssets();
    }

    public Optional<Asset> getAssetBySymbol(@Nullable String symbolName) {
        return instrumentsService.getAssetBySymbol(symbolName);
    }

    public AssetBalance updateAssetComment(Asset asset, String comment) {
        AssetBalance assetBalanceByAsset = getAssetBalanceByAsset(asset);
        assetBalanceByAsset.setComment(comment);
        return instrumentsService.saveAssetBalance(assetBalanceByAsset);
    }

    public AssetBalance markAssetAsFavorite(Asset asset, boolean isFavorite) {
        AssetBalance assetBalanceByAsset = getAssetBalanceByAsset(asset);
        assetBalanceByAsset.setMarkedAsFavorite(isFavorite);
        return instrumentsService.saveAssetBalance(assetBalanceByAsset);
    }

    public double getAmountOfTokens(Asset asset) {
        return Optional.ofNullable(asset)
                .map(this::getAssetBalanceByAsset)
                .map(AssetBalance::getAmount)
                .orElse(Double.NaN);
    }

    public List<AssetBalance> getAssetsWithNonZeroAmount() {
        return instrumentsService.getAssetsWithNonZeroAmount(getAuthenticatedUserPortfolio());
    }

    public List<Asset> getAllAssetsEverBought() {
        return getAllTransactions()
                .stream()
                .filter(CryptoTransaction::isBuyTransaction)
                .map(CryptoTransaction::getAsset)
                .distinct()
                .toList();
    }

    //</editor-fold>

    //<editor-fold desc="TRANSACTIONS">
    public List<CryptoTransaction> getAllTransactions() {
        return instrumentsService.getTransactionsBy(getAuthenticatedUserPortfolio());
    }

    public List<CryptoTransaction> getTransactionsByAsset(Asset asset) {
        return instrumentsService.getTransactionsBy(getAuthenticatedUserPortfolio(), asset);
    }

    public List<CryptoTransaction> getTransactions(LocalDate from) {
        return getTransactions(from, LocalDate.now());
    }

    public List<CryptoTransaction> getTransactions(LocalDate from, LocalDate to) {
        return instrumentsService.getTransactionsBy(getAuthenticatedUserPortfolio(), from, to);
    }

    public CryptoTransaction saveTransaction(CryptoTransaction transaction) {
        transaction.setPortfolio(getAuthenticatedUserPortfolio());
        return instrumentsService.saveTransaction(transaction);
    }

    public void deleteTransaction(CryptoTransaction transaction) {
        instrumentsService.deleteTransaction(transaction);
    }
    //</editor-fold>

    //<editor-fold desc="ASSET WATCHERS">
    public AssetWatcher saveAssetWatcher(AssetWatcher assetWatcher) {
        assetWatcher.setPortfolio(getAuthenticatedUserPortfolio());
        return instrumentsService.saveAssetWatcher(assetWatcher);
    }

    public void deleteAssetWatcher(AssetWatcher assetWatcher) {
        instrumentsService.deleteAssetWatcher(assetWatcher);
    }

    public List<AssetWatcher> getAssetWatchersByAsset(Asset asset) {
        return instrumentsService.getAssetWatchersByAsset(getAuthenticatedUserPortfolio(), asset);
    }

    public List<AssetWatcher> getAssetWatchersByAssetAndActionType(Asset asset, AssetWatcher.ActionType actionType) {
        return instrumentsService.getAssetWatchersByAssetAndActionType(getAuthenticatedUserPortfolio(), asset, actionType);
    }

    public double getClosestBuyWatcherPrice(Asset asset) {
        return getAssetWatchersByAssetAndActionType(asset, AssetWatcher.ActionType.BUY)
                .stream()
                .filter(assetWatcher -> !assetWatcher.isCompleted())
                .map(AssetWatcher::getTarget)
                .min(Comparator.naturalOrder())
                .orElse(0.0);
    }

    public double getClosestSellWatcherPrice(Asset asset) {
        return getAssetWatchersByAssetAndActionType(asset, AssetWatcher.ActionType.SELL)
                .stream()
                .filter(assetWatcher -> !assetWatcher.isCompleted())
                .map(AssetWatcher::getTarget)
                .max(Comparator.naturalOrder())
                .orElse(0.0);
    }

    //</editor-fold>

    //<editor-fold desc="PORTFOLIO BALANCES">
    public List<AssetBalance> getAssetBalances() {
        return instrumentsService.getAssetBalancesByPortfolio(getAuthenticatedUserPortfolio());
    }

    @NotNull
    public AssetBalance getAssetBalanceByAsset(Asset asset) {
        return instrumentsService.getAssetBalancesByPortfolioAndAsset(getAuthenticatedUserPortfolio(), asset);
    }
    //</editor-fold>

    private Portfolio getAuthenticatedUserPortfolio() {
        return instrumentsService.getPortfolioByUser(securityService.getAuthenticatedUser());
    }

    public void updateAssetData() {
        instrumentsService.updateAssetData();
    }

}
