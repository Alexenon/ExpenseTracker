package com.example.application.user.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.StringJoiner;

@Data
@Builder
public class RegisterUserRequest {

	@NotBlank
	@Size(min = 4, max = 255, message = "Username should be between 4 and 20 characters")
	private String username;

	@NotBlank
	@Email
	@Size(min = 4, max = 320, message = "Email should be between 4 and 320 characters")
	private String email;

	@NotBlank
	@Size(min = 4, max = 128, message = "Password should be between 4 and 20 characters")
	private String password;

	@NotBlank
	@Size(min = 4, max = 128, message = "Confirm password should be between 4 and 20 characters")
	private String confirmPassword;

	public RegisterUserRequest() {
	}

	public RegisterUserRequest(String username, String email, String password, String confirmPassword) {
		this.username = username;
		this.email = email;
		this.password = password;
		this.confirmPassword = confirmPassword;
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", RegisterUserRequest.class.getSimpleName() + "[", "]")
				.add("username='" + username + "'")
				.add("email='" + email + "'")
				.add("password='###'")
				.toString();
	}

}
