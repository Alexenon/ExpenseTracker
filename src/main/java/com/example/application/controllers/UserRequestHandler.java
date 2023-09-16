package com.example.application.controllers;

import com.example.application.data.requests.RegisterUserRequest;
import com.example.application.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Component
public class UserRequestHandler {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> createNewUser(@Valid @RequestBody RegisterUserRequest registerUserRequest) {
        try {
            userService.createNewUser(registerUserRequest);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok("User successfully created");
    }

}
