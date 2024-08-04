package com.example.application.data.enums;

import java.util.Arrays;
import java.util.List;

public enum Symbols {
    BTC,
    ETH,
    SOL,
    BNB,
    MNT,
    ALT;

    public static List<String> getAll() {
        return Arrays.stream(Symbols.values()).map(Enum::name).toList();
    }

}
