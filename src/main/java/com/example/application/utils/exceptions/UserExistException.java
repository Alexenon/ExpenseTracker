package com.example.application.utils.exceptions;

import org.springframework.security.core.AuthenticationException;

public class UserExistException extends AuthenticationException {

    public UserExistException(String message) {
        super(message);
    }
}
