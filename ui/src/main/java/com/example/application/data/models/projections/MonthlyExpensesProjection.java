package com.example.application.data.models.projections;

import java.time.LocalDate;

public interface MonthlyExpensesProjection {
    String getName();

    String getDescription();

    String getCategoryName();

    String getTimestamp();

    Double getAmount();

    LocalDate getStartDate();

    LocalDate getExpireDate();

    Integer getDaysPassed();

    LocalDate getEndDate();

    Integer getTimesTriggered();
}
