package com.example.application.data.models.crypto.common;

import com.example.application.entities.common.TransactionType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionInput {

	private BigDecimal amount;
	private BigDecimal price;
	private TransactionType type;

	public BigDecimal getTotalCost() {
		return amount.multiply(price);
	}

}
