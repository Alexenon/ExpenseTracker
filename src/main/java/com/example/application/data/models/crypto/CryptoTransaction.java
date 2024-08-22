package com.example.application.data.models.crypto;

import com.example.application.entities.crypto.Asset;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CryptoTransaction {

    private Asset asset;
    private double price;
    private double amount;
    private AmountType amountType;
    private TransactionType type;
    private LocalDateTime dateTime;

    public enum AmountType {
        TOKEN, USD
    }

    public enum TransactionType {
        BUY,
        SELL
    }

    public boolean isBuyTransaction() {
        return this.type == TransactionType.BUY;
    }

    public boolean isSellTransaction() {
        return this.type == TransactionType.SELL;
    }

}



