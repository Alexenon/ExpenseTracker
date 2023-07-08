package com.example.application.exceptions;

public class UserAlreadyExistException extends RuntimeException{

    public UserAlreadyExistException() {
        super("There is already a user with this username");
    }

    public UserAlreadyExistException(String message) {
        super(message);
    }
}
