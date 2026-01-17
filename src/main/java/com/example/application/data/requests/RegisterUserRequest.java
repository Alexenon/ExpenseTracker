package com.example.application.data.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
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

}
