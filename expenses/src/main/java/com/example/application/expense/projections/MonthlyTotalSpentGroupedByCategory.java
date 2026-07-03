package com.example.application.expense.projections;

import java.util.Optional;

public interface MonthlyTotalSpentGroupedByCategory {
    String getCategoryName();

    Optional<Double> getTotalSpentPerMonth();
}
