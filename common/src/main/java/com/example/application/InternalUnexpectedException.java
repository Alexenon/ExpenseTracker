package com.example.application;

public class InternalUnexpectedException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "An unexpected error occurred";

    public InternalUnexpectedException() {
        this(DEFAULT_MESSAGE);
    }

    public InternalUnexpectedException(String message) {
        this(message, null);
    }

    public InternalUnexpectedException(Throwable cause) {
        this(DEFAULT_MESSAGE, cause);
    }

    public InternalUnexpectedException(String message, Throwable cause) {
        super(message, cause);
    }

}
