package com.example.application;

public class InvalidDataException extends RuntimeException {

	private static final String DEFAULT_MESSAGE = "Invalid data";

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
