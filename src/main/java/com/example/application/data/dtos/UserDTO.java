package com.example.application.data.dtos;

import com.example.application.entities.User;

import java.util.Set;

public interface UserDTO {
    Long getId();

    String getUsername();

    String getPassword();

    String getEmail();

    Set<User.Role> getRoles();
}
