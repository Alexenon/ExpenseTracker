package com.example.application.services.crypto;

import com.example.application.entities.User;
import com.example.application.entities.crypto.Wallet;
import com.example.application.entities.crypto.WalletBalance;
import com.example.application.repositories.crypto.AssetRepository;
import com.example.application.repositories.crypto.WalletBalanceRepository;
import com.example.application.repositories.crypto.WalletRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class WalletService {

    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private WalletBalanceRepository walletBalanceRepository;

    /**
     * Creates and attach a new wallet to the provided user.
     * */
    @NotNull
    @Transactional
    public Wallet createWallet(@NotNull User user) {
        Objects.requireNonNull(user, "user");
        Wallet wallet = new Wallet();
        wallet.setUser(user);
        Wallet savedWallet = walletRepository.save(wallet);

        // TODO: Instead of creating bunch of Database columns with zeroes values, it shouldn't be created,
        //  only a transaction was added
        //
        // Creating new Wallet Balance for each asset with value 0.0
        assetRepository.findAll().forEach(asset -> {
            WalletBalance walletBalance = new WalletBalance();
            walletBalance.setWallet(savedWallet);
            walletBalance.setAsset(asset);
            walletBalanceRepository.save(walletBalance);
        });

        return savedWallet;
    }

    @NotNull
    public Wallet getWalletByUser(@NotNull User user) {
        Objects.requireNonNull(user, "user");
        return Objects.requireNonNull(walletRepository.findByUser(user), "Cannot find wallet for %s".formatted(user));
    }

}
