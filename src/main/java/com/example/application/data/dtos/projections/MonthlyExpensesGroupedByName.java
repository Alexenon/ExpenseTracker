package com.example.application.data.dtos.projections;

public interface MonthlyExpensesGroupedByName {

    String getExpenseName();

    String getCategoryName();

    Double getTotalAmount();

    Integer getOccurrence();

}
