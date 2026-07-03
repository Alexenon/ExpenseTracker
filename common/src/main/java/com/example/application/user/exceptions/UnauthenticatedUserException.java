package com.example.application.user.exceptions;

import org.springframework.security.core.AuthenticationException;

public class UnauthenticatedUserException extends AuthenticationException {

    public UnauthenticatedUserException(String message) {
        super(message);
    }

}
