package com.example.application.utils.exceptions;

public class InvalidBalanceAmount extends RuntimeException {

    public InvalidBalanceAmount(String message) {
        super(message);
    }

}
