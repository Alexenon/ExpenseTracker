package com.example.application.utils.investment;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FeeCalculator {
    private double transferAmount;
    private double feePercentage;
    private double feeAmount;

    public static void main(String[] args) {
        double rateForOtherCard = 18;
        double rateForSameCard = 18.2;
        double sumToTransfer = 4000;

        double transferOtherCardFee = FeeCalculator.builder()
                .withTransferAmount(sumToTransfer)
                .withFeePercentage(1)
                .withFeeAmount(20)
                .build()
                .calculateFee();

        double rateDifference = sumToTransfer * (rateForSameCard - rateForOtherCard);

        System.out.printf("Sending %.2f at rate of %.2f will cost additional %.2f\n",
                sumToTransfer, rateForOtherCard, transferOtherCardFee);
        System.out.printf("Sending %.2f at rate of %.2f will cost additional %.2f\n",
                sumToTransfer, rateForSameCard, 0.0);
        System.out.printf("rate: %.2f", rateDifference);
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
    static class Builder {
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
