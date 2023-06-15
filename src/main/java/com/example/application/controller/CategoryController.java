package com.example.application.controller;

import com.example.application.model.Category;
import com.example.application.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {
    
    @Autowired
    CategoryRepository repository;

    @GetMapping("/all")
    public List<Category> getAllCategorys() {
        return repository.findAll();
    }

    @GetMapping("/add")
    public String addCategory(Category category) {
        repository.save(category);
        return "Category was added succesfully";
    }

    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        repository.deleteById(id);
        return "Category was added succesfully";
    }
    
}
