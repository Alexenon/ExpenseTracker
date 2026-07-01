package com.example.application.tags;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateTagRequest {

	@NotNull
	@Min(value = 1, message = "Invalid id")
	private final Long id;

	@NotBlank
	@Size(min = 3, max = 20, message = "Name should be between 3 and 20 characters")
	private final String name;

}
