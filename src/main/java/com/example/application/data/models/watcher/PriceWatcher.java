package com.example.application.data.models.watcher;

public class PriceWatcher {
    private final String currencyName;
    private final PriceTarget priceTarget;
    private boolean isCompleted;

    public PriceWatcher(String currencyName, PriceTarget priceTarget) {
        this.currencyName = currencyName;
        this.priceTarget = priceTarget;
        this.isCompleted = false;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public double getAmount() {
        return priceTarget.getAmount();
    }

    public double getTargetAmount() {
        return priceTarget.getTarget();
    }

    public PriceTarget.Type getType() {
        return priceTarget.getType();
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
