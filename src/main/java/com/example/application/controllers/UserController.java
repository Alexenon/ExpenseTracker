package com.example.application.controllers;

import com.example.application.data.requests.RegisterUserRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRequestHandler userRequestHandler;

    @PostMapping("/register")
    public ResponseEntity<?> createNewUser(@Valid @RequestBody RegisterUserRequest registerUserRequest) {
        return userRequestHandler.createNewUser(registerUserRequest);
    }

}
