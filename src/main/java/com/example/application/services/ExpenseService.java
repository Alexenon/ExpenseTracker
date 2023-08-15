package com.example.application.services;

import com.example.application.dtos.ExpenseDTO;
import com.example.application.entities.Expense;
import com.example.application.repositories.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository repository;

    public ExpenseService(ExpenseRepository repository) {
        this.repository = repository;
    }

    public List<ExpenseDTO> getAllExpenses() {
        return repository.getAll();
    }

    public List<ExpenseDTO> getAllExpensesByUser(String userEmailOrUsername) {
        return repository.getAll(userEmailOrUsername);
    }

    public Expense saveExpense(Expense expense) {
        return repository.save(expense);
    }

    public void saveExpenses(List<Expense> expenseList) {
        repository.saveAll(expenseList);
    }

    public void updateExpense(Expense expense) {
        Expense expenseToUpdate = repository.findById(expense.getId())
                .orElseThrow(() -> new IllegalArgumentException("Expense not found"));

        expenseToUpdate.setName(expense.getName());
        expenseToUpdate.setAmount(expense.getAmount());
        expenseToUpdate.setDescription(expense.getDescription());
        expenseToUpdate.setStartDate(expense.getExpireDate());
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

    public List<Object[]> getMonthlyCategoriesTotalSum(String userEmailOrUsername, int year, int month) {
        return repository.findMonthlyCategoriesTotalSum(userEmailOrUsername, year, month);
    }

}
