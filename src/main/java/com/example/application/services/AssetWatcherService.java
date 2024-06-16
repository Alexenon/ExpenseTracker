package com.example.application.services;

import com.example.application.entities.AssetWatcher;
import com.example.application.entities.User;
import com.example.application.repositories.AssetWatcherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssetWatcherService {

    @Autowired
    private AssetWatcherRepository assetWatcherRepository;

    public List<AssetWatcher> getAssetsWatchers() {
        return assetWatcherRepository.findAll();
    }

    public List<AssetWatcher> getAssetsWatchersByUser(User user) {
        return assetWatcherRepository.findByUser(user);
    }

    public AssetWatcher save(AssetWatcher assetWatcher) {
        return assetWatcherRepository.save(assetWatcher);
    }

    public void delete(AssetWatcher assetWatcher) {
        assetWatcherRepository.delete(assetWatcher);
    }

    public void deleteById(long assetWatcherId) {
        assetWatcherRepository.deleteById(assetWatcherId);
    }

}
