package com.example.application.repositories.crypto;

import com.example.application.entities.User;
import com.example.application.entities.crypto.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

	List<Portfolio> findByUser(User user);

	@Query(value = """
			         SELECT * FROM portfolios P
			         WHERE P.name = :name AND P.user_id = :userId
			""", nativeQuery = true)
	Optional<Portfolio> findByNameAndUser(@Param("name") String name, @Param("userId") long userId);

}
