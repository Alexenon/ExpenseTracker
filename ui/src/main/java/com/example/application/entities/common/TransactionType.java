package com.example.application.entities.common;

public enum TransactionType {
	BUY,
	SELL;

	public boolean isBuyTransaction() {
		return this == TransactionType.BUY;
	}

	public boolean isSellTransaction() {
		return this == TransactionType.SELL;
	}

}

