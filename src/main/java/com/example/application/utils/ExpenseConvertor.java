package com.example.application.utils;

import com.example.application.entities.Expense;
import com.example.application.entities.Timestamp;
import com.example.application.entities.Category;
import com.example.application.dtos.ExpenseRequest;
import com.example.application.services.CategoryService;
import com.example.application.services.TimestampService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseConvertor {

    @Autowired
    private TimestampService timestampService;

    @Autowired
    private CategoryService categoryService;

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
