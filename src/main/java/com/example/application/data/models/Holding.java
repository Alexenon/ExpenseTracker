package com.example.application.data.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/*
 * Name = BTC
 * CurrentPrice = 64_000
 * MinPrice = 32_000
 * MaxPrice = 73_000
 *
 * StartPrice1 = 60_000
 * StartPrice2 = 50_000
 * StartPrice3 = 40_000
 *
 *
 * */


@AllArgsConstructor
public class Holding {

    @Getter
    private String currencyName;

    @Getter
    @Setter
    // TODO: Change to sorted collection like PriorityQueue, TreeSet
    private List<Double> wantedPrices;

    @Getter
    @Setter
    private String comment;

}
