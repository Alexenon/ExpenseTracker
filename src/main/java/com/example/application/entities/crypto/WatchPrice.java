package com.example.application.entities.crypto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "watch_price")
@NoArgsConstructor
@AllArgsConstructor
public class WatchPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private long id;

    @Getter
    @Setter
    @Column(nullable = false)
    private double price;

    public WatchPrice(double price) {
        this.price = price;
    }
}
