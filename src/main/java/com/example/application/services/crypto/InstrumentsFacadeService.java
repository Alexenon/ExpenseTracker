package com.example.application.services.crypto;

import com.example.application.data.enums.Symbols;
import com.example.application.data.models.InstrumentsProvider;
import com.example.application.entities.crypto.*;
import com.example.application.services.SecurityService;
import com.example.application.services.UserService;
import com.example.application.utils.fetchers.api_responses.AssetMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.List;

/*
    TODO: Rename
        - Assets -> AssetServiceFacade         UserAssetsService
        - Expenses -> ExpensesServiceFacade    UserExpensesService
*/

/**
 * Service that provides information just for authenticated user and guest user
 * and hides other information that user is not supposed to have
 */
@Service
public class InstrumentsFacadeService {

    private final SecurityService securityService;
    private final InstrumentsService instrumentsService;
    private final InstrumentsProvider instrumentsProvider;

    @Autowired
    public InstrumentsFacadeService(UserService userService,
                                    SecurityService securityService,
                                    InstrumentsService instrumentsService,
                                    InstrumentsProvider instrumentsProvider) {
        this.securityService = securityService;
        this.instrumentsService = instrumentsService;
        this.instrumentsProvider = instrumentsProvider;
    }

    //<editor-fold desc="ASSET">
    public List<Asset> getAllAssets() {
        return instrumentsService.getAllAssets();
    }

    public Asset getAssetBySymbol(String symbolName) {
        return instrumentsService.getAssetBySymbol(symbolName);
    }

    public Asset getAssetBySymbol(Symbols symbol) {
        return instrumentsService.getAssetBySymbol(symbol.name());
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

    public WalletBalance getWalletBalanceByAsset(Asset asset) {
        return instrumentsService.getWalletBalancesByWalletAndAsset(getAuthenticatedUserWallet(), asset);
    }

    public double getAmountOfTokens(Asset asset) {
        return instrumentsService.getWalletBalancesByWalletAndAsset(getAuthenticatedUserWallet(), asset).getAmount();
    }
    //</editor-fold>

    //<editor-fold desc="METADATA">
    public AssetMetadata getAssetMetadata(Asset asset) {
        return instrumentsProvider.getMetadata().get(asset);
    }

    public String getAssetFullName(Asset asset) {
        return instrumentsProvider.getMetadata().get(asset).getName();
    }

    public double getAssetPrice(Asset asset) {
        return instrumentsProvider.getMetadata().get(asset).getPriceUsd();
    }

    public String getAssetDescriptionSummary(Asset asset) {
        return instrumentsProvider.getMetadata().get(asset).getAssetDescriptionSummary();
    }

    public double getAssetTotalMarketCap(Asset asset) {
        return instrumentsProvider.getMetadata().get(asset).getTotalMktCapUsd();
    }

    public BigInteger getAssetSupplyTotal(Asset asset) {
        return instrumentsProvider.getMetadata().get(asset).getSupplyTotal();
    }

    public BigInteger getAssetSupplyCirculating(Asset asset) {
        return instrumentsProvider.getMetadata().get(asset).getSupplyCirculating();
    }

    public double getAsset24HourChangePercentage(Asset asset) {
        return instrumentsProvider.getMetadata().get(asset).getSpotMoving24HourChangePercentageUsd();
    }

    public double getAsset24HourVolume(Asset asset) {
        return instrumentsProvider.getMetadata().get(asset).getSpotMoving24HourQuoteVolumeUsd();
    }

    public String getAssetImgUrl(Asset asset) {
        return instrumentsProvider.getMetadata().get(asset).getLogoUrl();
    }

    public void updateAssetMetadata() {
        instrumentsProvider.getUpdatedMetadata();
    }

    //</editor-fold>

    private Wallet getAuthenticatedUserWallet() {
        return instrumentsService.getWalletByUser(securityService.getAuthenticatedUser());
    }

}
