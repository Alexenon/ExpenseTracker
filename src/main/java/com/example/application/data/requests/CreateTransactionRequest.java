package com.example.application.data.requests;

import com.example.application.entities.common.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CreateTransactionRequest {

	private String assetSymbol;
	private Long portfolioId;
	private double marketPrice;
	private double orderQuantity;
	private TransactionType type;
	private String note;
	private LocalDateTime dateTime;

	public CreateTransactionRequest() {
	}

	private CreateTransactionRequest(String assetSymbol, Long portfolioId, double marketPrice, double orderQuantity, TransactionType type, String note, LocalDateTime dateTime) {
		this.assetSymbol = assetSymbol;
		this.portfolioId = portfolioId;
		this.marketPrice = marketPrice;
		this.orderQuantity = orderQuantity;
		this.type = type;
		this.note = note;
		this.dateTime = dateTime;
	}

	public double getOrderTotalCost() {
		return orderQuantity * marketPrice;
	}

}