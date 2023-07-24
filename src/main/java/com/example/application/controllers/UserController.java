package com.example.application.controllers;

import com.example.application.dtos.RegistrationUserDTO;
import com.example.application.entities.User;
import com.example.application.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/add")
    public void createNewUser(@Valid @RequestBody RegistrationUserDTO registrationUserDTO) {
        userService.createNewUser(registrationUserDTO);
    }

    @PostMapping("/save")
    public void createNewUser(@Valid @RequestBody User user) {
        userService.createNewUser(user);
    }

}
