package com.example.application.utils.investment;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Deposit calculator, to find what's the avgBuyPrice and fees paid for all P2P transactions
 * */
public class DepositsCalculator {

    public static void main(String[] args) {
        // Here you place deposits from same day, but with different P2P amount and fees
        List<Deposit> deposits = List.of(
                create(942, 16.33, FeeCalculator.NON_MOLDINCOMBANK_INTERNAL),
                create(236, 16.99, FeeCalculator.NONE)
        );
        printAverage(deposits);
    }

    private static Deposit create(double usdAmount, double buyPrice, FeeCalculator feeCalculator) {
        return Deposit.builder()
                .amount(usdAmount)
                .buyPrice(buyPrice)
                .fee(feeCalculator.calculateFee(usdAmount * buyPrice))
                .build();
    }

    private static void printAverage(List<Deposit> deposits) {
        double totalAmount = deposits.stream().mapToDouble(Deposit::getAmount).sum();
        double totalFee = deposits.stream().mapToDouble(Deposit::getFee).sum();
        double avgBuyPrice = deposits.stream().mapToDouble(Deposit::getBuyPrice).average().orElse(0.0);

        System.out.printf("""
                Amount: $%.2f
                Avg buy: $%.2f
                Fee: %.2f MDL
                """, totalAmount, avgBuyPrice, totalFee);
    }

    @Data
    @Builder
    static class Deposit {
        double amount;
        double buyPrice;
        double fee;
    }
}
