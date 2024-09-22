package com.example.application.data.models;

import com.example.application.entities.crypto.Asset;
import com.example.application.repositories.crypto.AssetRepository;
import com.example.application.utils.fetchers.CryptoCompareFetcher;
import com.example.application.utils.fetchers.api_responses.AssetMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InstrumentsProvider {

    private final AssetRepository assetRepository;

    private Map<Asset, AssetMetadata> metadataPerAsset;

    @Autowired
    private InstrumentsProvider(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
        this.metadataPerAsset = fetchMetadata();
    }

    private Map<Asset, AssetMetadata> fetchMetadata() {
        Map<Asset, AssetMetadata> map = new HashMap<>();

        assetRepository.findAll().forEach(asset -> {
            try {
                String symbolName = asset.getSymbol();
                AssetMetadata assetMetadata = CryptoCompareFetcher.getCoinMetaData(symbolName).getData();
                map.put(asset, assetMetadata);
            } catch (Exception e) {
                System.out.printf("Failed to fetch metadata for asset: %s. Error: %s\n", asset.getSymbol(), e.getMessage());
            }
        });

        return map;
    }

    // TODO: Compare results

    /**
     * @return fetched metadata in parallel for performance improvement
     */
    private Map<Asset, AssetMetadata> fetchMetadataInParallel() {
        Map<Asset, AssetMetadata> map = new ConcurrentHashMap<>();
        assetRepository.findAll().parallelStream().forEach(asset -> {
            try {
                String symbolName = asset.getSymbol();
                AssetMetadata assetMetadata = CryptoCompareFetcher.getCoinMetaData(symbolName).getData();
                map.put(asset, assetMetadata);
            } catch (Exception e) {
                System.out.printf("Failed to fetch metadata for asset: %s. Error: %s\n", asset.getSymbol(), e.getMessage());
            }
        });
        return map;
    }

    public Map<Asset, AssetMetadata> getMetadata() {
        return metadataPerAsset;
    }

    public Map<Asset, AssetMetadata> getUpdatedMetadata() {
        metadataPerAsset = fetchMetadata();
        return metadataPerAsset;
    }

    public String getAssetLogoUrl() {
        return "";
    }

}
