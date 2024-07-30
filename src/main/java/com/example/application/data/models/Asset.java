package com.example.application.data.models;

import com.example.application.utils.responses.AssetData;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class Asset {

    @Getter
    private final String symbol;

    @Getter
    private List<Holding> holdings;

    @Setter
    @Getter
    private String comment;

    @Setter
    @Getter
    private boolean markedAsFavorite;

    private AssetData assetData;

    public Asset(String symbol) {
        this(symbol, new ArrayList<>(), "");
    }

    public Asset(String symbol, List<Holding> holdings, String comment) {
        this.symbol = symbol;
        this.holdings = holdings;
        this.comment = comment;
        this.markedAsFavorite = false;
    }

    public double getAveragePrice() {
//        return holdings.stream()
//                .map(Holding::getPriceBought)
//                .mapToDouble(BigDecimal::doubleValue)
//                .average()
//                .orElse(0.0);
        return 0;
    }

    public double getProfit() {
        return 0.0;
    }

    public double getTotalAmount() {
        return holdings.stream()
                .mapToDouble(Holding::getAmount)
                .sum();
    }

    public void addHolding(Holding holding) {
        holdings.add(holding);
    }

    public void removeHolding(Holding holding) {
        holdings.remove(holding);
    }

    public AssetData getAssetData() {
        if (assetData == null) {
            assetData = InstrumentsProvider.getInstance().getCoinData(symbol);
        }
        return assetData;
    }

}
