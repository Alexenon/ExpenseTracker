package com.example.application.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
 * https://developers.cryptocompare.com/documentation/legacy/Other/allCoinsWithContentEndpoint
 * */
public class CryptoCompareFetcher {

    private static final String URL = "https://min-api.cryptocompare.com/data/all/coinlist";
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final Duration TIMEOUT_DURATION = Duration.ofSeconds(60);

    public static void main(String[] args) {
        getMetaDataList().forEach(System.out::println);
    }

    /**
     * @return metadata per coin in a string format
     */
    private static String fetchCoinMetaData() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(new URI(URL))
                    .timeout(TIMEOUT_DURATION)
                    .build();

            return httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private static List<CoinData> getMetaDataList() {
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


}
