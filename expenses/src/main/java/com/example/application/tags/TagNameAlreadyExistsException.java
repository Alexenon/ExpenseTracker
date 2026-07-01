package com.example.application.tags;

public class TagNameAlreadyExistsException extends RuntimeException {

	public TagNameAlreadyExistsException(String tagName) {
		super("Tag already exists: " + tagName);
	}

}