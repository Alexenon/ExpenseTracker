package com.example.application.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public interface ExpenseDTO {
    long getId();

    String getName();

    double getAmount();

    String getCategory();

    String getDescription();

    String getTimestamp();

    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate getDate();

    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate getExpireDate();

}
