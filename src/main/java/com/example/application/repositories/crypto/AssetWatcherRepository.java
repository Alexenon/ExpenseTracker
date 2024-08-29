package com.example.application.repositories.crypto;

import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.AssetWatcher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetWatcherRepository extends JpaRepository<AssetWatcher, Long> {

    List<AssetWatcher> findByAsset(Asset asset);

    List<AssetWatcher> findByAssetAndActionType(Asset asset, AssetWatcher.ActionType actionType);

}
