package com.example.application.data.dtos.projections;

import java.util.Optional;

public interface ExpensesSumGroupedByCategory {
    String getCategoryName();

    Optional<Double> getTotalSpentPerMonth();
}
