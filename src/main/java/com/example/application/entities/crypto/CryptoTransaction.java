package com.example.application.entities.crypto;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Objects;

/*
* TODO:
*   - Add SpotPairs - BTC/USDT, BTC/USDC, ...
*   - TRANSFER - Add asset amount from external sources
    - CONVERT  - Switch from one asset to another
* */

@Data
@Entity
@NoArgsConstructor
public class CryptoTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @ManyToOne
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @Column(name = "market_price", nullable = false)
    @DecimalMin(value = "0.0", inclusive = false, message = "Market price must be greater than 0")
    private double marketPrice;

    @Column(name = "order_total_cost", nullable = false)
    @DecimalMin(value = "0.0", inclusive = false, message = "Order total cost must be greater than 0")
    private double orderTotalCost;

    @Column(name = "order_quantity", nullable = false)
    @DecimalMin(value = "0.0", inclusive = false, message = "Order quantity must be greater than 0")
    private double orderQuantity;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Column(name = "notes", length = 250)
    private String notes;

    @Column(nullable = false)
    private LocalDate date;

    public CryptoTransaction(Asset asset, double marketPrice, double orderTotalCost, TransactionType type) {
        this(asset, marketPrice, orderTotalCost, type, null, LocalDate.now());
    }

    public CryptoTransaction(Asset asset, double marketPrice, double orderTotalCost,
                             TransactionType type, String notes, LocalDate date) {
        this.asset = asset;
        this.marketPrice = marketPrice;
        this.orderTotalCost = orderTotalCost;
        this.orderQuantity = orderTotalCost / marketPrice;
        this.type = type;
        this.notes = Objects.requireNonNullElse(notes, "");
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
    }

    @Override
    public String toString() {
        return "CryptoTransaction{" +
               "id=" + id +
               ", asset=" + asset +
               ", marketPrice=" + marketPrice +
               ", orderTotalCost=" + orderTotalCost +
               ", orderQuantity=" + orderQuantity +
               ", type=" + type +
               ", notes='" + notes + '\'' +
               ", date=" + date +
               '}';
    }
}



