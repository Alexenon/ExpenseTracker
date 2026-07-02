package com.example.application.tag;

public class TagNameAlreadyExistsException extends RuntimeException {

	public TagNameAlreadyExistsException(String tagName) {
		super("Tag already exists: " + tagName);
	}

}