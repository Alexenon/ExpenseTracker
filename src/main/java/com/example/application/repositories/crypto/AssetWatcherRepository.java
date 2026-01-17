package com.example.application.repositories.crypto;

import com.example.application.entities.crypto.AssetWatcher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetWatcherRepository extends JpaRepository<AssetWatcher, Long> {

	@Query(value = """
			SELECT t FROM asset_watchers aw
			WHERE aw.portfolio_id = :portfolio_id
			""")
	List<AssetWatcher> findByPortfolio(@Param("portfolioId") Long portfolioId);

	@Query(value = """
			SELECT t FROM asset_watchers aw
			INNER JOIN assets a ON a.id = aw.asset_id
			WHERE a.symbol = :assetSymbol
			""")
    List<AssetWatcher> findByAsset(@Param("assetSymbol") String assetSymbol);

	@Query(value = """
			SELECT t FROM asset_watchers aw
			INNER JOIN assets a ON a.id = aw.asset_id
			WHERE a.symbol = :assetSymbol AND aw.portfolio_id = :portfolio_id
			""")
    List<AssetWatcher> findByPortfolioAndAsset(@Param("portfolioId") Long portfolioId,
											   @Param("assetSymbol") String assetSymbol);

	@Query(value = """
			SELECT t FROM asset_watchers aw
			INNER JOIN assets a ON a.id = aw.asset_id
			WHERE a.symbol = :assetSymbol AND aw.portfolio_id = :portfolio_id AND aw.action_type = :actionType
			""")
    List<AssetWatcher> findByPortfolioAndAssetAndActionType(@Param("portfolioId") Long portfolioId,
															@Param("assetSymbol") String assetSymbol,
															@Param("actionType") AssetWatcher.ActionType actionType);

}
