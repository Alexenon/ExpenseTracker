package com.example.application.utils;

import com.example.application.data.requests.ExpenseRequest;
import com.example.application.entities.Category;
import com.example.application.entities.Expense;
import com.example.application.entities.Timestamp;
import com.example.application.entities.User;
import com.example.application.services.CategoryService;
import com.example.application.services.SecurityService;
import com.example.application.services.TimestampService;
import com.example.application.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ExpenseConvertor {

    private final UserService userService;
    private final TimestampService timestampService;
    private final CategoryService categoryService;
    private final SecurityService securityService;

    /* TODO: REFACTOR THIS
    *   Throws error for unathenticated user
    *   Think about allowing posting requests
    * */

    public Expense convertToExpense(ExpenseRequest expenseRequest) {
        Expense expense = new Expense();

        expense.setName(expenseRequest.getName());
        expense.setAmount(expenseRequest.getAmount());
        expense.setStartDate(expenseRequest.getStartDate());
        expense.setExpireDate(expenseRequest.getExpireDate());
        expense.setDescription(expenseRequest.getDescription());

        String timestampName = expenseRequest.getTimestampName();
        Timestamp timestamp = timestampService.getTimestampByName(timestampName);
        expense.setTimestamp(timestamp);

        String categoryName = expenseRequest.getCategoryName();
        Category category = categoryService.getCategoryByName(categoryName);
        expense.setCategory(category);

        String userEmailOrUsername = Objects.requireNonNullElse(expenseRequest.getUserEmailOrUsername(),
                securityService.getAuthenticatedUser().getUsername());

        User user = userService.findByUsernameOrEmailIgnoreCase(userEmailOrUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User with such username or email not found!"));
        expense.setUser(user);

        return expense;
    }

    public Expense convertToExpense(ExpenseRequest expenseRequest, User user) {
        Expense expense = new Expense();

        String timestampName = expenseRequest.getTimestampName();
        Timestamp timestamp = timestampService.getTimestampByName(timestampName);

        String categoryName = expenseRequest.getCategoryName();
        Category category = categoryService.getCategoryByName(categoryName);

        expense.setName(expenseRequest.getName());
        expense.setAmount(expenseRequest.getAmount());
        expense.setStartDate(expenseRequest.getStartDate());
        expense.setExpireDate(expenseRequest.getExpireDate());
        expense.setDescription(expenseRequest.getDescription());
        expense.setTimestamp(timestamp);
        expense.setCategory(category);
        expense.setUser(user);

        return expense;
    }

}
