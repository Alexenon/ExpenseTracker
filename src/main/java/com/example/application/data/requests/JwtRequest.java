package com.example.application.data.requests;

import lombok.Data;

@Data
public class JwtRequest {
    private String username;
    private String password;
}