package com.example.application.utils.fetchers.crypto_compare;

import com.example.application.utils.fetchers.crypto_compare.exceptions.ExternalApiResponseException;
import com.example.application.utils.fetchers.crypto_compare.response.ApiResponse;
import com.example.application.utils.fetchers.crypto_compare.response.AssetMetaDataApiResp;
import com.example.application.utils.fetchers.crypto_compare.response.Coin;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/*
 * https://developers.cryptocompare.com/documentation/legacy/Other/allCoinsWithContentEndpoint
 *
 * https://developers.cryptocompare.com/documentation/data-api/asset_v1_metadata
 *
 *
 * */
@Slf4j
public class CryptoCompareFetcher {

	private static final String COIN_LIST_URL = "https://min-api.cryptocompare.com/data/all/coinlist";
	private static final String METADATA_URL = "https://data-api.cryptocompare.com/asset/v1/metadata";
	private static final HttpClient httpClient = HttpClient.newHttpClient();
	private static final Duration TIMEOUT_DURATION = Duration.ofSeconds(60);

	public static void main(String[] args) {
//        System.out.println(getCoinMetaData("BTC"));


		getCoinList().stream()
				.sorted(Comparator.comparing(Coin::getCirculatingSupply))
				.forEach(c -> System.out.println(c.getSymbol() + ","));
	}

	public static List<Coin> getCoinList() {
		String json = fetchCoinMetaData();
		ObjectMapper objectMapper = new ObjectMapper();
		List<Coin> coinList = new ArrayList<>();
		try {
			ApiResponse apiResponse = objectMapper.readValue(json, ApiResponse.class);
			Map<String, Coin> dataMap = apiResponse.getData();
			if (dataMap != null) {
				coinList.addAll(dataMap.values());
			}
		} catch (IOException e) {
			System.out.println(ExceptionUtils.getStackTrace(e));
		}
		return coinList;
	}

	/**
	 * @return metadata per coin in a string format
	 */
	private static String fetchCoinMetaData() {
		try {
			HttpRequest request = HttpRequest.newBuilder()
					.GET()
					.uri(new URI(COIN_LIST_URL))
					.timeout(TIMEOUT_DURATION)
					.build();

			return httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * <a href="https://developers.cryptocompare.com/documentation/data-api/asset_v1_metadata">Documentation</a>
	 */
	@NotNull
	@Retryable(
			retryFor = {ExternalApiResponseException.class},
			maxAttempts = 5,
			backoff = @Backoff(delay = 1000, multiplier = 2)
	)
	public static AssetMetaDataApiResp fetchAssetMetaData(String symbol) {
		try {
			String url = METADATA_URL + "?asset="
						 + URLEncoder.encode(symbol, StandardCharsets.UTF_8)
						 + "&asset_lookup_priority=SYMBOL&quote_asset=USD";

			HttpRequest request = HttpRequest.newBuilder()
					.GET()
					.uri(URI.create(url))
					.timeout(TIMEOUT_DURATION)
					.build();

			ObjectMapper objectMapper = new ObjectMapper();
			String jsonResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
			return objectMapper.readValue(jsonResponse, AssetMetaDataApiResp.class);
		} catch (Exception e) {
			log.error("Failed to fetch asset metadata for:  {}", symbol, e);
			throw new ExternalApiResponseException("Failed to fetch asset metadata for: ", e);
		}
	}

	// https://developers.cryptocompare.com/documentation/legacy/Streaming/TopPriceEndpoint
	public String fetchTopCoinsByPrice() {
		try {
			String url = "https://min-api.cryptocompare.com/data/top/price?limit=100&tsym=USD";

			HttpRequest request = HttpRequest.newBuilder()
					.GET()
					.uri(URI.create(url))
					.timeout(TIMEOUT_DURATION)
					.build();

			return "";
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


}
