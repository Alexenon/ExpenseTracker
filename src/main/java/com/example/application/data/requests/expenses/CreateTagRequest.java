package com.example.application.data.requests.expenses;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateTagRequest {

	@NotBlank
	@Size(min = 4, max = 20, message = "Name should be between 4 and 20 characters")
	private final String name;

	@NotNull
	@Min(value = 0, message = "Invalid userId")
	private final Long userId;

}
