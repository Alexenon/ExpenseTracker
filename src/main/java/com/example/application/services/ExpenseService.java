package com.example.application.services;

import com.example.application.data.dtos.ExpenseDTO;
import com.example.application.data.models.projections.MonthlyExpensesProjection;
import com.example.application.data.requests.ExpenseRequest;
import com.example.application.entities.Expense;
import com.example.application.entities.ExpenseTimestamp;
import com.example.application.entities.User;
import com.example.application.repositories.ExpenseRepository;
import com.example.application.utils.ExpenseConvertor;
import com.example.application.utils.common.lang.DateUtils;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/*
    TODO: Add Facade Service
        - Fix convertToExpense
        - Fix updateExpense
        - Remove old implementations of monthly expenses
* */

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private ExpenseConvertor expenseConvertor;

    @NotNull
    public List<ExpenseDTO> getAllExpenses() {
        return expenseRepository.getAll();
    }

    @NotNull
    public List<ExpenseDTO> getAllExpensesByUser(@NotNull String userEmailOrUsername) {
        Objects.requireNonNull(userEmailOrUsername, "user email or username");
        return expenseRepository.getAll(userEmailOrUsername);
    }

    @NotNull
    public List<ExpenseDTO> getAllExpensesByUser(@NotNull User user) {
        Objects.requireNonNull(user, "user");
        return getAllExpensesByUser(user.getUsername());
    }

    @NotNull
    public List<ExpenseDTO> getAllExpensesByUser() {
        return getAllExpensesByUser(securityService.getAuthenticatedUser());
    }

    public Expense saveExpense(@NotNull Expense expense) {
        Objects.requireNonNull(expense);
        replaceExpireDateForOneTimeExpenses(expense);
        System.out.println("Saving " + expense);

        return expenseRepository.save(expense);
    }

    public void saveExpenses(@NotNull List<Expense> expenseList) {
        Objects.requireNonNull(expenseList, "expensesList")
                .forEach(this::saveExpense);
    }

    @Nullable
    public Expense updateExpense(@NotNull Expense expense) {
        Objects.requireNonNull(expense, "expense");
        Expense expenseToUpdate = expenseRepository.findById(expense.getId())
                .orElseThrow(() -> new IllegalArgumentException("Expense not found"));

        expenseToUpdate.setName(expense.getName());
        expenseToUpdate.setAmount(expense.getAmount());
        expenseToUpdate.setDescription(expense.getDescription());
        expenseToUpdate.setStartDate(expense.getStartDate());
        expenseToUpdate.setExpireDate(expense.getExpireDate());
        expenseToUpdate.setTimestamp(expense.getTimestamp());
        expenseToUpdate.setCategory(expense.getCategory());

        replaceExpireDateForOneTimeExpenses(expense);

        return expenseRepository.save(expenseToUpdate);
    }

    /**
     * Updates the expireDate to be startDate + 1 day, if the expense timestamp is ONCE
     */
    private void replaceExpireDateForOneTimeExpenses(Expense expense) {
        if (!expense.getTimestamp().equals(ExpenseTimestamp.ONCE)) {
            return;
        }

        LocalDate startDate = expense.getStartDate();
        expense.setExpireDate(startDate.plusDays(1));
    }

    public void deleteExpense(Expense expense) {
        expenseRepository.delete(expense);
    }

    public void deleteExpenseById(long expenseId) {
        expenseRepository.deleteById(expenseId);
    }

    public void deleteAllExpanses() {
        expenseRepository.deleteAll();
    }

    @NotNull
    public List<ExpenseDTO> getExpensesByCategory(String categoryName) {
        return expenseRepository.findByCategory(categoryName);
    }

    @NotNull
    public List<ExpenseDTO> getExpensesByMonth(int month) {
        return expenseRepository.findExpensesPerMonth(month);
    }

    @NotNull
    public List<ExpenseDTO> getExpensesByYear(int year) {
        return expenseRepository.findExpensesPerYear(year);
    }

    @NotNull
    @Transactional
    public List<MonthlyExpensesProjection> getMonthlyExpensesByUser() {
        return getMonthlyExpensesByUser(securityService.getAuthenticatedUser(), LocalDate.now());
    }

    @NotNull
    @Transactional
    public List<MonthlyExpensesProjection> getMonthlyExpensesByUser(@NotNull LocalDate date) {
        return getMonthlyExpensesByUser(securityService.getAuthenticatedUser(), date);
    }

    /**
     * @param date is converted if it's:
     *             <ul>
     *                  <li>CURRENT MONTH -> remains same
     *                  <li>PREVIOUS MONTH -> into another date with its last day of month
     *                  <li>NEXT MONTH -> into another date with its first day of month
     *              </ul>
     */
    @NotNull
    @Transactional
    public List<MonthlyExpensesProjection> getMonthlyExpensesByUser(@NotNull User user, @NotNull LocalDate date) {
        Objects.requireNonNull(user, "user");
        Objects.requireNonNull(date, "date");

        if (!DateUtils.isInSameMonthAndYear(date, LocalDate.now())) {
            date = date.isBefore(LocalDate.now())
                    ? DateUtils.lastDayOfMonth(date)
                    : DateUtils.firstDayOfMonth(date);
        }

        return expenseRepository.findMonthlyExpenses(user.getUsername(), date);
    }

    public Expense convertToExpense(ExpenseRequest expenseRequest) {
        return expenseConvertor.convertToExpense(expenseRequest);
    }

    public Expense convertToExpense(ExpenseRequest expenseRequest, User user) {
        return expenseConvertor.convertToExpense(expenseRequest, user);
    }

}
