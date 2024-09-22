package com.example.application.repositories.crypto;

import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.CryptoTransaction;
import com.example.application.entities.crypto.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CryptoTransactionRepository extends JpaRepository<CryptoTransaction, Long> {

    List<CryptoTransaction> findByWallet(Wallet wallet);

    List<CryptoTransaction> findByWalletAndAsset(Wallet wallet, Asset asset);

    List<CryptoTransaction> findByWalletAndAssetAndType(Wallet wallet, Asset asset, CryptoTransaction.TransactionType type);

}
