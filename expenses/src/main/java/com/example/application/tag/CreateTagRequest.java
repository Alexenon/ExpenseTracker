package com.example.application.tag;

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
	@Min(value = 1, message = "Invalid userId")
	private final Long userId;

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CreateTagRequest that)) return false;

		return name.equals(that.name) && userId.equals(that.userId);
	}

	@Override
	public int hashCode() {
		int result = name.hashCode();
		result = 31 * result + userId.hashCode();
		return result;
	}

}
