package com.example.application.data.models.watcher;

import lombok.Data;

@Data
public class PriceTarget {
    private double amount;
    private double target;
    private Type type;

    /**
     * @param target represents the price target or percentage target of the asset price
     */
    public PriceTarget(double amount, Type type, double target) {
        this.amount = amount;
        this.type = type;
        this.target = target;
    }

    public enum Type {
        PRICE, PERCENTAGE
    }
}