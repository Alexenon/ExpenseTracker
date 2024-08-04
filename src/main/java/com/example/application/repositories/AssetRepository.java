package com.example.application.repositories;

import com.example.application.entities.crypto.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {

    Asset findBySymbol(String name);

}
