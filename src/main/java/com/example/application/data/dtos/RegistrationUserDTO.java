package com.example.application.data.dtos;

public interface RegistrationUserDTO {

    // TODO: Add validation
    //  - Replace entity everywhere with dtos
    //  - Add finally jwt token


    String getUsername();

    String getPassword();

    String getConfirmPassword();

    String getEmail();
}