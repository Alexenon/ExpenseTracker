package com.example.application.expense;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
public class UpdateExpenseRequest {

	@NotNull
	@Min(value = 1, message = "Invalid id")
	private Long id;

	@NotBlank
	@Size(min = 4, max = 50, message = "Expense name should be between 4 and 50 characters")
	private String name;

	@Nullable
	@Size(max = 250, message = "Description should not exceed 250 characters")
	private String description;

	@NotNull
	@DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
	private BigDecimal amount;

	@NotNull
	private String category;

	@NotNull
	private Set<String> tags;

	@NotNull
	private ExpenseTimestamp timestamp;

	@NotNull
	private LocalDate startDate;

	@Nullable
	private LocalDate expireDate;

}
