package com.example.application.transaction;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionDTO {

	private final Long id;
	private Long portfolioId;
	private String assetSymbol;
	private BigDecimal marketPrice;
	private BigDecimal orderQuantity;
	private BigDecimal avgBuyPriceAtMoment;
	private TransactionType type;
	private String note;
	private LocalDateTime dateTime;

	public TransactionDTO() {
		this.id = null;
	}

	public TransactionDTO(Transaction transaction) {
		id = transaction.getId();
		portfolioId = transaction.getPortfolio().getId();
		assetSymbol = transaction.getAsset().getSymbol();
		orderQuantity = transaction.getOrderQuantity();
		marketPrice = transaction.getMarketPrice();
		avgBuyPriceAtMoment = transaction.getAvgBuyPriceAtMoment();
		type = transaction.getType();
		note = transaction.getNote();
		dateTime = transaction.getDateTime();
	}

	public BigDecimal getOrderTotalCost() {
		return orderQuantity.multiply(marketPrice);
	}

	public boolean isBuyTransaction() {
		return type.isBuyTransaction();
	}

	public boolean isSellTransaction() {
		return type.isSellTransaction();
	}

}
