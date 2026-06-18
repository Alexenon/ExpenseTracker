package com.example.application.repositories.crypto;

import com.example.application.entities.crypto.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

	@Query(value = """
			         SELECT * FROM portfolios
			         WHERE user_id = :userId
			""", nativeQuery = true)
	List<Portfolio> findByUser(@Param("userId") long userId);

	@Query(value = """
			         SELECT * FROM portfolios
			         WHERE name = :name AND user_id = :userId
			""", nativeQuery = true)
	Optional<Portfolio> findByNameAndUser(@Param("name") String name, @Param("userId") long userId);

	@Query(value = """
			         SELECT * FROM portfolios
			         WHERE user_id = :userId
			         ORDER BY last_time_updated
			         LIMIT 1
			""", nativeQuery = true)
	Portfolio findLatestUpdatedPortfolio(@Param("userId") long userId);

}
