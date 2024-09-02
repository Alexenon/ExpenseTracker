package com.example.application.services;

import com.example.application.data.dtos.ExpenseDTO;
import com.example.application.data.dtos.projections.ExpensesSumGroupedByCategory;
import com.example.application.data.enums.Timestamps;
import com.example.application.entities.Expense;
import com.example.application.repositories.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
        updateExpireDateIfNeeded(expense);
        return repository.save(expense);
    }

    public void updateExpense(Expense expense) {
        Expense expenseToUpdate = repository.findById(expense.getId())
                .orElseThrow(() -> new IllegalArgumentException("Expense not found"));

        expenseToUpdate.setName(expense.getName());
        expenseToUpdate.setAmount(expense.getAmount());
        expenseToUpdate.setDescription(expense.getDescription());
        expenseToUpdate.setStartDate(expense.getStartDate());
        expenseToUpdate.setExpireDate(expense.getExpireDate());
        expenseToUpdate.setTimestamp(expense.getTimestamp());
        expenseToUpdate.setCategory(expense.getCategory());

        updateExpireDateIfNeeded(expense);

        repository.save(expenseToUpdate);
    }

    public void saveExpenses(List<Expense> expenseList) {
        expenseList.forEach(this::updateExpireDateIfNeeded);
        repository.saveAll(expenseList);
    }

    private void updateExpireDateIfNeeded(Expense expense) {
        String timestampName = expense.getTimestamp().getName();
        if (timestampName.equals(Timestamps.ONCE.toString())) {
            LocalDate startDate = expense.getStartDate();
            expense.setExpireDate(startDate.plusDays(1));
        }
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

    public List<ExpenseDTO> getExpensesByCategory(String categoryName) {
        return repository.findByCategory(categoryName);
    }

    public List<ExpenseDTO> getExpensesByMonth(int month) {
        return repository.findExpensesPerMonth(month);
    }

    public List<ExpenseDTO> getExpensesByYear(int year) {
        return repository.findExpensesPerYear(year);
    }

    public List<ExpensesSumGroupedByCategory> getExpensesTotalSumGroupedByCategory(String userEmailOrUsername) {
        return repository.findExpensesTotalSumGroupedByCategory(userEmailOrUsername, LocalDate.now().getYear(), LocalDate.now().getMonthValue());
    }

    public List<ExpensesSumGroupedByCategory> getExpensesTotalSumGroupedByCategory(String userEmailOrUsername, int year, int month) {
        return repository.findExpensesTotalSumGroupedByCategory(userEmailOrUsername, year, month);
    }

}
