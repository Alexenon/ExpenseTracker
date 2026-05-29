package com.example.application.data.requests;

import com.example.application.entities.expenses.ExpenseTimestamp;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class ExpenseRequest {

	private String name;

	private String categoryName;

	private Set<String> tags;

	private ExpenseTimestamp timestamp;

	private String userEmailOrUsername;

	private double amount;

	private String description;

	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate startDate;

	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate expireDate;
}