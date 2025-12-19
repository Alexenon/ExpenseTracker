package com.example.application.utils.investment;

import lombok.Builder;

@Builder
public class FeeCalculator {

    public static FeeCalculator NONE = FeeCalculator.builder().build();

    public static FeeCalculator MOLDINCOMBANK = FeeCalculator.builder()
            .feePercentage(0)
            .feeAmount(0)
            .build();

    public static FeeCalculator NON_MOLDINCOMBANK_INTERNAL = FeeCalculator.builder()
            .feePercentage(1)
            .feeAmount(20)
            .build();

    public static FeeCalculator NON_MOLDINCOMBANK_EXTERNAL = FeeCalculator.builder()
            .feePercentage(1.5)
            .feeAmount(20)
            .build();

    private double transferAmount;
    private double feePercentage;
    private double feeAmount;

    public static void main(String[] args) {
        double sumToTransfer = 4956;      // MDL

        // MOLDINCOMBANK
        double rateForSameCard = 17;
        double feeMoldincombank = MOLDINCOMBANK.calculateFee(sumToTransfer);

        // VICTORIABANK
        double rateForOtherCard = 16.80;
        double feeOtherCard = NON_MOLDINCOMBANK_INTERNAL.calculateFee(sumToTransfer);

        printResults(sumToTransfer, rateForOtherCard, feeOtherCard);
        printResults(sumToTransfer, rateForSameCard, feeMoldincombank);
    }

    private static void printResults(double transferAmount, double rate, double feeAmount) {
        transferAmount -= feeAmount;
        double boughtAmountUSDT = transferAmount / rate;
        double feeInUsd = feeAmount / rate;

        System.out.printf("""
                Buying with %.2f at price of %.2f per USDT
                    -> fee: %.2f MDL ~ $%.2f
                    -> bought: %.2f USDT
                    _______________________________________
                    -> without fee: %.2f USDT
                """, transferAmount, rate, feeAmount, feeInUsd, boughtAmountUSDT, boughtAmountUSDT + feeInUsd);
        System.out.println();
    }

    public double calculateFee(double transferAmount) {
        return transferAmount * feePercentage / 100 + feeAmount;
    }

}
