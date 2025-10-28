package com.example.application.utils.exceptions;

public class InvalidDataException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "An unexpected error occurred";

    public InvalidDataException() {
        this(DEFAULT_MESSAGE);
    }

    public InvalidDataException(String message) {
        this(message, null);
    }

    public InvalidDataException(Throwable cause) {
        this(DEFAULT_MESSAGE, cause);
    }

    public InvalidDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
