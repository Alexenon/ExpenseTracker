package com.example.application.service;

import com.example.application.model.Expense;
import com.example.application.model.ExpenseDTO;
import com.example.application.repository.ExpenseRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseService {

    @Autowired
    private ExpenseRepository repository;

    public List<ExpenseDTO> getAllExpenses() {
        return repository.getAll();
    }

    public Expense saveExpense(Expense expense) {
        return repository.save(expense);
    }

    public void deleteExpense(Expense expense) {
        repository.delete(expense);
    }

    public void deleteExpenseById(Integer expenseId) {

    }

    public List<Expense> getExpeneseByCategory(String categoryName) {
        return repository.findAll();
    }

    public List<ExpenseDTO> getExpensesCurrentMonth() {
        return repository.getExpensesCurrentMonth();
    }


}
