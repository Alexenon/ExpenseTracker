package com.example.application;

public class InvalidBalanceAmountException extends RuntimeException {

    public InvalidBalanceAmountException(String message) {
        super(message);
    }

}
