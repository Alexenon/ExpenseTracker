package com.example.application.services.expenses;

public class TagNameAlreadyExistsException extends RuntimeException {

	public TagNameAlreadyExistsException(String tagName) {
		super("Tag already exists: " + tagName);
	}

}