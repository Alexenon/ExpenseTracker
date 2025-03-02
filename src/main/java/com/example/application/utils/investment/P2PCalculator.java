package com.example.application.utils.investment;

import lombok.Data;

import java.util.Scanner;

public class P2PCalculator {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.print("Introduceti suma transferului: ");
        double transferAmount = scanner.nextDouble();

        String options = """
                1. Moldincombank
                2. Other Moldova card
                3. Non Moldova card
                4. MIA without fee
                5. MIA with 5 Lei fee
                """;
        System.out.println(options);

        while (true) {
            Card card = displayOptions();
            System.out.print("Introduceti rata USD sub care cumparati: ");
            double rate = scanner.nextDouble();

            printResults(transferAmount, rate, card.calculateFee(transferAmount));
        }
    }

    private static Card displayOptions() {
        System.out.print("Introduceti cardul caruia doriti sa efectuati transferul: ");

        return switch (scanner.nextInt()) {
            case 1 -> new Card("Moldincombank", 0, 0);
            case 2 -> new Card("Other Moldova card", 1, 20);
            case 3 -> new Card("Non Moldova card", 1.5, 30);
            case 4 -> new Card("MIA no fee", 0, 0);
            case 5 -> new Card("MIA 5 Lei fee", 0, 5);
            default -> throw new IllegalArgumentException();
        };
    }

    private static void printResults(double transferAmount, double rate, double feeAmount) {
        double remainingAmount = transferAmount - feeAmount;
        double boughtAmountUSDT = remainingAmount / rate;

        String s = """
                    Total amount: %.2f lei
                    Price per USDT: %.2f lei
                    Bought: %.2f USDT
                    Fee: %.2f lei
                """.formatted(transferAmount, rate, boughtAmountUSDT, feeAmount);

        System.out.println("\n" + s);
    }

    @Data
    static class Card {
        private String name;
        private double feePercentage;
        private double feeAmount;

        public Card(String name, double feePercentage, double feeAmount) {
            this.name = name;
            this.feePercentage = feePercentage;
            this.feeAmount = feeAmount;
        }

        public double calculateFee(double transferAmount) {
            return transferAmount * feePercentage / 100 + feeAmount;
        }

    }


}
