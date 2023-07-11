package com.example.application.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ExpenseRequest {

    private String name;

    private double amount;

    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;

    private String timestamp;

    private String category;
}