package com.example.application.data.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class Currency {

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private double currentPrice;

    @Getter
    @Setter
    private double minPrice;

    @Getter
    @Setter
    private double maxPrice;

}
