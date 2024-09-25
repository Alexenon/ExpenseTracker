package com.example.application.utils.exceptions;

import org.springframework.security.core.AuthenticationException;

public class UnauthenticatedUserException extends AuthenticationException {

    public UnauthenticatedUserException(String message) {
        super(message);
    }

}
