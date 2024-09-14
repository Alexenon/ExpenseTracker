package com.example.application.services;

import com.example.application.data.dtos.ExpenseDTO;
import com.example.application.data.dtos.projections.MonthlyExpensesGroupedByName;
import com.example.application.data.dtos.projections.MonthlyExpensesProjection;
import com.example.application.data.dtos.projections.MonthlyTotalSpentGroupedByCategory;
import com.example.application.data.requests.ExpenseRequest;
import com.example.application.entities.Expense;
import com.example.application.entities.User;
import com.example.application.repositories.ExpenseRepository;
import com.example.application.utils.ExpenseConvertor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository repository;

    @Autowired
    private ExpenseConvertor expenseConvertor;

    @Autowired
    private SecurityService securityService;

    public List<ExpenseDTO> getAllExpenses() {
        return repository.getAll();
    }

    public List<ExpenseDTO> getAllExpensesByUser(String userEmailOrUsername) {
        return repository.getAll(userEmailOrUsername);
    }

    public List<ExpenseDTO> getAllExpensesByUser() {
        return getAllExpensesByUser(securityService.getAuthenticatedUser().getUsername());
    }

    public Expense saveExpense(Expense expense) {
        replaceExpireDateForOneTimeExpenses(expense);
        System.out.println("Saving " + expense);

        return repository.save(expense);
    }

    public void saveExpenses(List<Expense> expenseList) {
        expenseList.forEach(this::saveExpense);
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

        replaceExpireDateForOneTimeExpenses(expense);

        repository.save(expenseToUpdate);
    }

    /**
     * Updates the expireDate to be startDate + 1 day, if the expense timestamp is ONCE
     */
    private void replaceExpireDateForOneTimeExpenses(Expense expense) {
        if (!expense.getTimestamp().equals(Expense.Timestamp.ONCE)) {
            return;
        }

        LocalDate startDate = expense.getStartDate();
        expense.setExpireDate(startDate.plusDays(1));
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

    public List<MonthlyTotalSpentGroupedByCategory> getMonthlyTotalSpentGroupedByCategory(String userEmailOrUsername) {
        return repository.totalSpentPerMonthGroupedByCategory(userEmailOrUsername, LocalDate.now().getYear(), LocalDate.now().getMonthValue());
    }

    public List<MonthlyTotalSpentGroupedByCategory> getMonthlyTotalSpentGroupedByCategory() {
        return getMonthlyTotalSpentGroupedByCategory(securityService.getAuthenticatedUser().getUsername());
    }

    public List<MonthlyTotalSpentGroupedByCategory> getMonthlyTotalSpentGroupedByCategory(String userEmailOrUsername, int year, int month) {
        return repository.totalSpentPerMonthGroupedByCategory(userEmailOrUsername, year, month);
    }

    public List<MonthlyExpensesGroupedByName> getMonthlyExpensesByUser(String userEmailOrUsername, Integer year, Integer month) {
        year = Objects.requireNonNullElse(year, LocalDate.now().getYear());
        month = Objects.requireNonNullElse(month, LocalDate.now().getMonthValue());
        return repository.totalSpentPerMonthGroupedByExpenseName(userEmailOrUsername, year, month);
    }

    public List<MonthlyExpensesGroupedByName> getMonthlyExpensesByUser(Integer year, Integer month) {
        return getMonthlyExpensesByUser(securityService.getAuthenticatedUser().getUsername(), year, month);
    }

    @Transactional
    public List<MonthlyExpensesProjection> getMonthlyExpensesByUser(String username, LocalDate date) {
        date = Objects.requireNonNullElse(date, LocalDate.now());
        return repository.findMonthlyExpenses(username, date);
    }

    @Transactional
    public List<MonthlyExpensesProjection> getMonthlyExpensesByUser(LocalDate date) {
        return getMonthlyExpensesByUser(securityService.getAuthenticatedUser().getUsername(), date);
    }

    public Expense convertToExpense(ExpenseRequest expenseRequest) {
        return expenseConvertor.convertToExpense(expenseRequest);
    }

    public Expense convertToExpense(ExpenseRequest expenseRequest, User user) {
        return expenseConvertor.convertToExpense(expenseRequest, user);
    }

}
