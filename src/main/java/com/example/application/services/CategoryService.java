package com.example.application.services;

import com.example.application.entities.Category;
import com.example.application.repositories.CategoryRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@NoArgsConstructor
@AllArgsConstructor
public class CategoryService {

    @Autowired
    CategoryRepository repository;

    public List<Category> getAllCategories() {
        return repository.findAll();
    }

    public Category getCategoryByName(String name) {
        return repository.getByName(name);
    }
}