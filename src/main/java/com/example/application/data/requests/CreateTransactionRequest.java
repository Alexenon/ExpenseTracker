package com.example.application.data.requests;

import com.example.application.entities.common.TransactionType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateTransactionRequest {

	private String assetSymbol;
	private Long portfolioId;
	private double marketPrice;
	private double orderQuantity;
	private TransactionType type;
	private String note;
	private LocalDateTime dateTime;

	public double getOrderTotalCost() {
		return orderQuantity * marketPrice;
	}

}