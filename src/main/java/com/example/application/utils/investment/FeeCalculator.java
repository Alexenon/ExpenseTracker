package com.example.application.utils.investment;

import lombok.AllArgsConstructor;
import lombok.Data;

@SuppressWarnings("ClassEscapesDefinedScope")
@Data
@AllArgsConstructor
public class FeeCalculator {
    private double transferAmount;
    private double feePercentage;
    private double feeAmount;

    public static void main(String[] args) {
        double sumToTransfer = 1200;      // MDL
        double rateForOtherCard = 17.00;  // VICTORIABANK
        double rateForSameCard = 17.30;   // MOLDINCOMBANK

        double transferOtherCardFee = FeeCalculator.builder()
                .withTransferAmount(sumToTransfer)
                .withFeePercentage(1)
                .withFeeAmount(20)
                .build()
                .calculateFee();

        printResults(sumToTransfer, rateForOtherCard, transferOtherCardFee);
        printResults(sumToTransfer, rateForSameCard, 0.0);
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

    public static Builder builder() {
        return new Builder();
    }

    public double calculateFee() {
        return transferAmount * feePercentage / 100 + feeAmount;
    }

    public void print() {
        System.out.printf("%s with fee: $%s\n", this, calculateFee());
    }

    /*
     * Fee Calculator Builder
     * */
    private static class Builder {
        private final FeeCalculator instance;

        public Builder() {
            this.instance = new FeeCalculator(0, 0, 0);
        }

        public Builder withTransferAmount(double transferAmount) {
            instance.setTransferAmount(transferAmount);
            return this;
        }

        public Builder withFeePercentage(double feePercentage) {
            instance.setFeePercentage(feePercentage);
            return this;
        }

        public Builder withFeeAmount(double feeAmount) {
            instance.setFeeAmount(feeAmount);
            return this;
        }

        public FeeCalculator build() {
            return instance;
        }

    }

}
