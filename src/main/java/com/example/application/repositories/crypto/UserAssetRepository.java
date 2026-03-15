package com.example.application.repositories.crypto;

import com.example.application.entities.crypto.UserAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAssetRepository extends JpaRepository<UserAsset, Long> {

	@Query(value = """
        SELECT ua.*
        FROM user_assets ua
        INNER JOIN assets a ON a.id = ua.asset_id
        WHERE a.symbol = :assetSymbol AND ua.user_id = :userId
        """, nativeQuery = true)
	Optional<UserAsset> findByUserAndAsset(@Param("userId") Long userId,
										   @Param("assetSymbol") String assetSymbol);

}
