package com.example.application.repositories.crypto;

import com.example.application.entities.common.TransactionType;
import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.CryptoTransaction;
import com.example.application.entities.crypto.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CryptoTransactionRepository extends JpaRepository<CryptoTransaction, Long> {

    List<CryptoTransaction> findByPortfolio(Portfolio portfolio);

    List<CryptoTransaction> findByPortfolioAndAsset(Portfolio portfolio, Asset asset);

    List<CryptoTransaction> findByPortfolioAndAssetAndType(Portfolio portfolio, Asset asset, TransactionType type);

    @Query("""
                SELECT t FROM crypto_transactions t
                WHERE t.portfolio = :portfolio
                  AND t.dateTime >= :fromDateTime
                  AND t.dateTime < :toDateTime
            """)
    List<CryptoTransaction> findByPortfolioAndDateTimeBetween(
            @Param("portfolio") Portfolio portfolio,
            @Param("fromDateTime") LocalDateTime fromDateTime,
            @Param("toDateTime") LocalDateTime toDateTime
    );

}
