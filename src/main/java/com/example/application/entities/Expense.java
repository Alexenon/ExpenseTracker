package com.example.application.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "amount", nullable = false)
    private double amount;

    @Column(name = "description")
    private String description;

    @Column(name = "start_date", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate = LocalDate.now();

    @Column(name = "expire_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expireDate;

    @Column(name = "timestamp", unique = true, nullable = false)
    @Enumerated(EnumType.STRING)
    private Timestamp timestamp = Timestamp.ONCE;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Override
    public String toString() {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return String.format(
                "Expense{%d, %s, %f, %s, %s, %s, %s, %s %s}",
                id, name, amount, timestamp, category.getName(),
                dateFormat.format(startDate),
                (expireDate != null) ? dateFormat.format(expireDate) : "N/A",
                description, user.getUsername()
        );
    }

    public enum Timestamp {
        ONCE,
        DAILY,
        WEEKLY,
        MONTHLY,
        YEARLY;

        public static List<String> getTimestampNames() {
            return Arrays.stream(values()).map(Timestamp::name).toList();
        }
    }

}

