package com.example.application.data.models;

import lombok.Getter;

import java.time.LocalDateTime;

public class Asset {
    @Getter
    private Currency currency;
    @Getter
    private double amount;
    @Getter
    private double priceBought;
    @Getter
    private LocalDateTime dateTime;
}
