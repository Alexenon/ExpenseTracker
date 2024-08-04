package com.example.application.entities.crypto;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    private String symbol;

    @NotNull
    private double amount;

    private String comment;

    @NotNull
    private boolean markedAsFavorite;

    @NotNull
    private LocalDateTime lastTimeUpdated;

    public Asset(String symbol) {
        this(0, symbol, 0.0, null, false, LocalDateTime.now());
    }

    public Asset(long id, String symbol, double amount, String comment, boolean markedAsFavorite, LocalDateTime lastTimeUpdated) {
        this.id = id;
        this.symbol = symbol;
        this.amount = amount;
        this.comment = comment;
        this.markedAsFavorite = markedAsFavorite;
        this.lastTimeUpdated = lastTimeUpdated;
    }
}
