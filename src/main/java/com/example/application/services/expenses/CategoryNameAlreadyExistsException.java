package com.example.application.services.expenses;

public class CategoryNameAlreadyExistsException extends RuntimeException {

    public CategoryNameAlreadyExistsException(String categoryName) {
        super("Category already exists: " + categoryName);
    }

}