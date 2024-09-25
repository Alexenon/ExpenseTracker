package com.example.application.utils;

import com.example.application.data.requests.ExpenseRequest;
import com.example.application.entities.Category;
import com.example.application.entities.Expense;
import com.example.application.entities.User;
import com.example.application.services.CategoryService;
import com.example.application.services.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/* TODO: REFACTOR THIS
 *   Throws error for unauthenticated user on API side - FIXING BY NOT ALLOWING UNAUTHENTICATED USERS TO THIS METHOD
 *   Think about API part, allowing to POST for other users too, or just for logged in
 * */

@Component
@RequiredArgsConstructor
public class ExpenseConvertor {

    @Autowired
    private final CategoryService categoryService;

    @Autowired
    private final SecurityService securityService;

    public Expense convertToExpense(ExpenseRequest expenseRequest) {
        return convertToExpense(expenseRequest, securityService.getAuthenticatedUser());
    }

    public Expense convertToExpense(ExpenseRequest expenseRequest, User user) {
        Expense expense = new Expense();
        expense.setName(expenseRequest.getName());
        expense.setAmount(expenseRequest.getAmount());
        expense.setStartDate(expenseRequest.getStartDate());
        expense.setExpireDate(expenseRequest.getExpireDate());
        expense.setDescription(expenseRequest.getDescription());
        expense.setTimestamp(expenseRequest.getTimestamp());
        expense.setUser(user);

        Category category = categoryService.getCategoryByName(expenseRequest.getCategoryName());
        expense.setCategory(category);

        return expense;
    }

}
