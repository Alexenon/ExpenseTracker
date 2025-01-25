package com.example.application.data.models;

import com.example.application.entities.crypto.Asset;
import com.example.application.repositories.crypto.AssetRepository;
import com.example.application.utils.fetchers.CryptoCompareFetcher;
import com.example.application.utils.fetchers.api_responses.AssetMetadata;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/*
    TODO:
        - Compare results using findAll().parallelStream()
* */
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("LoggingSimilarMessage")
@Slf4j
@Component
public class InstrumentsProvider {

    private final AssetRepository assetRepository;
    private final Map<String, AssetMetadata> metadataPerAsset = new HashMap<>();

    @Autowired
    private InstrumentsProvider(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
        fetchMetadata();
    }

    private void fetchMetadata() {
        List<Asset> assets = assetRepository.findAll();
        log.info("Started fetching metadata for {} assets", assets.size());
        assets.forEach(asset -> {
            try {
                String symbolName = asset.getSymbol();
                AssetMetadata assetMetadata = CryptoCompareFetcher.getCoinMetaData(symbolName).getData();
                Optional.of(CryptoCompareFetcher.getCoinMetaData(symbolName).getData())
                        .ifPresent(metadata -> metadataPerAsset.put(symbolName, metadata));
            } catch (Exception e) {
                log.error("Failed to fetch metadata for asset: {}. Error: {}", asset.getSymbol(), ExceptionUtils.getStackTrace(e));
            }
        });

        log.info("Loaded meta data for {} instruments", metadataPerAsset.size());
    }

    // TODO: Compare results

    /**
     * @return fetched metadata in parallel for performance improvement
     */
    private Map<String, AssetMetadata> fetchMetadataInParallel() {
        Map<String, AssetMetadata> map = new ConcurrentHashMap<>();
        assetRepository.findAll().parallelStream().forEach(asset -> {
            try {
                String symbolName = asset.getSymbol();
                Optional.of(CryptoCompareFetcher.getCoinMetaData(symbolName).getData())
                        .ifPresent(metadata -> map.put(symbolName, metadata));
            } catch (Exception e) {
                log.error("Failed to fetch metadata for asset: {}. Error: {}", asset.getSymbol(), ExceptionUtils.getStackTrace(e));
            }
        });

        log.info("Loaded meta data for {} instruments", metadataPerAsset.size());
        return map;
    }

    public Map<String, AssetMetadata> getMetadata() {
        if (metadataPerAsset.isEmpty())
            throw new NullPointerException("The metadata for all assets is empty");

        return metadataPerAsset;
    }

    public Map<String, AssetMetadata> getUpdatedMetadata() {
        fetchMetadata();
        return metadataPerAsset;
    }

}
