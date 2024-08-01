package com.example.application.data.models.watcher;

public class PriceWatcher {
    private final String currencyName;
    private final PriceTarget priceTarget;

    public PriceWatcher(String currencyName, PriceTarget priceTarget) {
        this.currencyName = currencyName;
        this.priceTarget = priceTarget;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public double getTarget() {
        return priceTarget.getTarget();
    }

    public double getTargetAmount() {
        return priceTarget.getTargetAmount();
    }

    public PriceTarget.Type getType() {
        return priceTarget.getType();
    }

}
