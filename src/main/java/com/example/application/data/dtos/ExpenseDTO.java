package com.example.application.data.dtos;

import com.example.application.entities.Expense;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public interface ExpenseDTO {
    long getId();

    String getName();

    double getAmount();

    String getCategory();

    String getDescription();

    Expense.Timestamp getTimestamp();

    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate getStartDate();

    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate getExpireDate();

    String getUser();
}
