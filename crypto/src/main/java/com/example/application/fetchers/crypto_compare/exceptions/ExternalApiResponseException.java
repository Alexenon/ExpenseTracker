package com.example.application.fetchers.crypto_compare.exceptions;

public class ExternalApiResponseException extends RuntimeException {
    public ExternalApiResponseException(String message) {
        super(message);
    }

    public ExternalApiResponseException(String message, Throwable cause) {
        super(message, cause);
    }
}