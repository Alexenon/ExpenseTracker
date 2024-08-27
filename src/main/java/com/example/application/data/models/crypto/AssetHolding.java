package com.example.application.data.models.crypto;

import com.example.application.entities.User;
import com.example.application.entities.crypto.Asset;
import jakarta.persistence.*;

import java.time.LocalDateTime;

//@Entity
public class AssetHolding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @Column(nullable = false, precision = 19, scale = 8)
    private double amount;

    @Column(precision = 19, scale = 6)
    private double averagePurchasePrice;

    @Column(nullable = false)
    private LocalDateTime lastUpdated;

    @Column(precision = 19, scale = 4)
    private double initialInvestment;

    @Transient
    private double currentValue;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 500)
    private String comment;

    // Constructors, getters, setters, and other methods...
}
