package com.example.application.services;

import com.example.application.entities.Category;
import com.example.application.repositories.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository repository;

    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

    public List<Category> getAllCategories() {
        return repository.findAll();
    }

    public List<String> getAllCategoryNames() {
        return getAllCategories().stream().map(Category::getName).toList();
    }

    public Category getCategoryByName(String name) {
        return repository.getByName(name);
    }

}