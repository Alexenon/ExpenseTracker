package com.example.application.controllers;

import com.example.application.dtos.RegistrationUserDTO;
import com.example.application.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/add")
    public void createNewUser(RegistrationUserDTO registrationUserDTO) {
        userService.createNewUser(registrationUserDTO);
    }
}
