package com.example.application.transaction;

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

