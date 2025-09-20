package com.example.application.services.crypto;

import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.AssetWatcher;
import com.example.application.entities.crypto.Portfolio;
import com.example.application.repositories.crypto.AssetWatcherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/*
 * TODO: [LONG TERM] Add retrieve from holdings, on successful state
 * */

@Service
public class AssetWatcherService {

    @Autowired
    private AssetWatcherRepository assetWatcherRepository;

    public AssetWatcher save(AssetWatcher assetWatcher) {
         return assetWatcherRepository.save(assetWatcher);
    }

    public void delete(AssetWatcher assetWatcher) {
        assetWatcherRepository.delete(assetWatcher);
    }

    public List<AssetWatcher> findBy(Portfolio portfolio) {
        return assetWatcherRepository.findByPortfolio(portfolio);
    }

    public List<AssetWatcher> findBy(Asset asset) {
        return assetWatcherRepository.findByAsset(asset);
    }

    public List<AssetWatcher> findBy(Portfolio portfolio, Asset asset) {
        return assetWatcherRepository.findByPortfolioAndAsset(portfolio, asset);
    }

    public List<AssetWatcher> findBy(Portfolio portfolio, Asset asset, AssetWatcher.ActionType actionType) {
        return assetWatcherRepository.findByPortfolioAndAssetAndActionType(portfolio, asset, actionType);
    }

}
