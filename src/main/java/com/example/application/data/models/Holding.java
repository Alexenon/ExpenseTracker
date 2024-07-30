package com.example.application.data.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
public class Holding {

    @Getter
    private final String currencyName;

    @Getter
    private double amount;

    @Getter
    private LocalDateTime lastTimeUpdated;

    public Holding(String currencyName) {
        this(currencyName, 0);
    }

    public Holding(String currencyName, double amount) {
        this.currencyName = currencyName;
        this.amount = amount;
        this.lastTimeUpdated = LocalDateTime.now();
    }

    public void addAmount(double amount) {
        this.amount += amount;
    }

    public void removeAmount(double amount) {
        this.amount -= amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }


}
