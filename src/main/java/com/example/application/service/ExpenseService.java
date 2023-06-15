package com.example.application.service;

import com.example.application.model.Expense;
import com.example.application.model.ExpenseDTO;
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

    public List<ExpenseDTO> getAllExpenses() {
        return repository.getAll();
    }

    public Expense saveExpense(Expense expense) {
        return repository.save(expense);
    }

    public void deleteExpense(Expense expense) {
        repository.delete(expense);
    }

    public void deleteExpenseById(long expenseId) {
        repository.deleteById(expenseId);
    }

    public void deleteAllExpanses() {
        repository.deleteAll();
    }

    public List<ExpenseDTO> getExpeneseByCategory(String categoryName) {
        return repository.findByCategory(categoryName);
    }

    public List<ExpenseDTO> getExpensesCurrentMonth() {
        return repository.findExpensesCurrentMonth();
    }

    public List<ExpenseDTO> getExpensesByMonth(int month) {
        return repository.findExpensesMonth(month);
    }

    public List<Object[]> getGroupedExpensesByCategory() {
        return repository.findGroupedExpensesByCategory();
    }

}
