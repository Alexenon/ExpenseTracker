package com.example.application.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private long id;

    @NotNull
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
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @Getter
    @Setter
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "timestamp_id")
    @Getter
    @Setter
    private Timestamp timestamp;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "category_id")
    @Getter
    @Setter
    private Category category;

    @Override
    public String toString() {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return String.format(
                "%d, %s, %f, %s, %s, %s, %s, %s",
                id, name, amount, timestamp.getName(), category.getName(),
                dateFormat.format(date), description, dateFormat.format(expiryDate)
        );
    }

}

