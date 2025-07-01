package com.example.application.services.crypto;

import com.example.application.data.enums.SymbolIndentifier;
import com.example.application.data.models.InstrumentsProvider;
import com.example.application.entities.User;
import com.example.application.entities.crypto.*;
import com.example.application.repositories.crypto.AssetRepository;
import com.example.application.repositories.crypto.WalletBalanceRepository;
import com.example.application.utils.common.formatters.number.AmountFormatter;
import com.example.application.utils.exceptions.InvalidBalanceAmount;
import com.example.application.utils.fetchers.api_responses.AssetMetadata;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class InstrumentsService {

    private final InstrumentsProvider instrumentsProvider;
    private final WalletService walletService;
    private final AssetRepository assetRepository;
    private final AssetWatcherService assetWatcherService;
    private final CryptoTransactionService transactionService;
    private final WalletBalanceRepository walletBalanceRepository;

    @Autowired
    public InstrumentsService(InstrumentsProvider instrumentsProvider,
                              WalletService walletService,
                              AssetRepository assetRepository,
                              CryptoTransactionService transactionService,
                              AssetWatcherService assetWatcherService,
                              WalletBalanceRepository walletBalanceRepository
    ) {
        this.instrumentsProvider = instrumentsProvider;
        this.walletService = walletService;
        this.assetRepository = assetRepository;
        this.transactionService = transactionService;
        this.assetWatcherService = assetWatcherService;
        this.walletBalanceRepository = walletBalanceRepository;
    }

    /*
     * ASSETS
     * */

    public List<Asset> getAllAssets() {
        return assetRepository.findAll();
    }

    public Asset getAssetBySymbol(String symbolName) {
        return assetRepository.findBySymbol(symbolName.toUpperCase());
    }

    public Asset getAssetBySymbol(SymbolIndentifier symbol) {
        return assetRepository.findBySymbol(symbol.name());
    }

    public Asset saveAsset(Asset asset) {
        return assetRepository.save(asset);
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

    public List<AssetWatcher> getAssetWatchersByAsset(Wallet wallet, Asset asset) {
        return assetWatcherService.findBy(wallet, asset);
    }

    public List<AssetWatcher> getAssetWatchersByAssetAndActionType(Wallet wallet, Asset asset, AssetWatcher.ActionType actionType) {
        return assetWatcherService.findBy(wallet, asset, actionType);
    }

    /*
     * Transactions
     * */

    public CryptoTransaction saveTransaction(CryptoTransaction transaction) {
        return transactionService.saveTransaction(transaction);
    }

    public void deleteTransaction(CryptoTransaction transaction) {
        transactionService.deleteTransaction(transaction);
    }

    public List<CryptoTransaction> getTransactionsBy(Wallet wallet) {
        return transactionService.findBy(wallet);
    }

    public List<CryptoTransaction> getTransactionsBy(Wallet wallet, Asset asset) {
        return transactionService.findBy(wallet, asset);
    }

    public List<CryptoTransaction> getTransactionsBy(Wallet wallet, Asset asset, CryptoTransaction.TransactionType type) {
        return transactionService.findBy(wallet, asset, type);
    }

    /*
     * WALLETS
     * */

    public Wallet getWalletByUser(User user) {
        return walletService.getWalletByUser(user);
    }

    /*
     * WALLET BALANCES
     * */

    public WalletBalance getWalletBalancesByWalletAndAsset(Wallet wallet, Asset asset) {
        return walletBalanceRepository.findByWalletAndAsset(wallet, asset).orElseThrow();
    }

    public List<WalletBalance> getWalletBalancesByWallet(Wallet wallet) {
        return walletBalanceRepository.findByWallet(wallet);
    }

    /**
     * Updates wallet balance with an amount that should be added or removed using a positive/negative tokens amount
     * <p> Example: + 200 ARB
     */
    public WalletBalance fillWalletBalance(Wallet wallet, Asset asset, double tokensAmountToBeAdded) {
        Objects.requireNonNull(asset);
        WalletBalance walletBalance = getWalletBalancesByWalletAndAsset(wallet, asset);
        double balanceAfterSupply = calculateBalanceAfterSupply(walletBalance, tokensAmountToBeAdded);

        AmountFormatter amountFormatter = AmountFormatter.withDefaults();
        System.out.printf("Fill %s with %s. Left amount: %s\n", wallet,
                amountFormatter.format(tokensAmountToBeAdded, asset),
                amountFormatter.format(balanceAfterSupply, asset));

        if (balanceAfterSupply < 0) {
            throw new InvalidBalanceAmount("The balance amount cannot be negative.");
        }

        walletBalance.setAmount(balanceAfterSupply);
        walletBalanceRepository.save(walletBalance);
        return walletBalance;
    }

    public double calculateBalanceAfterSupply(WalletBalance walletBalance, double tokensAmountToBeAdded) {
        return walletBalance.getAmount() + tokensAmountToBeAdded;
    }

    // TODO: FIND A WAY TO EXTRACT THIS FROM DATABASE WITHOUT EXCEPTION
    public List<WalletBalance> getWalletBalancesByWalletWithNonZeroAmount(Wallet wallet) {
        return walletBalanceRepository.findByWalletWithNonZeroAmount(wallet.getId());
    }

    //<editor-fold desc="METADATA">
    @Nullable
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
        log.info("Updated database with {} assets", metadataMap.size());
    }

    private void updateAssetData(SymbolIndentifier indentifier, @Nullable AssetMetadata assetMetadata) {
        if (assetMetadata == null) {
            log.info("Received an empty asset metadata, skipping updating database");
            return;
        }

        Asset asset = Optional.ofNullable(assetRepository.findBySymbol(indentifier.name())).orElse(new Asset());
        asset.setSymbol(indentifier.name());
        asset.setFullName(indentifier.getFullName());
        Optional.ofNullable(assetMetadata.getPriceUsd()).ifPresent(asset::setMarketPrice);
        Optional.ofNullable(assetMetadata.getAssetDescription()).ifPresent(asset::setDescription);
        Optional.ofNullable(assetMetadata.getAssetDescriptionSummary()).ifPresent(asset::setSummaryDescription);
        Optional.ofNullable(assetMetadata.getSpotMoving24HourQuoteVolumeUsd()).ifPresent(asset::setTodayVolume);
        Optional.ofNullable(assetMetadata.getSpotMoving24HourChangePercentageUsd()).ifPresent(asset::setChangePercentage);
        Optional.ofNullable(assetMetadata.getSupplyCirculating()).ifPresent(asset::setCirculationSupply);
        Optional.ofNullable(assetMetadata.getSupplyTotal()).ifPresent(asset::setTotalSupply);
        Optional.ofNullable(assetMetadata.getTotalMktCapUsd()).ifPresent(asset::setTotalMarketCap);
        Optional.ofNullable(assetMetadata.getLogoUrl()).ifPresent(asset::setImageUrl);
        assetRepository.save(asset);
    }
    //</editor-fold>


}
