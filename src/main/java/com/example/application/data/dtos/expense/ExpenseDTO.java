package com.example.application.data.dtos.expense;

import com.example.application.entities.expenses.ExpenseTimestamp;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public interface ExpenseDTO {
    long getId();

    String getName();

    double getAmount();

    String getCategory();

    String getDescription();

    ExpenseTimestamp getTimestamp();

    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate getStartDate();

    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate getExpireDate();

    String getUser();
}
