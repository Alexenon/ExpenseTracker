package com.example.application.repositories.crypto;

import com.example.application.entities.common.TransactionType;
import com.example.application.entities.crypto.AssetWatcher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetWatcherRepository extends JpaRepository<AssetWatcher, Long> {

	@Query(value = """
			SELECT * FROM asset_watchers aw
			WHERE aw.portfolio_id = :portfolioId
			""", nativeQuery = true)
	List<AssetWatcher> findByPortfolio(@Param("portfolioId") Long portfolioId);

	@Query(value = """
			SELECT * FROM asset_watchers aw
			INNER JOIN assets a ON a.id = aw.asset_id
			WHERE a.symbol = :assetSymbol
			""", nativeQuery = true)
	List<AssetWatcher> findByAsset(@Param("assetSymbol") String assetSymbol);

	@Query(value = """
			SELECT * FROM asset_watchers aw
			INNER JOIN assets a ON a.id = aw.asset_id
			WHERE a.symbol = :assetSymbol AND aw.portfolio_id = :portfolioId
			""", nativeQuery = true)
	List<AssetWatcher> findByPortfolioAndAsset(@Param("portfolioId") Long portfolioId,
											   @Param("assetSymbol") String assetSymbol);

	@Query(value = """
			SELECT * FROM asset_watchers aw
			INNER JOIN assets a ON a.id = aw.asset_id
			WHERE a.symbol = :assetSymbol AND aw.portfolio_id = :portfolioId AND aw.transactionType = :transactionType
			""", nativeQuery = true)
	List<AssetWatcher> findByPortfolioAndAssetAndTransactionType(@Param("portfolioId") Long portfolioId,
																 @Param("assetSymbol") String assetSymbol,
																 @Param("transactionType") TransactionType transactionType);

}
