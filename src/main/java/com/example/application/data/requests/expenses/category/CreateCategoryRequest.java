package com.example.application.data.requests.expenses.category;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CreateCategoryRequest {

	@NotBlank
	@Size(min = 4, max = 20, message = "Name should be between 4 and 20 characters")
	private final String name;

	@NotBlank
	@Size(min = 4, max = 50, message = "Icon name should be between 4 and 20 characters")
	private final String iconName;

	@NotNull
	@Min(value = 1, message = "Invalid user id")
	private final Long userId;

}
