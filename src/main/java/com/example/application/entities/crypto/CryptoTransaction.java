package com.example.application.entities.crypto;

import com.example.application.entities.common.TransactionType;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

/*
* TODO [LONG TERM]:
*   - Add SpotPairs - BTC/USDT, BTC/USDC, ...
*   - TRANSFER - Add asset amount from external sources
    - CONVERT  - Switch from one asset to another
* */

@Data
@Entity(name = "crypto_transactions")
@NoArgsConstructor
public class CryptoTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @NotNull
    @Column(name = "market_price", nullable = false)
    @DecimalMin(value = "0.0", inclusive = false, message = "Market price must be greater than 0")
    private double marketPrice;

    @Column(name = "order_total_cost", nullable = false)
    @DecimalMin(value = "0.0", inclusive = false, message = "Order total cost must be greater than 0")
    private double orderTotalCost;

    @Column(name = "order_quantity", nullable = false)
    @DecimalMin(value = "0.0", inclusive = false, message = "Order quantity must be greater than 0")
    private double orderQuantity;

    @NotNull
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Nullable
    @Column(name = "notes", length = 250)
    private String notes;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime dateTime;

    public CryptoTransaction(Asset asset, double marketPrice, double orderTotalCost, TransactionType type) {
        this(asset, marketPrice, orderTotalCost, type, null, LocalDateTime.now());
    }

    public CryptoTransaction(Asset asset, double marketPrice, double orderTotalCost,
                             TransactionType type, String notes, LocalDateTime dateTime)
    {
        this.asset = asset;
        this.marketPrice = marketPrice;
        this.orderTotalCost = orderTotalCost;
        this.orderQuantity = orderTotalCost / marketPrice;
        this.type = type;
        this.notes = Objects.requireNonNullElse(notes, "");
        this.dateTime = dateTime;
    }

    public CryptoTransaction(CryptoTransaction transaction) {
        this.id = transaction.id;
        this.portfolio = transaction.portfolio;
        this.asset = transaction.asset;
        this.marketPrice = transaction.marketPrice;
        this.orderTotalCost = transaction.orderTotalCost;
        this.orderQuantity = transaction.orderQuantity;
        this.type = transaction.type;
        this.notes = transaction.notes;
        this.dateTime = transaction.dateTime;
    }

    public boolean isBuyTransaction() {
        return type.isBuyTransaction();
    }

    public boolean isSellTransaction() {
        return type.isSellTransaction();
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
               ", dateTime=" + dateTime +
               '}';
    }
}



