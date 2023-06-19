package com.example.application.model;

import com.example.application.service.CategoryService;
import com.example.application.service.TimestampService;
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
