package com.example.application.data.requests.expenses.category;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateCategoryRequest {

	@NotNull
	@Min(value = 0, message = "Invalid id")
	private final Long id;

	@NotBlank
	@Size(min = 4, max = 20, message = "Name should be between 4 and 20 characters")
	private final String name;

	@NotBlank
	@Size(min = 4, max = 20, message = "Icon name should be between 4 and 20 characters")
	private final String iconName;

}
