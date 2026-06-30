package com.example.application.utils.investment;

import com.example.application.utils.investment.models.BankTransfer;
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
                create(942, 16.33, BankTransfer.SAME_BANK),
                create(236, 16.99, BankTransfer.MIA)
        );
        printAverage(deposits);
    }

    private static Deposit create(double amount, double buyPrice, BankTransfer bankTransfer) {
        return Deposit.builder()
                .amount(usdAmount)
                .buyPrice(buyPrice)
                .fee(bankTransfer.calculateFee(amount * buyPrice))
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
