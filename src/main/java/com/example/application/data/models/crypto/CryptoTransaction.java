package com.example.application.data.models.crypto;

import com.example.application.entities.crypto.Asset;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/*
* TODO:
*   - Add SpotPairs - BTC/USDT, BTC/USDC, ...
* */

@Data
public class CryptoTransaction {

    private Asset asset;

    private double marketPrice;

    private double orderTotalPrice;

    private double orderQuantity;

    private TransactionType type;

    private String comment;

    private LocalDate date;

    public CryptoTransaction() {
    }

    public CryptoTransaction(Asset asset, double marketPrice, double orderTotalPrice, TransactionType type) {
        this(asset, marketPrice, orderTotalPrice, type, null, LocalDate.now());
    }

    public CryptoTransaction(Asset asset, double marketPrice, double orderTotalPrice,
                             TransactionType type, String comment, LocalDate date) {
        this.asset = asset;
        this.marketPrice = marketPrice;
        this.orderTotalPrice = orderTotalPrice;
        this.orderQuantity = orderTotalPrice / marketPrice;
        this.type = type;
        this.comment = comment;
        this.date = date;
    }

    public boolean isBuyTransaction() {
        return this.type == TransactionType.BUY;
    }

    public boolean isSellTransaction() {
        return this.type == TransactionType.SELL;
    }

    public enum TransactionType {
        BUY,
        SELL
        // TRANSFER - Add asset amount from external sources
        // CONVERT  - Switch from one asset to another
    }

}



