package com.example.application.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MarketSimulator {

    private final Random random = new Random();
    private final double minPercentageStep;
    private final double maxPercentageStep;
    private final int size;

    // ✅ Private constructor used by the builder
    private MarketSimulator(Builder builder) {
        this.minPercentageStep = builder.minPercentageStep;
        this.maxPercentageStep = builder.maxPercentageStep;
        this.size = builder.size;
    }

    /*
     * Random walk algorithm
     */
    public List<Double> generate(double price) {
        List<Double> results = new ArrayList<>(size + 1);
        results.add(price);
        for (int i = 0; i < size; i++) {
            double direction = random.nextBoolean() ? 1 : -1;
            double stepSize = random.nextDouble(minPercentageStep, maxPercentageStep);
            double step = percentageOf(stepSize, price);
            price += step * direction;
            results.add(price);
            System.out.println(price);
        }
        return results;
    }

    private static double percentageOf(double smallPart, double whole) {
        return (smallPart / 100.0) * whole;
    }

    //<editor-fold desc="Builder">
    public static class Builder {
        private double minPercentageStep = 1;
        private double maxPercentageStep = 10;
        private int size = 100;

        public Builder minPercentageStep(double minPercentageStep) {
            if(minPercentageStep < 0 || maxPercentageStep > 100) {
                throw new IllegalArgumentException("minPercentageStep should be between 0 and 100");
            }
            this.minPercentageStep = minPercentageStep;
            return this;
        }

        public Builder maxPercentageStep(double maxPercentageStep) {
            if(maxPercentageStep < 0 || maxPercentageStep > 100) {
                throw new IllegalArgumentException("minPercentageStep should be between 0 and 100");
            }
            this.maxPercentageStep = maxPercentageStep;
            return this;
        }

        public Builder size(int size) {
            if(size <= 0) {
                throw new IllegalArgumentException("Size should be bigger than 0");
            }
            this.size = size;
            return this;
        }

        public MarketSimulator build() {
            if (minPercentageStep >= maxPercentageStep) {
                throw new IllegalArgumentException("minPercentageStep must be less than maxPercentageStep");
            }

            return new MarketSimulator(this);
        }
    }
    //</editor-fold>

    public static void main(String[] args) {
        MarketSimulator simulator = new MarketSimulator.Builder()
                .minPercentageStep(0.5)
                .maxPercentageStep(2)
                .size(200)
                .build();

        List<Double> prices = simulator.generate(3400.59);
        SimpleChart.showChart(prices);
    }
}
