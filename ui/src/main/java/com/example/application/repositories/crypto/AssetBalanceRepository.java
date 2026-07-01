package com.example.application.repositories.crypto;

import com.example.application.entities.crypto.AssetBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetBalanceRepository extends JpaRepository<AssetBalance, Long> {

	@Query(value = """
			         SELECT ab.* FROM asset_balances ab
			         WHERE ab.portfolio_id = :portfolioId
			""", nativeQuery = true)
	List<AssetBalance> findByPortfolio(@Param("portfolioId") Long portfolioId);

	@Query(value = """
			         SELECT ab.* FROM asset_balances ab
			         INNER JOIN assets a ON a.id = ab.asset_id
			         WHERE ab.portfolio_id = :portfolioId AND a.symbol = :assetSymbol
			""", nativeQuery = true)
	Optional<AssetBalance> findByPortfolioAndAsset(@Param("portfolioId") Long portfolioId,
												   @Param("assetSymbol") String assetSymbol);

	@Query(value = """
			         SELECT ab.* FROM asset_balances ab
			         INNER JOIN assets a ON a.id = ab.asset_id
			         WHERE ab.portfolio_id = :portfolioId AND a.id = :assetId
			""", nativeQuery = true)
	Optional<AssetBalance> findByPortfolioAndAsset(@Param("portfolioId") Long portfolioId,
												   @Param("assetId") Long assetId);

}