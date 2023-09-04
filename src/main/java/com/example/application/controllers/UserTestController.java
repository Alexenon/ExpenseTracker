package com.example.application.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/test")
public class UserTestController {

    @GetMapping("/info")
    public String getTest(Principal principal) {
        return principal.getName();
    }

}
