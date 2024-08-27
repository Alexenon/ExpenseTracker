package com.example.application.data.models.crypto;

import com.example.application.entities.crypto.Asset;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CryptoTransaction {

    private Asset assetReceived;

    private Asset assetGiven;

    private double price;

    private double amount;

    private TransactionType type;

    private String comment;

    private LocalDateTime dateTime;

    public enum TransactionType {
        BUY,
        SELL
        // TRANSFER - Add asset amount from external sources
        // CONVERT  - Switch from one asset to another
    }

    public boolean isBuyTransaction() {
        return this.type == TransactionType.BUY;
    }

    public boolean isSellTransaction() {
        return this.type == TransactionType.SELL;
    }

}



