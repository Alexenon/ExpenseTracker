package com.example.application.utils;

import com.example.application.dtos.ExpenseRequest;
import com.example.application.entities.Category;
import com.example.application.entities.Expense;
import com.example.application.entities.Timestamp;
import com.example.application.entities.User;
import com.example.application.services.CategoryService;
import com.example.application.services.TimestampService;
import com.example.application.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExpenseConvertor {

    private final UserService userService;
    private final TimestampService timestampService;
    private final CategoryService categoryService;

    public Expense convertToExpense(ExpenseRequest expenseRequest) {
        Expense expense = new Expense();

        expense.setName(expenseRequest.getName());
        expense.setAmount(expenseRequest.getAmount());
        expense.setDate(expenseRequest.getDate());
        expense.setExpiryDate(expenseRequest.getExpiryDate());
        expense.setDescription(expenseRequest.getDescription());

        String timestampName = expenseRequest.getTimestampName();
        Timestamp timestamp = timestampService.getTimestampByName(timestampName);
        expense.setTimestamp(timestamp);

        String categoryName = expenseRequest.getCategoryName();
        Category category = categoryService.getCategoryByName(categoryName);
        expense.setCategory(category);

        String userEmailOrUsername = expenseRequest.getUserEmailOrUsername();
        User user = userService.findByUsernameOrEmailIgnoreCase(userEmailOrUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User with such username or email not found!"));
        expense.setUser(user);

        return expense;
    }

}