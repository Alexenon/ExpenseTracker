package com.example.application.data.requests;

import com.example.application.entities.common.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class CreateTransactionRequest {

	private String assetSymbol;
	private Long portfolioId;
	private BigDecimal marketPrice;
	private BigDecimal orderQuantity;
	private TransactionType type = TransactionType.BUY;
	private String note;
	private LocalDateTime dateTime = LocalDateTime.now();

	public CreateTransactionRequest() {
	}

	private CreateTransactionRequest(String assetSymbol, Long portfolioId, BigDecimal marketPrice, BigDecimal orderQuantity, TransactionType type, String note, LocalDateTime dateTime) {
		this.assetSymbol = assetSymbol;
		this.portfolioId = portfolioId;
		this.marketPrice = marketPrice;
		this.orderQuantity = orderQuantity;
		this.type = type;
		this.note = note;
		this.dateTime = dateTime;
	}

	public BigDecimal getOrderTotalCost() {
		return orderQuantity.multiply(marketPrice);
	}

}