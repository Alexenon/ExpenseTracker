package com.example.application.data.models;

import com.example.application.utils.BinanceFetcher;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

public class CurrencyProvider {

    private static CurrencyProvider instance;

    @Getter
    private List<Currency> currencyList;

    private CurrencyProvider() {
        this.currencyList = updateCurrencyList();
    }

    public static CurrencyProvider getInstance() {
        if (instance == null) {
            instance = new CurrencyProvider();
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

}
