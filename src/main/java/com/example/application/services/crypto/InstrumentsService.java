package com.example.application.services.crypto;

import com.example.application.data.enums.Symbols;
import com.example.application.entities.User;
import com.example.application.entities.crypto.*;
import com.example.application.repositories.crypto.AssetRepository;
import com.example.application.repositories.crypto.WalletBalanceRepository;
import com.example.application.utils.common.number.AmountFormatter;
import com.example.application.utils.exceptions.InvalidBalanceAmount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class InstrumentsService {

    private final WalletService walletService;
    private final AssetRepository assetRepository;
    private final AssetWatcherService assetWatcherService;
    private final CryptoTransactionService transactionService;
    private final WalletBalanceRepository walletBalanceRepository;

    @Autowired
    public InstrumentsService(WalletService walletService,
                              AssetRepository assetRepository,
                              CryptoTransactionService transactionService,
                              AssetWatcherService assetWatcherService,
                              WalletBalanceRepository walletBalanceRepository
    ) {
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

    public Asset getAssetBySymbol(Symbols symbol) {
        return assetRepository.findBySymbol(symbol.name());
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
        double balanceAfterSupply = getWalletBalanceAfterSupply(walletBalance, tokensAmountToBeAdded);

        AmountFormatter amountFormatter = AmountFormatter.withDefaults();

        System.out.printf("Fill %s with %s. Left amount: %s\n", wallet,
                amountFormatter.format(tokensAmountToBeAdded, asset.getSymbol()),
                amountFormatter.format(balanceAfterSupply, asset.getSymbol()));

        if (balanceAfterSupply < 0) {
            throw new InvalidBalanceAmount("The balance amount cannot be negative.");
        }

        walletBalance.setAmount(balanceAfterSupply);
        walletBalanceRepository.save(walletBalance);
        return walletBalance;
    }

    public double getWalletBalanceAfterSupply(WalletBalance walletBalance, double tokensAmountToBeAdded) {
        return walletBalance.getAmount() + tokensAmountToBeAdded;
    }

    // TODO: FIND A WAY TO EXTRACT THIS FROM DATABASE WITHOUT EXCEPTION
    public List<WalletBalance> getWalletBalancesByWalletWithNonZeroAmount(Wallet wallet) {
        return walletBalanceRepository.findByWalletWithNonZeroAmount(wallet.getId());
    }

    /*
     * OTHERS
     * */

    public void updateDatabase() {
        Arrays.stream(Symbols.values()).forEach(asset -> {
            if (assetRepository.findBySymbol(asset.name()) == null) {
                assetRepository.save(new Asset(asset.name(), asset.getFullName()));
            }
        });

        System.out.println("Filled database with " + Symbols.values().length + " assets");
    }

}
