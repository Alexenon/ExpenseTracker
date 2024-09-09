package com.example.application.utils;

import com.example.application.data.requests.ExpenseRequest;
import com.example.application.entities.Category;
import com.example.application.entities.Expense;
import com.example.application.entities.User;
import com.example.application.services.CategoryService;
import com.example.application.services.SecurityService;
import com.example.application.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ExpenseConvertor {

    @Autowired
    private final UserService userService;

    @Autowired
    private final CategoryService categoryService;

    @Autowired
    private final SecurityService securityService;

    /* TODO: REFACTOR THIS
     *   Throws error for unauthenticated user on API side - FIXING BY NOT ALLOWING UNAUTHENTICATED USERS TO THIS METHOD
     *   Think about API part, allowing to POST for other users too, or just for logged in
     * */

    public Expense convertToExpense(ExpenseRequest expenseRequest) {
        String userEmailOrUsername = Objects.requireNonNullElse(expenseRequest.getUserEmailOrUsername(),
                securityService.getAuthenticatedUserDetails().getUsername());

        User user = userService.findByUsernameOrEmailIgnoreCase(userEmailOrUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User with such username or email not found!"));

        return convertToExpense(expenseRequest, user);
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
