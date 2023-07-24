package com.example.application.dtos;

import com.example.application.entities.Role;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Collection;

@Data
public class UserRequest {

    @NotNull
    @Size(min = 4, max = 20)
    private String username;

    @NotNull
    @Size(min = 4, max = 20)
    private String password;

    @NotNull
    private String email;

    private Collection<Role> roles;
}
