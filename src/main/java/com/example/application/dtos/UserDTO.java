package com.example.application.dtos;

import com.example.application.entities.Role;

import java.util.List;

public interface UserDTO {
    Long getId();

    String getUsername();

    String getPassword();

    String getEmail();

    List<Role> getRoles();
}
