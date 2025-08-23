package com.example.application.utils.exceptions.auth;

import org.springframework.security.core.AuthenticationException;

public class UsernameTakenException extends AuthenticationException {

    public UsernameTakenException(String message) {
        super(message);
    }
}
