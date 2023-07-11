package com.example.application.utils;

import com.example.application.dtos.ExpenseRequest;
import com.example.application.entities.Category;
import com.example.application.entities.Expense;
import com.example.application.entities.Timestamp;
import com.example.application.services.CategoryService;
import com.example.application.services.TimestampService;
import org.springframework.stereotype.Component;

@Component
public class ExpenseConvertor {
    
    private final TimestampService timestampService;
    private final CategoryService categoryService;

    public ExpenseConvertor(TimestampService timestampService, CategoryService categoryService) {
        this.timestampService = timestampService;
        this.categoryService = categoryService;
    }

    public Expense convertToExpense(ExpenseRequest expenseRequest) {
        Expense expense = new Expense();

        expense.setName(expenseRequest.getName());
        expense.setAmount(expenseRequest.getAmount());
        expense.setDate(expenseRequest.getDate());
        expense.setExpiryDate(expenseRequest.getExpiryDate());
        expense.setDescription(expenseRequest.getDescription());

        String timestampName = expenseRequest.getTimestamp();
        Timestamp timestamp = timestampService.getTimestampByName(timestampName);
        expense.setTimestamp(timestamp);

        String categoryName = expenseRequest.getCategory();
        Category category = categoryService.getCategoryByName(categoryName);
        expense.setCategory(category);

        return expense;
    }

}
