package com.example.application.user.exceptions;

import org.springframework.security.core.AuthenticationException;

public class UsernameTakenException extends AuthenticationException {

    public UsernameTakenException(String message) {
        super(message);
    }
}
