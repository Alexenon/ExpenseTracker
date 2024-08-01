package com.example.application.data.models.watcher;

import lombok.Data;

@Data
public class PriceTarget {

    private double target;
    private double targetAmount;
    private Type type;
    private boolean isCompleted;

    public PriceTarget(double target, double targetAmount) {
        this(target, targetAmount, Type.PRICE, false);
    }

    public PriceTarget(double target, double targetAmount, Type type) {
        this(target, targetAmount, type, false);
    }

    /**
     * @param target       represents the price target or percentage target of the asset price
     * @param targetAmount represents the amount to invest when the target is reached
     * @param type         represents the type of the target - price / percentage
     * @param isCompleted  represents if the target was achieved
     */
    public PriceTarget(double target, double targetAmount, Type type, boolean isCompleted) {
        this.targetAmount = targetAmount;
        this.type = type;
        this.target = target;
        this.isCompleted = isCompleted;
    }

    public enum Type {
        PRICE, PERCENTAGE
    }
}