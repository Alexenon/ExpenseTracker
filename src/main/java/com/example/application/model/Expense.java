package com.example.application.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "timestamp_id")
    @Getter
    @Setter
    private Timestamp timestamp;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @Getter
    @Setter
    private Timestamp category;

    @Override
    public String toString() {
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return String.format("%d, %s, %f, %s, %s, %s, %s",
                id, name, amount, timestamp.getName(),
                category.getName(), description, dateFormatter.format(date));
    }

}

