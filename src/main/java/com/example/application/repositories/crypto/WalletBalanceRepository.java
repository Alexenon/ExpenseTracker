package com.example.application.repositories.crypto;

import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.Wallet;
import com.example.application.entities.crypto.WalletBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WalletBalanceRepository extends JpaRepository<WalletBalance, Long> {

    List<WalletBalance> findByWallet(Wallet wallet);

    Optional<WalletBalance> findByWalletAndAsset(Wallet wallet, Asset asset);



}
