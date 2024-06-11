package com.example.application.data.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class Holding {

    @Getter
    @Setter
    private Currency currency;

    @Getter
    @Setter
    private Range preferredRange;

    @Getter
    @Setter
    private Range wantedRange;

    @Getter
    @Setter
    private double score;


}
