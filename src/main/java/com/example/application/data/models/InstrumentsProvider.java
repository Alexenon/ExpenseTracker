package com.example.application.data.models;

import com.example.application.utils.BinanceFetcher;
import com.example.application.utils.CryptoCompareFetcher;
import com.example.application.utils.responses.AssetData;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class InstrumentsProvider {

    private static InstrumentsProvider instance;

    @Getter
    private List<Currency> currencyList;

    @Getter
    private List<Asset> assets;

    private InstrumentsProvider() {
        this.assets = initAssets();
    }

    public static InstrumentsProvider getInstance() {
        if (instance == null) {
            System.out.println("New Instance");
            instance = new InstrumentsProvider();
        }
        return instance;
    }

    public AssetData getCoinData(String name) {
        return CryptoCompareFetcher.getCoinMetaData(name).getData();
    }

    public List<Asset> initAssets() {
        return Arrays.stream(Symbol.values())
                .map(Enum::name)
                .map(Asset::new)
                .toList();
    }

    public List<Currency> updateCurrencyList() {
        List<Currency> currencyList = BinanceFetcher.getCryptoPrices("USDT")
                .stream()
                .map(cryptoCurrency -> {
                    String name = cryptoCurrency.getSymbol();
                    BigDecimal price = BigDecimal.valueOf(cryptoCurrency.getPrice());
                    return new Currency(name, price, BigDecimal.ZERO);
                })
                .toList();

        this.currencyList = currencyList;
        return currencyList;
    }

    public Currency getCurrencyByName(String name) {
        return currencyList.stream()
                .filter(c -> c.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

}
