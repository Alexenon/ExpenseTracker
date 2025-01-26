package com.example.application.utils.fetchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.stream.Collectors;

public class MarketFetcher {

    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final Duration TIMEOUT_DURATION = Duration.ofSeconds(60);
    private static final String MARKET_CAP_API_KEY = System.getenv("MARKET_CAP_API_KEY");
    private static final String METADATA_URL = "https://pro-api.coinmarketcap.com/v2/cryptocurrency/info";

    public static void main(String[] args) {
        System.out.println(getCoinMetaData("BTC"));
    }


    public static MarketCapResponse getCoinMetaData(String symbol) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> parameters = Map.of("symbol", symbol);
        String url = buildUrlWithParams(METADATA_URL, parameters);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(url))
                    .header(HttpHeaders.ACCEPT, "application/json")
                    .header("X-CMC_PRO_API_KEY", MARKET_CAP_API_KEY)
                    .timeout(TIMEOUT_DURATION)
                    .build();

            String jsonResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
            System.out.println(jsonResponse);
            return objectMapper.readValue(jsonResponse, MarketCapResponse.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String buildUrlWithParams(String baseUrl, Map<String, String> parameters) {
        String paramString = parameters.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));

        return baseUrl + "?" + paramString;
    }

}
