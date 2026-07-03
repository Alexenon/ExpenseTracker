package com.example.application.data.models;

import com.example.application.asset.SymbolIndentifier;
import com.example.application.components.AssetUpdateScheduler;
import com.example.application.fetchers.binance.BinanceFetcher;
import com.example.application.fetchers.crypto_compare.CryptoCompareFetcher;
import com.example.application.fetchers.crypto_compare.response.AssetMetaDataApiResp;
import com.example.application.fetchers.crypto_compare.response.AssetMetadata;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * Service designed load internal instruments from external sources
 *
 * @see BinanceFetcher
 * @see CryptoCompareFetcher
 * @see AssetUpdateScheduler
 */
@Slf4j
@Service
public class InstrumentsProvider {

	private Map<String, AssetMetadata> assetMetadata;

	private InstrumentsProvider() {
		assetMetadata = getUpdatedAssetMetadata();
	}

	private Map<String, AssetMetadata> fetchMetadata() {
		log.info("Starting retrieving asset data from external API");
		Map<String, AssetMetadata> metadataMap = new ConcurrentHashMap<>();

		Stream.of(SymbolIndentifier.values())
				.map(Enum::name)
				.parallel()
				.forEach(symbolName -> {
					try {
						AssetMetaDataApiResp response = CryptoCompareFetcher.fetchAssetMetaData(symbolName);

						AssetMetadata metadata = response.getData();
						if (metadata != null) {
							metadataMap.put(symbolName, metadata);
						} else {
							log.info("Missing asset metadata, cause: {}", response.getError());
						}
					} catch (Exception e) {
						log.warn("Missing asset metadata", e);
					}
				});

		log.info("Finished retrieving asset data from external API for {} assets", metadataMap.size());
		return metadataMap;
	}

	@Nonnull
	public Map<String, AssetMetadata> getAssetMetadata() {
		return assetMetadata;
	}

	@Nonnull
	public Map<String, AssetMetadata> getUpdatedAssetMetadata() {
		assetMetadata = fetchMetadata();
		return assetMetadata;
	}

}