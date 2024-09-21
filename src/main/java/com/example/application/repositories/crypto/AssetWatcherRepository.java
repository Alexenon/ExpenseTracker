package com.example.application.repositories.crypto;

import com.example.application.data.models.crypto.Wallet;
import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.AssetWatcher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetWatcherRepository extends JpaRepository<AssetWatcher, Long> {

    List<AssetWatcher> findBy(Wallet wallet);

    List<AssetWatcher> findBy(Asset asset);

    List<AssetWatcher> findBy(Wallet wallet, Asset asset);

    List<AssetWatcher> findBy(Wallet wallet, Asset asset, AssetWatcher.ActionType actionType);

}
