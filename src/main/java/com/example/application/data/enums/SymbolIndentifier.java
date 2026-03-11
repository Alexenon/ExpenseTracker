package com.example.application.data.enums;

import java.util.Arrays;
import java.util.List;

public enum SymbolIndentifier {
    ARB("Arbitrum"),
    OP("Optimism"),
    SUI("Sui"),
    APT("Aptos"),
    ZK("zkSync"),
    ALT("Altlayer"),
    STRK("Starknet"),
    W("Wormhole"),
    MANTA("Manta Network"),
    POL("Polygon"),
    TIA("Celestia"),
    LINK("Chainlink"),
    TON("Toncoin"),
    AEVO("Aevo"),
    ATOM("Cosmos"),
    ENA("Ethena"),
    ZRO("LayerZero"),
    AVAX("Avalanche"),
    ICP("Internet Computer"),
    MANTLE("Mantle"),
    UNI("Uniswap"),
    DOT("Polkadot"),
    DYDX("dYdX"),
    NOT("Notcoin"),
    BTC("Bitcoin"),
    ETH("Ethereum"),
    SOL("Solana"),

    /*
        TODO:
            Add deposits
            Add export from one portfolio to another
            For transaction mention that it required extraction from deposit or not, will be necessary when exporting
    */

    // STABLECOINS
    USDT("Tether"),
    USDC("USD Coin"),
    DAI("Dai");

    private final String fullName;

    SymbolIndentifier(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }

    public static List<String> getAll() {
        return Arrays.stream(SymbolIndentifier.values()).map(Enum::name).toList();
    }

}
