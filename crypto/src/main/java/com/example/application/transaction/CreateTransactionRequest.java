package com.example.application.transaction;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class CreateTransactionRequest {

	@NotBlank
	private String assetSymbol;

	@NotNull
	private Long portfolioId;

	@NotNull
	@DecimalMin(value = "0.0", inclusive = false, message = "Market price must be greater than 0")
	private BigDecimal marketPrice;

	@NotNull
	@DecimalMin(value = "0.0", inclusive = false, message = "Quantity must be greater than 0")
	private BigDecimal orderQuantity;

	@NotNull
	private TransactionType type = TransactionType.BUY;

	@Nullable
	private String note;

	@NotNull
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