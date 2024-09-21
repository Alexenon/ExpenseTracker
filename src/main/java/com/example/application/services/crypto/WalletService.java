package com.example.application.services.crypto;

import com.example.application.data.models.crypto.Wallet;
import com.example.application.data.models.crypto.WalletBalance;
import com.example.application.entities.User;
import com.example.application.repositories.crypto.AssetRepository;
import com.example.application.repositories.crypto.WalletBalanceRepository;
import com.example.application.repositories.crypto.WalletRepository;
import org.springframework.stereotype.Service;

@Service
public class WalletService {

    private final AssetRepository assetRepository;
    private final WalletRepository walletRepository;
    private final WalletBalanceRepository walletBalanceRepository;

    public WalletService(AssetRepository assetRepository, WalletRepository walletRepository, WalletBalanceRepository walletBalanceRepository) {
        this.assetRepository = assetRepository;
        this.walletRepository = walletRepository;
        this.walletBalanceRepository = walletBalanceRepository;
    }

    public Wallet saveWallet(Wallet wallet) {
        Wallet savedWallet = walletRepository.save(wallet);

        // Creating new Wallet Balance for each asset with value 0.0
        assetRepository.findAll().forEach(asset -> {
            WalletBalance walletBalance = new WalletBalance();
            walletBalance.setWallet(savedWallet);
            walletBalance.setAsset(asset);
            walletBalanceRepository.save(walletBalance);
        });

        return savedWallet;
    }

    public Wallet getWalletByUser(User user) {
        return walletRepository.findByUser(user);
    }

}
