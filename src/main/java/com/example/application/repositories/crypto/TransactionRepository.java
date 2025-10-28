package com.example.application.repositories.crypto;

import com.example.application.entities.common.TransactionType;
import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.Portfolio;
import com.example.application.entities.crypto.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	List<Transaction> findByPortfolio(Portfolio portfolio);

	List<Transaction> findByPortfolioAndAsset(Portfolio portfolio, Asset asset);

	List<Transaction> findByPortfolioAndAssetAndType(Portfolio portfolio, Asset asset, TransactionType type);

	@Query(value = """
			SELECT t FROM transactions t
			WHERE t.portfolio = :portfolio
			    AND t.dateTime BETWEEN :fromDateTime AND :toDateTime
			""")
	List<Transaction> findByPortfolioAndDateTimeBetween(
			@Param("portfolio") Portfolio portfolio,
			@Param("fromDateTime") LocalDateTime fromDateTime,
			@Param("toDateTime") LocalDateTime toDateTime
	);

}
