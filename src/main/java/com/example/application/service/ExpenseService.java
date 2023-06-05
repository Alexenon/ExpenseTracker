package com.example.application.service;

import com.example.application.model.Expense;
import com.example.application.repository.ExpenseRepository;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.GeneratorType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository repository;

    public ExpenseService() {
    }

    public ExpenseService(ExpenseRepository repository) {
        this.repository = repository;
    }

    public List<Expense> getAllExpenses() {
        return repository.findAll();
    }

    public void saveExpense(Expense expense) {
        repository.save(expense);
    }

    public void deleteExpense(Expense expense) {
        repository.delete(expense);
    }

//    public List<Expense> searchExpenses(String value) {
//        return repository.searchIgnoreCase(value);
//    }
}
