package com.example.application.services.crypto;

import com.example.application.data.enums.SymbolIndentifier;
import com.example.application.data.models.InstrumentsProvider;
import com.example.application.entities.User;
import com.example.application.entities.common.TransactionType;
import com.example.application.entities.crypto.*;
import com.example.application.utils.fetchers.api_responses.AssetMetadata;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class InstrumentsService {

    private final InstrumentsProvider instrumentsProvider;
    private final PortfolioService portfolioService;
    private final AssetService assetService;
    private final AssetWatcherService assetWatcherService;
    private final CryptoTransactionService transactionService;
    private final AssetBalanceService assetBalanceService;

    @Autowired
    public InstrumentsService(InstrumentsProvider instrumentsProvider,
                              PortfolioService portfolioService,
                              AssetService assetService,
                              CryptoTransactionService transactionService,
                              AssetWatcherService assetWatcherService,
                              AssetBalanceService assetBalanceService)
    {
        this.instrumentsProvider = instrumentsProvider;
        this.portfolioService = portfolioService;
        this.assetService = assetService;
        this.transactionService = transactionService;
        this.assetWatcherService = assetWatcherService;
        this.assetBalanceService = assetBalanceService;
    }

    /*
     * ASSETS
     * */

    public List<Asset> getAllAssets() {
        return assetService.findAll();
    }

    public Optional<Asset> getAssetBySymbol(String symbolName) {
        return assetService.findBySymbol(symbolName);
    }

    public Asset saveAsset(Asset asset) {
        return assetService.save(asset);
    }

    /*
     * AssetWatcher
     * */

    public AssetWatcher saveAssetWatcher(AssetWatcher assetWatcher) {
        return assetWatcherService.save(assetWatcher);
    }

    public void deleteAssetWatcher(AssetWatcher assetWatcher) {
        assetWatcherService.delete(assetWatcher);
    }

    public List<AssetWatcher> getAssetWatchersByAsset(Asset asset) {
        return assetWatcherService.findBy(asset);
    }

    public List<AssetWatcher> getAssetWatchersByAsset(Portfolio portfolio, Asset asset) {
        return assetWatcherService.findBy(portfolio, asset);
    }

    public List<AssetWatcher> getAssetWatchersByAssetAndActionType(Portfolio portfolio, Asset asset, AssetWatcher.ActionType actionType) {
        return assetWatcherService.findBy(portfolio, asset, actionType);
    }

    /*
     * Transactions
     * */

    public CryptoTransaction saveTransaction(CryptoTransaction transaction) {
        return transactionService.save(transaction);
    }

    public void deleteTransaction(CryptoTransaction transaction) {
        transactionService.delete(transaction);
    }

    public List<CryptoTransaction> getTransactionsBy(Portfolio portfolio) {
        return transactionService.findBy(portfolio);
    }

    public List<CryptoTransaction> getTransactionsBy(Portfolio portfolio, Asset asset) {
        return transactionService.findBy(portfolio, asset);
    }

    public List<CryptoTransaction> getTransactionsBy(Portfolio portfolio, Asset asset, TransactionType type) {
        return transactionService.findBy(portfolio, asset, type);
    }

    public List<CryptoTransaction> getTransactionsBy(Portfolio portfolio, LocalDate from, LocalDate to) {
        return transactionService.findBy(portfolio, from, to);
    }

    public List<AssetBalance> getAssetsWithNonZeroAmount(@NotNull Portfolio portfolio) {
        return assetBalanceService.getAssetBalancesByPortfolioWithNonZeroAmount(portfolio);
    }

    /*
     * PORTFOLIOS
     * */

    public Portfolio getPortfolioByUser(User user) {
        return portfolioService.getPortfolioByUser(user);
    }

    /*
     * PORTFOLIO BALANCES
     * */
    public AssetBalance saveAssetBalance(@NotNull AssetBalance assetBalance) {
        return assetBalanceService.save(assetBalance);
    }

    @NotNull
    public AssetBalance getAssetBalancesByPortfolioAndAsset(@NotNull Portfolio portfolio, @NotNull Asset asset) {
        return assetBalanceService.getByPortfolioAndAsset(portfolio, asset);
    }

    @NotNull
    public List<AssetBalance> getAssetBalancesByPortfolio(@NotNull Portfolio portfolio) {
        return assetBalanceService.getByPortfolio(portfolio);
    }

    //<editor-fold desc="METADATA">
    @NotNull
    public AssetMetadata getAssetMetadata(@NotNull String symbol) {
        Objects.requireNonNull(symbol, "symbol");
        return instrumentsProvider.getMetadata().get(symbol);
    }

    public void updateAssetData() {
        Map<String, AssetMetadata> metadataMap = instrumentsProvider.getUpdatedMetadata();

        if (metadataMap == null || metadataMap.isEmpty()) {
            log.info("Metadata is empty. Skipping updating the database");
            return;
        }

        metadataMap.forEach((key, value) -> updateAssetData(SymbolIndentifier.valueOf(key), value));
        log.info("Updated database for {} assets", metadataMap.size());
    }

    private void updateAssetData(SymbolIndentifier indentifier, @Nullable AssetMetadata assetMetadata) {
        if (assetMetadata == null) {
            log.info("Asset metadata for {} asset is null, skipping updating database", indentifier.name());
            return;
        }

        Asset asset = getAssetBySymbol(indentifier.name()).orElse(new Asset());
        asset.setSymbol(indentifier.name());
        asset.setFullName(indentifier.getFullName());
        Optional.ofNullable(assetMetadata.getPriceUsd()).ifPresent(asset::setMarketPrice);
        Optional.ofNullable(assetMetadata.getAssetDescriptionSummary()).ifPresent(asset::setSummaryDescription);
        Optional.ofNullable(assetMetadata.getSpotMoving24HourQuoteVolumeUsd()).ifPresent(asset::setTodayVolume);
        Optional.ofNullable(assetMetadata.getSpotMoving24HourChangePercentageUsd()).ifPresent(asset::setChangePercentage);
        Optional.ofNullable(assetMetadata.getSupplyCirculating()).ifPresent(asset::setCirculationSupply);
        Optional.ofNullable(assetMetadata.getSupplyTotal()).ifPresent(asset::setTotalSupply);
        Optional.ofNullable(assetMetadata.getTotalMktCapUsd()).ifPresent(asset::setTotalMarketCap);
        Optional.ofNullable(assetMetadata.getLogoUrl()).ifPresent(asset::setImageUrl);

        saveAsset(asset);
    }
//</editor-fold>


}
