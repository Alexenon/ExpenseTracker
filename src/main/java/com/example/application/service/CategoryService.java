package com.example.application.service;

import com.example.application.model.Category;
import com.example.application.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
@AllArgsConstructor
public class CategoryService {

    @Autowired
    CategoryRepository repository;

    public Category getCategoryByName(String name) {
        return repository.getByName(name);
    }

}