package com.example.application.data.models.projections;

import java.util.Optional;

public interface MonthlyTotalSpentGroupedByCategory {
    String getCategoryName();

    Optional<Double> getTotalSpentPerMonth();
}
