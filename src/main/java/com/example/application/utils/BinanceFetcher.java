package com.example.application.utils;

import com.example.application.data.CryptoCurrency;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

/*
 * https://binance.github.io/binance-api-swagger/
 *
 * https://api.binance.com/api/v3/ticker/price?symbol=BTCUSDT
 *
 * https://dev.binance.vision/t/how-to-correctly-get-lowest-highest-price-in-last-3-days-for-a-given-symbol/10220
 * */
public class BinanceFetcher {

    private static final String BINANCE_API_URL = "https://api.binance.com/api/v3/ticker/price";
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    public static String fetchCryptoPrices() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(BINANCE_API_URL))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<CryptoCurrency> getAllCryptoPrices() {
        String jsonResponse = fetchCryptoPrices();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(jsonResponse, new TypeReference<>() {
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<CryptoCurrency> getCryptoPrices(String currency) {
        return getAllCryptoPrices().stream()
                .filter(c -> c.getSymbol().endsWith(currency))
                .peek(c -> {
                    String symbolWithoutCurrency = c.getSymbol().replace(currency, "");
                    c.setSymbol(symbolWithoutCurrency);
                })
                .toList();
    }

    public static List<CryptoCurrency> getCryptoPrices(List<String> symbols, String currency) {
        List<String> symbolsWithCurrency = symbols.stream().map(s -> s + currency).toList();

        return getAllCryptoPrices().stream()
                .filter(c -> symbolsWithCurrency.stream().anyMatch(s -> s.equals(c.getSymbol())))
                .toList();
    }

}
