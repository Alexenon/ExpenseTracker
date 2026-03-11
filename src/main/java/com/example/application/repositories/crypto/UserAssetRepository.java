package com.example.application.repositories.crypto;

import com.example.application.entities.User;
import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.UserAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAssetRepository extends JpaRepository<UserAsset, Long> {

    Optional<UserAsset> findByUserAndAsset(User user, Asset asset);

}
