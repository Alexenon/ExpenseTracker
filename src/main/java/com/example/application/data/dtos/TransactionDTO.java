package com.example.application.data.dtos;

import com.example.application.entities.common.TransactionType;
import com.example.application.entities.crypto.Transaction;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransactionDTO {

	private final Long id;
	private Long portfolioId;
	private String assetSymbol;
	private double marketPrice;
	private double orderQuantity;
	private double avgBuyPriceAtMoment;
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

	public double getOrderTotalCost() {
		return marketPrice * orderQuantity;
	}

	public boolean isBuyTransaction() {
		return type.isBuyTransaction();
	}

	public boolean isSellTransaction() {
		return type.isSellTransaction();
	}


}
