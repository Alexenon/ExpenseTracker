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

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/*
    TODO: Rename
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

    public WalletBalance updateAssetComment(Asset asset, String comment) {
        WalletBalance walletBalanceByAsset = getWalletBalanceByAsset(asset);
        walletBalanceByAsset.setComment(comment);
        return instrumentsService.saveWalletBalance(walletBalanceByAsset);
    }

    public WalletBalance markAssetAsFavorite(Asset asset, boolean isFavorite) {
        WalletBalance walletBalanceByAsset = getWalletBalanceByAsset(asset);
        walletBalanceByAsset.setMarkedAsFavorite(isFavorite);
        return instrumentsService.saveWalletBalance(walletBalanceByAsset);
    }

    public double getAmountOfTokens(Asset asset) {
        return Optional.ofNullable(asset)
                .map(this::getWalletBalanceByAsset)
                .map(WalletBalance::getAmount)
                .orElse(Double.NaN);
    }

    public List<Asset> getAssetsWithNonZeroAmount() {
        return getWalletBalances()
                .stream()
                .filter(wb -> wb.getAmount() > 0)
                .map(WalletBalance::getAsset)
                .collect(Collectors.toList());
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
        return instrumentsService.getTransactionsBy(getAuthenticatedUserWallet());
    }

    public List<CryptoTransaction> getTransactionsByAsset(Asset asset) {
        return instrumentsService.getTransactionsBy(getAuthenticatedUserWallet(), asset);
    }

    public CryptoTransaction saveTransaction(CryptoTransaction transaction) {
        transaction.setWallet(getAuthenticatedUserWallet());
        return instrumentsService.saveTransaction(transaction);
    }

    public void deleteTransaction(CryptoTransaction transaction) {
        instrumentsService.deleteTransaction(transaction);
    }
    //</editor-fold>

    //<editor-fold desc="ASSET WATCHERS">
    public AssetWatcher saveAssetWatcher(AssetWatcher assetWatcher) {
        assetWatcher.setWallet(getAuthenticatedUserWallet());
        return instrumentsService.saveAssetWatcher(assetWatcher);
    }

    public void deleteAssetWatcher(AssetWatcher assetWatcher) {
        instrumentsService.deleteAssetWatcher(assetWatcher);
    }

    public List<AssetWatcher> getAssetWatchersByAsset(Asset asset) {
        return instrumentsService.getAssetWatchersByAsset(getAuthenticatedUserWallet(), asset);
    }

    public List<AssetWatcher> getAssetWatchersByAssetAndActionType(Asset asset, AssetWatcher.ActionType actionType) {
        return instrumentsService.getAssetWatchersByAssetAndActionType(getAuthenticatedUserWallet(), asset, actionType);
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

    //<editor-fold desc="WALLET BALANCES">
    public List<WalletBalance> getWalletBalances() {
        return instrumentsService.getWalletBalancesByWallet(getAuthenticatedUserWallet());
    }

    @NotNull
    public WalletBalance getWalletBalanceByAsset(Asset asset) {
        return instrumentsService.getWalletBalancesByWalletAndAsset(getAuthenticatedUserWallet(), asset);
    }
    //</editor-fold>

    private Wallet getAuthenticatedUserWallet() {
        return instrumentsService.getWalletByUser(securityService.getAuthenticatedUser());
    }

    public void updateAssetData() {
        instrumentsService.updateAssetData();
    }

}
