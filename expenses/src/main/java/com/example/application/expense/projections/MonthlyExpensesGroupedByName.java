package com.example.application.expense.projections;

public interface MonthlyExpensesGroupedByName {

    String getExpenseName();

    String getCategoryName();

    Double getTotalAmount();

    Integer getOccurrence();

}
