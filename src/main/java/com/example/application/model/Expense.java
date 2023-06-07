package com.example.application.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private long id;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private double amount;

    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    private Date date;

    @ManyToOne
    @JoinColumn(name = "timestamp_id")
    @Getter
    @Setter
    private Timestamp timestamp;

    @Override
    public String toString() {
        return String.format("%d, %s, %f, %s, %s, %s", id, name, amount, timestamp.getName(), description, date);
    }
}

