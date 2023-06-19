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

    public void updateExpense(Expense expense) {
        Expense expenseToUpdate = repository.findById(expense.getId())
                .orElseThrow(() -> new IllegalArgumentException("Expense not found"));

        expenseToUpdate.setName(expense.getName());
        expenseToUpdate.setAmount(expense.getAmount());
        expenseToUpdate.setDescription(expense.getDescription());
        expenseToUpdate.setDate(expense.getDate());
        expenseToUpdate.setTimestamp(expense.getTimestamp());
        expenseToUpdate.setCategory(expense.getCategory());

        repository.save(expenseToUpdate);
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

    public List<ExpenseDTO> getExpensesByMonth(int month) {
        return repository.findExpensesPerMonth(month);
    }

    public List<ExpenseDTO> getExpensesByYear(int year) {
        return repository.findExpensesPerYear(year);
    }

    public List<Object[]> getGroupedExpensesByCategory() {
        return repository.findGroupedCategoriesWithTotalSums();
    }

    public void saveExpenses(List<Expense> expenseList) {
        repository.saveAll(expenseList);
    }

}
