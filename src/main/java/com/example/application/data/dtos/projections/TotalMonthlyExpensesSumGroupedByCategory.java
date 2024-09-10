package com.example.application.data.dtos.projections;

import java.util.Optional;

public interface TotalMonthlyExpensesSumGroupedByCategory {
    String getCategoryName();

    Optional<Double> getTotalSpentPerMonth();
}
