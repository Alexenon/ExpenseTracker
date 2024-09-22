package com.example.application.repositories.crypto;

import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.CryptoTransaction;
import com.example.application.entities.crypto.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CryptoTransactionRepository extends JpaRepository<CryptoTransaction, Long> {

    List<CryptoTransaction> findBy(Wallet wallet);

    List<CryptoTransaction> findBy(Asset asset);

    List<CryptoTransaction> findBy(CryptoTransaction.TransactionType type);

    List<CryptoTransaction> findBy(Wallet wallet, Asset asset);

    List<CryptoTransaction> findBy(Wallet wallet, Asset asset, CryptoTransaction.TransactionType type);

}
