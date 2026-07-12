package com.example.application.user.exceptions;

public class EmailTakenException extends RuntimeException {

	public EmailTakenException(String message) {
		super(message);
	}

}
