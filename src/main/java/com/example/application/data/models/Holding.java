package com.example.application.data.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
public class Holding {

    @Getter
    private String currencyName;

    @Getter
    private double amount;

    @Getter
    private BigDecimal priceBought;

    @Getter
    private LocalDateTime dateTimeBought;

    public Holding(String currencyName, double amount, BigDecimal priceBought) {
        this(currencyName, amount, priceBought, LocalDateTime.now());
    }

}
