package com.example.application.data.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class Range {
    @Getter
    @Setter
    private double from;

    @Getter
    @Setter
    private double to;
}
