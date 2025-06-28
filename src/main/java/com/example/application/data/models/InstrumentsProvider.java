package com.example.application.data.models;

import com.example.application.data.enums.SymbolIndentifier;
import com.example.application.utils.fetchers.CryptoCompareFetcher;
import com.example.application.utils.fetchers.api_responses.AssetMetaDataApiResp;
import com.example.application.utils.fetchers.api_responses.AssetMetadata;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("LoggingSimilarMessage")
@Slf4j
@Component
public class InstrumentsProvider {

    private Map<String, AssetMetadata> metadataPerAsset;

    private InstrumentsProvider() {
        metadataPerAsset = getUpdatedMetadata();
    }

    // TODO: Compare with  ->  parallelStream()
    @NotNull
    private Map<String, AssetMetadata> fetchMetadata() {
        Map<String, AssetMetadata> metadataMap = new HashMap<>();
        Arrays.stream(SymbolIndentifier.values())
                .map(Enum::name)
                .forEach(symbolName -> {
                    AssetMetaDataApiResp response = CryptoCompareFetcher.getCoinMetaData(symbolName);
                    // TODO: Re-try on first failure
                    if (response == null) {
                        return;
                    }

                    AssetMetadata metadata = response.getData();
                    if (metadata != null) {
                        metadataMap.put(symbolName, metadata);
                    } else {
                        // TODO: Here getError() is missing
                        log.warn("Missing asset metadata, cause: {}", response.getError());
                    }
                });

        log.info("Loaded meta data for {} instruments", metadataMap.size());
        return metadataMap;
    }

    @NotNull
    public Map<String, AssetMetadata> getMetadata() {
        return metadataPerAsset;
    }

    @NotNull
    public Map<String, AssetMetadata> getUpdatedMetadata() {
        metadataPerAsset = fetchMetadata();
        return metadataPerAsset;
    }

}
