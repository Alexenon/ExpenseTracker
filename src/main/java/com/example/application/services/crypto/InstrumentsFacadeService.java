package com.example.application.services.crypto;

import com.example.application.data.enums.Symbols;
import com.example.application.data.models.InstrumentsProvider;
import com.example.application.data.models.crypto.CryptoTransaction;
import com.example.application.data.models.crypto.Wallet;
import com.example.application.data.models.crypto.WalletBalance;
import com.example.application.entities.User;
import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.AssetWatcher;
import com.example.application.services.SecurityService;
import com.example.application.services.UserService;
import com.example.application.utils.fetchers.api_responses.AssetMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service that provides information just for authenticated user and guest user
 * and hides other information that user is not supposed to have
 */
@Service
public class InstrumentsFacadeService {

    private final UserService userService;
    private final SecurityService securityService;
    private final InstrumentsService instrumentsService;
    private final InstrumentsProvider instrumentsProvider;

    @Autowired
    public InstrumentsFacadeService(UserService userService,
                                    SecurityService securityService,
                                    InstrumentsService instrumentsService,
                                    InstrumentsProvider instrumentsProvider) {
        this.userService = userService;
        this.securityService = securityService;
        this.instrumentsService = instrumentsService;
        this.instrumentsProvider = instrumentsProvider;
    }

    //<editor-fold desc="ASSET">
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

    public List<CryptoTransaction> getTransactionsByAssetAndType(Asset asset, CryptoTransaction.TransactionType type) {
        return instrumentsService.getTransactionsBy(getAuthenticatedUserWallet(), asset, type);
    }

    public CryptoTransaction saveTransaction(CryptoTransaction transaction) {
        return instrumentsService.saveTransaction(transaction);
    }

    public void deleteTransaction(CryptoTransaction transaction) {
        instrumentsService.deleteTransaction(transaction);
    }
    //</editor-fold>

    //<editor-fold desc="ASSET WATCHERS">
    public AssetWatcher saveAssetWatcher(AssetWatcher assetWatcher) {
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
    //</editor-fold>

    //<editor-fold desc="WALLET BALANCES">
    public List<WalletBalance> getWalletBalances() {
        return instrumentsService.getWalletBalancesByWallet(getAuthenticatedUserWallet());
    }

    public WalletBalance getWalletBalanceByAsset(Asset asset) {
        return instrumentsService.getWalletBalancesByWalletAndAsset(getAuthenticatedUserWallet(), asset);
    }
    //</editor-fold>

    public AssetMetadata getAssetMetadata(Asset asset) {
        return instrumentsProvider.getMetadata().get(asset);
    }


    private Wallet getAuthenticatedUserWallet() {
        String username = securityService.getAuthenticatedUser().getUsername();
        User user = userService.findByUsernameIgnoreCase(username).orElseThrow();
        return instrumentsService.getWalletByUser(user);
    }


}
