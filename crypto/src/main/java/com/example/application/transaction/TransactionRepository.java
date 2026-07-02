package com.example.application.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	@Query(value = """
			SELECT t.* FROM transactions t
			WHERE t.portfolio_id = :portfolioId
			""", nativeQuery = true)
	List<Transaction> findByPortfolio(@Param("portfolioId") Long portfolioId);

	@Query(value = """
			SELECT t.* FROM transactions t
			INNER JOIN assets a ON a.id = t.asset_id
			WHERE t.portfolio_id = :portfolioId AND a.symbol = :assetSymbol
			""", nativeQuery = true)
	List<Transaction> findByPortfolioAndAsset(@Param("portfolioId") Long portfolioId,
											  @Param("assetSymbol") String assetSymbol);

	@Query(value = """
			SELECT t.* FROM transactions t
			INNER JOIN assets a ON a.id = t.asset_id
			WHERE t.portfolio_id = :portfolioId
				AND a.symbol = :assetSymbol
				AND t.type = :transactionType
			""", nativeQuery = true)
	List<Transaction> findByPortfolioAndAssetAndType(@Param("portfolioId") Long portfolioId,
													 @Param("assetSymbol") String assetSymbol,
													 @Param("transactionType") TransactionType type);

	@Query(value = """
			SELECT t.* FROM transactions t
			WHERE t.portfolio_id = :portfolioId
			    AND t.dateTime BETWEEN :fromDateTime AND :toDateTime
			""", nativeQuery = true)
	List<Transaction> findByPortfolioAndDateTimeBetween(
			@Param("portfolioId") Long portfolioId,
			@Param("fromDateTime") LocalDateTime fromDateTime,
			@Param("toDateTime") LocalDateTime toDateTime
	);

}
