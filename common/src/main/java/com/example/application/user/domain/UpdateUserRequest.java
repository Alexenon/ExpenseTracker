package com.example.application.user.domain;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserRequest {

	@NotNull
	@Min(value = 1, message = "Invalid id")
	private Long userId;

	@Size(min = 4, max = 255)
	private String username;

	@Size(min = 4, max = 128)
	private String email;

	public UpdateUserRequest(Long userId, String username, String email) {
		this.userId = userId;
		this.username = username;
		this.email = email;
	}

	public UpdateUserRequest(UserDTO userDTO) {
		this(userDTO.getId(), userDTO.getUsername(), userDTO.getEmail());
	}

}
