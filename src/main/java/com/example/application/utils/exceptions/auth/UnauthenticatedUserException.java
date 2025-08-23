package com.example.application.utils.exceptions.auth;

import org.springframework.security.core.AuthenticationException;

public class UnauthenticatedUserException extends AuthenticationException {

    public UnauthenticatedUserException(String message) {
        super(message);
    }

}
