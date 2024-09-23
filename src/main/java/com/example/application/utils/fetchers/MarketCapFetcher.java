package com.example.application.utils.fetchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

public class MarketCapFetcher {

    private static final String API_KEY = System.getenv("MARKET_CAP_API_KEY");
    private static final String METADATA_URL = "https://pro-api.coinmarketcap.com/v2/cryptocurrency/info";

    public static void main(String[] args) {
        System.out.println(getAssetLogoUrl("BTC"));
    }

    public static MarketCapResponse getCryptoCurrencyInfo(String symbol) {
        List<NameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("symbol", symbol));

        try {
            String result = makeAPICall(METADATA_URL, parameters);
            MarketCapResponse marketCapResponse = parseResponse(result);
            System.out.println(marketCapResponse);
            return marketCapResponse;
        } catch (IOException e) {
            System.out.println("Error: cannot access content - " + e);
        } catch (URISyntaxException e) {
            System.out.println("Error: Invalid URL " + e);
        }

        return null;
    }

    public static String makeAPICall(String uri, List<NameValuePair> parameters) throws URISyntaxException, IOException {
        String responseContent;

        URIBuilder query = new URIBuilder(uri);
        query.addParameters(parameters);

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet(query.build());

        request.setHeader(HttpHeaders.ACCEPT, "application/json");
        request.addHeader("X-CMC_PRO_API_KEY", API_KEY);

        try (CloseableHttpResponse response = client.execute(request)) {
            HttpEntity entity = response.getEntity();
            responseContent = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
        }

        return responseContent;
    }

    public static MarketCapResponse parseResponse(String jsonResponse) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonResponse, MarketCapResponse.class);
    }

    @SneakyThrows
    public static String getAssetLogoUrl(String symbol) {
        Map<String, MarketCapResponse.CryptocurrencyData> data = Objects.requireNonNull(getCryptoCurrencyInfo(symbol)).getData();
        Optional<String> firstKey = data.keySet().stream().findFirst();
        return data.get(firstKey.orElseThrow()).getLogo();
    }
}


