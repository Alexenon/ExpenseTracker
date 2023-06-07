package com.example.application.service;

import com.example.application.model.Expense;
import com.example.application.repository.ExpenseRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseService {

    @Autowired
    private ExpenseRepository repository;

    public List<Expense> getAllExpenses() {
        return repository.findAll();
    }

    public Expense saveExpense(Expense expense) {
        return repository.save(expense);
    }

    public void deleteExpense(Expense expense) {
        repository.delete(expense);
    }

//    public List<Expense> searchExpenses(String value) {
//        return repository.searchIgnoreCase(value);
//    }
}
