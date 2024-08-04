package com.example.application.data.models.crypto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
public class Currency {

    @Getter
    private final String name;

    @Getter
    private final BigDecimal currentPrice;

    @Getter
    private final BigDecimal changesLast24Hours;

}
