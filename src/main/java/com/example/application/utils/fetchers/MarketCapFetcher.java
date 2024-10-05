package com.example.application.utils.fetchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class MarketCapFetcher {

    private static final String API_KEY = System.getenv("MARKET_CAP_API_KEY");
    private static final String METADATA_URL = "https://pro-api.coinmarketcap.com/v2/cryptocurrency/info";
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final Duration TIMEOUT_DURATION = Duration.ofSeconds(60);

    public static void main(String[] args) {
        System.out.println(getAssetLogoUrl("BTC"));
    }

    public static MarketCapResponse getCryptoCurrencyInfo(String symbol) {
        try {
            String result = makeAPICall(METADATA_URL, symbol);
            MarketCapResponse marketCapResponse = parseResponse(result);
            System.out.println(marketCapResponse);
            return marketCapResponse;
        } catch (IOException | InterruptedException e) {
            System.out.println("Error: cannot access content - " + e);
        }
        return null;
    }

    public static String makeAPICall(String uri, String symbol) throws IOException, InterruptedException {
        String url = uri + "?symbol=" + URLEncoder.encode(symbol, StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .timeout(TIMEOUT_DURATION)
                .header(HttpHeaders.ACCEPT, "application/json")
                .header("X-CMC_PRO_API_KEY", API_KEY)
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }

    public static MarketCapResponse parseResponse(String jsonResponse) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonResponse, MarketCapResponse.class);
    }

    public static String getAssetLogoUrl(String symbol) {
        try {
            Map<String, MarketCapResponse.CryptocurrencyData> data = Objects.requireNonNull(getCryptoCurrencyInfo(symbol)).getData();
            Optional<String> firstKey = data.keySet().stream().findFirst();
            return data.get(firstKey.orElseThrow()).getLogo();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
