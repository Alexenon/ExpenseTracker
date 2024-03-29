package com.example.application.data.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ExpenseRequest {

    private String name;

    private double amount;

    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expireDate;

    private String timestampName;

    private String categoryName;

    private String userEmailOrUsername;
}