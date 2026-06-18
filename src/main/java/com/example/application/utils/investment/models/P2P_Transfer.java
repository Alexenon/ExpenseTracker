package com.example.application.utils.investment.models;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class P2P_Transfer {

	private double transferAmount;
	private double rate;
	private BankTransfer bank;

}
