package com.example.application.repositories.crypto;

import com.example.application.data.models.crypto.CryptoTransaction;
import com.example.application.entities.crypto.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CryptoTransactionRepository extends JpaRepository<CryptoTransaction, Long> {

    List<CryptoTransaction> findByAsset(Asset asset);

    List<CryptoTransaction> findByType(CryptoTransaction.TransactionType type);

    List<CryptoTransaction> findByAssetAndType(Asset asset, CryptoTransaction.TransactionType type);


}
