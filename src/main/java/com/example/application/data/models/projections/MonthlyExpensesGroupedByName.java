package com.example.application.data.models.projections;

public interface MonthlyExpensesGroupedByName {

    String getExpenseName();

    String getCategoryName();

    Double getTotalAmount();

    Integer getOccurrence();

}
