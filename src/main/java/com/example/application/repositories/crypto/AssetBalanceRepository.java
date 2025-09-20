package com.example.application.repositories.crypto;

import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.AssetBalance;
import com.example.application.entities.crypto.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetBalanceRepository extends JpaRepository<AssetBalance, Long> {

    List<AssetBalance> findByPortfolio(Portfolio portfolio);

    Optional<AssetBalance> findByPortfolioAndAsset(Portfolio portfolio, Asset asset);

    @Query(value = """
            SELECT * FROM asset_balances pb
            WHERE pb.portfolio_id = :portfolioId AND pb.amount > 0
            """, nativeQuery = true)
    List<AssetBalance> findByPortfolioWithNonZeroAmount(@Param("portfolioId") long portfolioId);

}
