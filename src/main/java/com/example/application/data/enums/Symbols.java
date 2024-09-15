package com.example.application.data.enums;

import java.util.Arrays;
import java.util.List;

public enum Symbols {
    BTC,
    ETH,
    SOL,
    BNB,

    // ALT COINS
    TON,
    NOT, // HERE
    ENA,
    SUI, // HERE
    UNI,
    ICP,
    AEVO, // HERE
    ARB,
    ALT,
    APT,
    AVAX,
    ATOM,
    JUP,
    STRK,
    DOT,
    LINK,
    OP,
    TIA,
    MANTLE, // HERE
    POL,
    DYDX, // HERE
    XRP, // HERE
    ZK, // HERE
    ZRO, // HERE

    // STABLE COINS
    USDT,
    USDC,
    DAI;

    public static List<String> getAll() {
        return Arrays.stream(Symbols.values()).map(Enum::name).toList();
    }

}
