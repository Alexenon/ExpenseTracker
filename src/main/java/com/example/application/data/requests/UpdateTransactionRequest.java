package com.example.application.data.requests;

import com.example.application.data.dtos.TransactionDTO;
import com.example.application.entities.common.TransactionType;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UpdateTransactionRequest {

	@NotNull
	@Min(value = 1, message = "Invalid id")
	private final Long id;

	@NotBlank
	private String assetSymbol;

	@NotNull
	@DecimalMin(value = "0.0", inclusive = false, message = "Market price must be greater than 0")
	private BigDecimal marketPrice;

	@NotNull
	@DecimalMin(value = "0.0", inclusive = false, message = "Quantity must be greater than 0")
	private BigDecimal orderQuantity;

	@NotNull
	private TransactionType type;

	@Nullable
	private String note;

	@NotNull
	private LocalDateTime dateTime;

	public UpdateTransactionRequest(TransactionDTO dto) {
		this.id = dto.getId();
		this.assetSymbol = dto.getAssetSymbol();
		this.marketPrice = dto.getMarketPrice();
		this.orderQuantity = dto.getOrderQuantity();
		this.type = dto.getType();
		this.note = dto.getNote();
		this.dateTime = dto.getDateTime();
	}

	public UpdateTransactionRequest(UpdateTransactionRequest request) {
		this.id = request.getId();
		this.assetSymbol = request.getAssetSymbol();
		this.marketPrice = request.getMarketPrice();
		this.orderQuantity = request.getOrderQuantity();
		this.type = request.getType();
		this.note = request.getNote();
		this.dateTime = request.getDateTime();
	}

	public BigDecimal getOrderTotalCost() {
		return orderQuantity.multiply(marketPrice);
	}

}