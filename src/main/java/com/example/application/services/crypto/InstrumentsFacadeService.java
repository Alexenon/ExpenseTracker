package com.example.application.services.crypto;

import com.example.application.data.models.InstrumentsProvider;
import com.example.application.entities.User;
import com.example.application.entities.crypto.*;
import com.example.application.services.SecurityService;
import com.example.application.services.UserService;
import com.example.application.utils.exceptions.InternalUnexpectedException;
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

    public Optional<Asset> getAssetBySymbol(@NotNull String symbolName) {
        return instrumentsService.getAssetBySymbol(symbolName);
    }

    public double getAmountOfTokens(Portfolio portfolio, Asset asset) {
        return Optional.ofNullable(asset)
                .flatMap(a -> getAssetBalanceByAsset(portfolio, asset))
                .map(AssetBalance::getAmount)
                .orElse(Double.NaN);
    }

    @Nullable
    public String getAssetComment(Asset asset) {
        return instrumentsService.getAssetComment(getAuthenticatedUser(), asset);
    }

    public boolean isAssetMarkedAsFavorite(Asset asset) {
        return instrumentsService.isAssetMarkedAsFavorite(getAuthenticatedUser(), asset);
    }

    public void updateAssetComment(Asset asset, @Nullable String comment) {
        instrumentsService.updateAssetComment(getAuthenticatedUser(), asset, comment);
    }

    public void updateMarkAssetAsFavorite(Asset asset, boolean markedAsFavorite) {
        instrumentsService.updateMarkAssetAsFavorite(getAuthenticatedUser(), asset, markedAsFavorite);
    }

    public List<AssetBalance> getAssetsWithNonZeroAmount(Portfolio portfolio) {
        return instrumentsService.getAssetsWithNonZeroAmount(portfolio);
    }

    public List<Asset> getAllAssetsEverBought(Portfolio portfolio) {
        return getTransactions(portfolio)
                .stream()
                .filter(Transaction::isBuyTransaction)
                .map(Transaction::getAsset)
                .distinct()
                .toList();
    }
    //</editor-fold>

    //<editor-fold desc="TRANSACTIONS">
    public List<Transaction> getTransactions(Portfolio portfolio) {
        return instrumentsService.getTransactionsBy(portfolio);
    }

    public List<Transaction> getTransactions(Portfolio portfolio, LocalDate from) {
        return getTransactions(portfolio, from, LocalDate.now());
    }

    public List<Transaction> getTransactions(Portfolio portfolio, LocalDate from, LocalDate to) {
        return instrumentsService.getTransactionsBy(portfolio, from, to);
    }

    public List<Transaction> getTransactionsByAsset(Portfolio portfolio, Asset asset) {
        return instrumentsService.getTransactionsBy(portfolio, asset);
    }

    public Transaction saveTransaction(Transaction transaction) {
        return instrumentsService.saveTransaction(transaction);
    }

    public void deleteTransaction(Transaction transaction) {
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

    public List<AssetWatcher> getAssetWatchersByAsset(Portfolio portfolio, Asset asset) {
        return instrumentsService.getAssetWatchersByAsset(portfolio, asset);
    }

    public List<AssetWatcher> getAssetWatchersByAssetAndActionType(Portfolio portfolio, Asset asset, AssetWatcher.ActionType actionType) {
        return instrumentsService.getAssetWatchersByAssetAndActionType(portfolio, asset, actionType);
    }

    public double getClosestBuyWatcherPrice(Portfolio portfolio, Asset asset) {
        return getAssetWatchersByAssetAndActionType(portfolio, asset, AssetWatcher.ActionType.BUY)
                .stream()
                .filter(assetWatcher -> !assetWatcher.isCompleted())
                .map(AssetWatcher::getTarget)
                .min(Comparator.naturalOrder())
                .orElse(0.0);
    }

    public double getClosestSellWatcherPrice(Portfolio portfolio, Asset asset) {
        return getAssetWatchersByAssetAndActionType(portfolio, asset, AssetWatcher.ActionType.SELL)
                .stream()
                .filter(assetWatcher -> !assetWatcher.isCompleted())
                .map(AssetWatcher::getTarget)
                .max(Comparator.naturalOrder())
                .orElse(0.0);
    }
    //</editor-fold>

    //<editor-fold desc="PORTFOLIO BALANCES">
    public List<AssetBalance> getAssetBalances(Portfolio portfolio) {
        return instrumentsService.getAssetBalancesByPortfolio(portfolio);
    }

    public List<AssetBalance> getAssetBalancesForPortfolios(List<Portfolio> portfolios) {
        return instrumentsService.getAssetBalancesByPortfolios(portfolios);
    }

    public Optional<AssetBalance> getAssetBalanceByAsset(Portfolio portfolio, Asset asset) {
        return instrumentsService.getAssetBalancesByPortfolioAndAsset(portfolio, asset);
    }

    public List<AssetBalance> getAssetBalancesByAsset(List<Portfolio> portfolios, Asset asset) {
        return instrumentsService.getAssetBalancesByPortfoliosAndAsset(portfolios, asset);
    }
    //</editor-fold>

    public Portfolio createPortfolio(String name) {
        return instrumentsService.createNewPortfolio(name, getAuthenticatedUser());
    }

    // TODO: [URGENT] -> FILTER BY MAIN PORTFOLIO
    // TODO: REMOVE ME, SO YOU WILL NOT MISS NOTHING DURING PROPERLY DEVELOPING THIS IMPL
    // THIS METHOD SHOULD BE IN THE VIEW ITSELF NOT HERE
    @NotNull
    public Portfolio getAuthenticatedUserMainPortfolio() {
        User user = getAuthenticatedUser();
        return instrumentsService.getPortfoliosByUser(user)
                .stream()
                .findFirst()
                .orElseThrow(() -> new InternalUnexpectedException("User '%s' doesn't have any portfolios"
                        .formatted(user.getUsername())));
    }

    public List<Portfolio> getUserPortfolios() {
        return instrumentsService.getPortfoliosByUser(getAuthenticatedUser());
    }

    @NotNull
    public User getAuthenticatedUser() {
        return securityService.getAuthenticatedUser();
    }

    public void updateAssetData() {
        instrumentsService.updateAssetData();
    }

}
