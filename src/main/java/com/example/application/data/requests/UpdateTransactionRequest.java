package com.example.application.data.requests;

import com.example.application.data.dtos.TransactionDTO;
import com.example.application.entities.common.TransactionType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UpdateTransactionRequest {

	private final Long id;
	private String assetSymbol;
	private BigDecimal marketPrice;
	private BigDecimal orderQuantity;
	private TransactionType type;
	private String note;
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