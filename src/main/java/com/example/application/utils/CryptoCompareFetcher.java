package com.example.application.utils;

import com.example.application.utils.responses.AssetMetaDataApiResp;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
 * https://developers.cryptocompare.com/documentation/legacy/Other/allCoinsWithContentEndpoint
 *
 * https://developers.cryptocompare.com/documentation/data-api/asset_v1_top_list
 *
 *
 * */
public class CryptoCompareFetcher {

    private static final String COIN_LIST_URL = "https://min-api.cryptocompare.com/data/all/coinlist";
    private static final String METADATA_URL = "https://data-api.cryptocompare.com/asset/v1/metadata";
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final Duration TIMEOUT_DURATION = Duration.ofSeconds(60);

    public static void main(String[] args) {
        System.out.println(getCoinMetaData("BTC"));
    }

    public static List<CoinData> getMetaDataList() {
        String json = fetchCoinMetaData();
        ObjectMapper objectMapper = new ObjectMapper();
        List<CoinData> coinDataList = new ArrayList<>();
        try {
            ApiResponse apiResponse = objectMapper.readValue(json, ApiResponse.class);
            Map<String, CoinData> dataMap = apiResponse.getData();
            if (dataMap != null) {
                coinDataList.addAll(dataMap.values());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return coinDataList;
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

    public static AssetMetaDataApiResp getCoinMetaData(String symbol) {
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
            throw new RuntimeException(e);
        }
    }


}
