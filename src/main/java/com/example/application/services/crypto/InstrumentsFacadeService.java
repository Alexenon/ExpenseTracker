package com.example.application.services.crypto;

import com.example.application.data.enums.SymbolIndentifier;
import com.example.application.data.models.InstrumentsProvider;
import com.example.application.entities.crypto.*;
import com.example.application.services.SecurityService;
import com.example.application.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
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
@SuppressWarnings("CallToPrintStackTrace")
@Slf4j
@Service
public class InstrumentsFacadeService {

    private final SecurityService securityService;
    private final InstrumentsService instrumentsService;

    @Autowired
    public InstrumentsFacadeService(UserService userService,
                                    SecurityService securityService,
                                    InstrumentsService instrumentsService,
                                    InstrumentsProvider instrumentsProvider) {
        this.securityService = securityService;
        this.instrumentsService = instrumentsService;
    }

    //<editor-fold desc="ASSET">
    public List<Asset> getAllAssets() {
        return instrumentsService.getAllAssets();
    }

    public Asset getAssetBySymbol(String symbolName) {
        return instrumentsService.getAssetBySymbol(symbolName);
    }

    public boolean saveAssetNote(Asset asset, String note) {
        try {
            log.info("Saving note for asset {}, note: '{}'", asset, note);
// SWITCH            asset.setComment(note);
            return instrumentsService.saveAsset(asset) != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Asset getAssetBySymbol(SymbolIndentifier symbol) {
        return instrumentsService.getAssetBySymbol(symbol.name());
    }

    public double getAmountOfTokens(Asset asset) {
        return asset == null ? 0 : getWalletBalanceByAsset(asset).getAmount();
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

    public WalletBalance getWalletBalanceByAsset(Asset asset) {
        return instrumentsService.getWalletBalancesByWalletAndAsset(getAuthenticatedUserWallet(), asset);
    }

    public WalletBalance fillWalletBalance(Asset asset, double amountToBeAdded) {
        return instrumentsService.fillWalletBalance(getAuthenticatedUserWallet(), asset, amountToBeAdded);
    }
    //</editor-fold>

    private Wallet getAuthenticatedUserWallet() {
        return instrumentsService.getWalletByUser(securityService.getAuthenticatedUser());
    }

    public void updateAssetData() {
        instrumentsService.updateAssetData();
    }

}
