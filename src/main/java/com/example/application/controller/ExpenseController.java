package com.example.application.controller;

import com.example.application.model.Expense;
import com.example.application.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expense")
public class ExpenseController {

    @Autowired
    ExpenseService expenseService;

    @GetMapping("/all")
    public List<Expense> getAllExpenses() {
        return expenseService.getAllExpenses();
    }

    @PostMapping("/add")
    public Expense addExpense(@RequestBody Expense expense) {
        return expenseService.saveExpense(expense);
    }

    @PostMapping("/delete")
    public String deleteExpense(@RequestBody Expense expense) {
        expenseService.deleteExpense(expense);
        return "The expense was deleted succesfully";
    }

}
