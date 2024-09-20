package com.example.application.entities.crypto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String symbol;

    @Column(name = "comment")
    private String comment;

    @Column(nullable = false)
    private boolean markedAsFavorite;

    @Column(nullable = false)
    private LocalDateTime lastTimeUpdated = LocalDateTime.now();

    public Asset(String symbol) {
        this(0, symbol, "", false, LocalDateTime.now());
    }

}

