package com.example.application.data.models.crypto;

import com.example.application.entities.crypto.Asset;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "Wallet_Balances")
public class WalletBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long balanceId;

    @ManyToOne
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @ManyToOne
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @Column(nullable = false)
    private double amount = 0.0;

    @Column(nullable = false, updatable = false)
    private LocalDate createdAt = LocalDate.now();

}
