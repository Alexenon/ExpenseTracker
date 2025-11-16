package com.example.application.data.models;

import com.example.application.data.enums.SymbolIndentifier;
import com.example.application.utils.fetchers.BinanceFetcher;
import com.example.application.utils.fetchers.crypto_compare.CryptoCompareFetcher;
import com.example.application.utils.fetchers.crypto_compare.response.AssetMetaDataApiResp;
import com.example.application.utils.fetchers.crypto_compare.response.AssetMetadata;
import jakarta.validation.constraints.NotNull;
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
 */
@Slf4j
@Service
public class InstrumentsProvider {

	private Map<String, AssetMetadata> metadataPerAsset;

	private InstrumentsProvider() {
		metadataPerAsset = getUpdatedMetadata();
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
							log.warn("Missing asset metadata, cause: {}", response.getError());
						}
					} catch (Exception e) {
						log.warn("Missing asset metadata", e);
					}
				});

		log.info("Finished retrieving asset data from external API for {} assets", metadataMap.size());
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