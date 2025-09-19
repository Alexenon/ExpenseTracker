package com.example.application.repositories.crypto;

import com.example.application.entities.common.TransactionType;
import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.CryptoTransaction;
import com.example.application.entities.crypto.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CryptoTransactionRepository extends JpaRepository<CryptoTransaction, Long> {

    List<CryptoTransaction> findByWallet(Wallet wallet);

    List<CryptoTransaction> findByWalletAndAsset(Wallet wallet, Asset asset);

    List<CryptoTransaction> findByWalletAndAssetAndType(Wallet wallet, Asset asset, TransactionType type);

    @Query("""
                SELECT t FROM crypto_transactions t
                WHERE t.wallet = :wallet
                  AND t.dateTime >= :fromDateTime
                  AND t.dateTime < :toDateTime
            """)
    List<CryptoTransaction> findByWalletAndDateTimeBetween(
            @Param("wallet") Wallet wallet,
            @Param("fromDateTime") LocalDateTime fromDateTime,
            @Param("toDateTime") LocalDateTime toDateTime
    );

}
