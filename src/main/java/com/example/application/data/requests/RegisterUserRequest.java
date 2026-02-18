package com.example.application.data.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.StringJoiner;

@Data
@Builder
public class RegisterUserRequest {

	@NotNull
	@Size(min = 4, max = 20)
	private String username;

	@NotNull
	@Size(min = 4, max = 20)
	private String email;

	@NotNull
	@Size(min = 4, max = 20)
	private String password;

	@NotNull
	@Size(min = 4, max = 20)
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
