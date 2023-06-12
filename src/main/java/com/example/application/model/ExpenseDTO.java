package com.example.application.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseDTO {
    private String expenseName;
    private BigDecimal amount;
    private String categoryName;
    private String description;
    private String timestampName;
}
