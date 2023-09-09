package com.example.application.data.requests;

import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
}