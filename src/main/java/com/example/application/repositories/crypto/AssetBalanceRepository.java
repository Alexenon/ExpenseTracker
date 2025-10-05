package com.example.application.repositories.crypto;

import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.AssetBalance;
import com.example.application.entities.crypto.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetBalanceRepository extends JpaRepository<AssetBalance, Long> {

    List<AssetBalance> findByPortfolio(Portfolio portfolio);

    Optional<AssetBalance> findByPortfolioAndAsset(Portfolio portfolio, Asset asset);

//    @Query(value = """
//            SELECT * FROM asset_balances ab
//            WHERE ab.portfolio_id = :portfolioId AND ab.amount > 0
//            """, nativeQuery = true)
//    List<AssetBalance> findByPortfolioWithNonZeroAmount(@Param("portfolioId") long portfolioId);
//
//    @Query(value = """
//            SELECT * FROM asset_balances ab
//            WHERE ab.portfolio_id = :portfolioId AND ab.amount > 0
//            """, nativeQuery = true)
//    List<AssetBalance> findByPortfoliosWithNonZeroAmount(@Param("userId") long userId);

    /*
    """
        SELECT * FROM asset_balances AB
        INNER JOIN users U ON U.id = AB.user_id
        INNER JOIN portfolios P ON U.id = P.user_id
        WHERE P.user_id = :userId;

    """
    */

}
