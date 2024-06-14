package com.example.application.data;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

public class CryptoCurrency {

    @JsonProperty("symbol")
    private String symbol;

    @JsonProperty("price")
    private double price;

    @JsonGetter
    public String getSymbol() {
        return symbol;
    }

    @JsonSetter
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @JsonGetter
    public double getPrice() {
        return price;
    }

    @JsonSetter
    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "CryptoPrice{symbol='%s', price='%f'}".formatted(symbol, price);
    }
}
