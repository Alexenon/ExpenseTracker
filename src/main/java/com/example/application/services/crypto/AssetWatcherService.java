package com.example.application.services.crypto;

import com.example.application.entities.crypto.AssetWatcher;
import com.example.application.repositories.crypto.AssetWatcherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/*
* FIXME: MAYBE DELETE THIS
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

    public void deleteById(long assetWatcherId) {
        assetWatcherRepository.deleteById(assetWatcherId);
    }

}
