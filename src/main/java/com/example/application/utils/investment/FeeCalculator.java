package com.example.application.utils.investment;

import com.example.application.utils.investment.models.BankTransfer;
import com.example.application.utils.investment.models.P2P_Transfer;

public class FeeCalculator {

	public static void main(String[] args) {
		double sumToTransferMDL = 1500;

		P2P_Transfer first = P2P_Transfer.builder()
				.transferAmount(sumToTransferMDL)
				.rate(17.50)
				.bank(BankTransfer.MIA)
				.build();

		P2P_Transfer second = P2P_Transfer.builder()
				.transferAmount(sumToTransferMDL)
				.rate(17.05)
				.bank(BankTransfer.DIFFERENT_INTERNAL_CARD)
				.build();

		compare(first, second);
	}

	private static void compare(P2P_Transfer first, P2P_Transfer second) {
		double firstTransferAmount = first.getTransferAmount();
		double firstFee = first.getBank().calculateFee(firstTransferAmount);
		double firstRate = first.getRate();

		double secondTransferAmount = second.getTransferAmount();
		double secondFee = second.getBank().calculateFee(secondTransferAmount);
		double secondRate = second.getRate();

		double boughtAmount1 = printResults(first.getBank().getName(), firstTransferAmount, firstRate, firstFee);
		double boughtAmount2 = printResults(second.getBank().getName(), secondTransferAmount, secondRate, secondFee);

		System.out.println();
		System.out.printf("""
						Results:
							-> For rate %.2f (fee: %.2f MDL) = %.2f USDT
							-> For rate %.2f (fee: %.2f MDL) = %.2f USDT
							-------------------------------------------
							Difference: %.2f USDT
						""",
				firstRate, firstFee, boughtAmount1,
				secondRate, secondFee, boughtAmount2,
				Math.abs(boughtAmount1 - boughtAmount2)
		);
	}


	private static double printResults(String name, double transferAmount, double rate, double feeAmount) {
		transferAmount -= feeAmount;
		double boughtAmountUSDT = transferAmount / rate;
		double feeInUsd = feeAmount / rate;
		double amountWithoutFee = boughtAmountUSDT + feeInUsd;

		System.out.printf("""
				Buying with %.2f MDL at %s
					-> price: %.2f per USDT
				    -> fee: %.2f MDL ~ $%.2f
				    -> without fee: %.2f USDT
				    _______________________________________
				    -> bought: %.2f USDT
				""", transferAmount, name, rate, feeAmount, feeInUsd, amountWithoutFee, boughtAmountUSDT);
		System.out.println();

		return boughtAmountUSDT;
	}

}
