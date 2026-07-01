package com.example.application.utils.fetchers.crypto_compare.exceptions;

public class ExternalApiResponseException extends RuntimeException {
    public ExternalApiResponseException(String message) {
        super(message);
    }

    public ExternalApiResponseException(String message, Throwable cause) {
        super(message, cause);
    }
}