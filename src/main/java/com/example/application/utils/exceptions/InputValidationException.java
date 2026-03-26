package com.example.application.utils.exceptions;

import jakarta.validation.ValidationException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class InputValidationException extends ValidationException {

	private final String message;
	private final Map<String, String> errors;

	public InputValidationException(String message) {
		this.message = message;
		this.errors = new HashMap<>();
	}

	public InputValidationException(Map<String, String> errors) {
		this.message = errors.entrySet()
				.stream()
				.map(e -> e.getKey() + ": " + e.getValue())
				.collect(Collectors.joining("\n", "{", "}"));

		this.errors = errors;
	}

	@Override
	public String getMessage() {
		return message;
	}

	public Map<String, String> getErrors() {
		return errors;
	}
}
