package com.example.application.user.domain;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserPasswordRequest {

	@NotNull
	@Min(value = 1, message = "Invalid id")
	private Long userId;

	@Size(min = 4, max = 255)
	private String password;

	public UpdateUserPasswordRequest(Long userId, String password) {
		this.userId = userId;
		this.password = password;
	}

}
