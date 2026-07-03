package com.example.application.category;

public class CategoryNameAlreadyExistsException extends RuntimeException {

    public CategoryNameAlreadyExistsException(String categoryName) {
        super("Category already exists: " + categoryName);
    }

}