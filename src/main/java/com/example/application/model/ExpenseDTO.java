package com.example.application.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public interface ExpenseDTO {
    long getId();

    String getName();

    double getAmount();

    String getCategory();

    String getDescription();

    String getTimestamp();

    LocalDateTime getDate();

    default String formatDate(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }
}
