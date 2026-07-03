package com.example.application.utils.investment.models;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BankTransfer {

	public static BankTransfer MIA = BankTransfer.builder()
			.name("No fee - MIA")
			.feePercentage(0)
			.feeAmount(0)
			.build();

	public static BankTransfer SAME_BANK = BankTransfer.builder()
			.name("Same internal card - MOLDINCOMBANK")
			.feePercentage(0)
			.feeAmount(0)
			.build();

	public static BankTransfer DIFFERENT_INTERNAL_CARD = BankTransfer.builder()
			.name("Different internal card - VICTORIABANK")
			.feePercentage(1)
			.feeAmount(20)
			.build();

	public static BankTransfer EXTERNAL_CARD = BankTransfer.builder()
			.name("External card - REVOLUT")
			.feePercentage(1.5)
			.feeAmount(20)
			.build();

	private String name;
	private double feePercentage;
	private double feeAmount;

	public double calculateFee(double transferAmount) {
		return transferAmount * feePercentage / 100 + feeAmount;
	}

}
