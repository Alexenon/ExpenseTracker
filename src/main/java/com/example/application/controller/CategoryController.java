package com.example.application.controller;

import com.example.application.model.Category;
import com.example.application.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/add")
    public String addCategory(@RequestBody Category category) {
        repository.save(category);
        return "Category was added succesfully";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        repository.deleteById(id);
        return "Category was deleted succesfully";
    }
    
}
