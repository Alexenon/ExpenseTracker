package com.example.application.data.models;

import com.example.application.utils.BinanceFetcher;
import com.example.application.utils.CryptoCompareFetcher;
import com.example.application.utils.responses.AssetData;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class InstrumentsProvider {

    private static volatile InstrumentsProvider instance;

    @Getter
    private List<Currency> currencyList;

    @Getter
    private CopyOnWriteArrayList<Asset> assets;


    private InstrumentsProvider() {
        this.currencyList = updateCurrencyList();
        this.assets = initAssets();
    }

    public static InstrumentsProvider getInstance() {
        if (instance == null) {
            synchronized (InstrumentsProvider.class) {
                if (instance == null) {
                    System.out.println("NEW INSTANCE");
                    instance = new InstrumentsProvider();
                }
            }
        }
        return instance;
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

    public AssetData getCoinData(String name) {
        return CryptoCompareFetcher.getCoinMetaData(name).getData();
    }

    public CopyOnWriteArrayList<Asset> initAssets() {
        System.out.println(" -> RECEIVING ASSETS");

        return currencyList.stream()
                .map(Currency::getName)
                .map(Asset::new)
                .filter(a -> a.getAssetData().getPriceUsd() > 0)
                .collect(Collectors.toCollection(CopyOnWriteArrayList::new));
    }


}
