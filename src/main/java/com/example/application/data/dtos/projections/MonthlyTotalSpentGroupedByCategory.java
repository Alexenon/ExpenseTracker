package com.example.application.data.dtos.projections;

import java.util.Optional;

public interface MonthlyTotalSpentGroupedByCategory {
    String getCategoryName();

    Optional<Double> getTotalSpentPerMonth();
}
