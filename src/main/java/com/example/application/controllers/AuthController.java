package com.example.application.controllers;

import com.example.application.data.requests.AuthRequest;
import com.example.application.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthRequest authRequest) {
        return authService.authenticate(authRequest.getUsername(), authRequest.getPassword());
    }

}
