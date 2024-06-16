package com.example.application.data.models;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

public class Asset {

    @Getter
    private final Currency currency;
    @Getter
    private List<Holding> holdings;
    @Setter
    @Getter
    private String comment;

    public Asset(Currency currency, List<Holding> holdings, String comment) {
        this.currency = currency;
        this.holdings = holdings;
        this.comment = comment;
    }

    public double getAveragePrice() {
        return holdings.stream()
                .map(Holding::getPriceBought)
                .mapToDouble(BigDecimal::doubleValue)
                .average()
                .orElse(0.0);
    }

    public double getProfit() {
        return 0.0;
    }

    public double getTotalAmount() {
        return holdings.stream()
                .mapToDouble(Holding::getAmount)
                .sum();
    }

}
