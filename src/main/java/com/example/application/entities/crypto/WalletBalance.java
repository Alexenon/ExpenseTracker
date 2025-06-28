package com.example.application.entities.crypto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity(name = "wallet_balances")
@NoArgsConstructor
@AllArgsConstructor
public class WalletBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @ManyToOne
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @Column(name = "comment")
    private String comment;

    @Column(name = "marked_as_favorite", nullable = false)
    private boolean markedAsFavorite = false;

    @Column(name = "amount", nullable = false)
    private double amount = 0.0;

    @Column(name = "avg_buy_price", nullable = false)
    private double avgBuyPrice = 0.0;

    @Column(name = "avg_sell_price", nullable = false)
    private double avgSellPrice = 0.0;

    @Column(name = "cost", nullable = false)
    private double cost = 0.0;

//    @Column(name = "total_invested", precision = 19, scale = 4, nullable = false)
//    private double totalInvested = 0.0;

//    @Column(name = "total_realized", precision = 19, scale = 4, nullable = false)
//    private double totalRealized = 0.0;

    @Column(name = "last_time_updated", nullable = false)
    private LocalDateTime lastTimeUpdated = LocalDateTime.now();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

}
