package com.example.application.expense;

import com.example.application.entities.expenses.ExpenseTimestamp;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
public class CreateExpenseRequest {

	@NotBlank
	@Size(min = 4, max = 20, message = "Expense name should be between 4 and 20 characters")
	private String name;

	@Nullable
	@Size(max = 250, message = "Description should not exceed 250 characters")
	private String description;

	@NotNull
	@DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
	private Double amount;

	@NotNull
	private String category;

	@NotNull
	private Set<String> tags = new HashSet<>();

	@NotNull
	private ExpenseTimestamp timestamp;

	@NotNull
	private LocalDate startDate;

	@Nullable
	private LocalDate expireDate;

	@NotNull
	private Long userId;

}
