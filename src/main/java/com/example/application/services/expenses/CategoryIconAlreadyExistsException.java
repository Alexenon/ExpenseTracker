package com.example.application.services.expenses;

public class CategoryIconAlreadyExistsException extends RuntimeException {

	public CategoryIconAlreadyExistsException(String categoryName) {
		super("Category icon already exists: " + categoryName);
	}

}
