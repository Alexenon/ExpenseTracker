package com.example.application.controller;

import com.example.application.model.Expense;
import com.example.application.model.ExpenseDTO;
import com.example.application.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/expense")
public class ExpenseController {

    @Autowired
    ExpenseService expenseService;

    @GetMapping("/all")
    public List<ExpenseDTO> getAllExpenses() {
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

    @DeleteMapping("/delete/{expenseId}")
    public String deleteExpenseById(@PathVariable Integer expenseId) {
        expenseService.deleteExpenseById(expenseId);
        return "Expense with id=" + expenseId + " had been deleted";
    }

    @GetMapping("/{categoryName}")
    public List<ExpenseDTO> getExpensesByCategory(@PathVariable String categoryName) {
        return expenseService.getExpeneseByCategory(categoryName);
    }

    @GetMapping("/get")
    public List<ExpenseDTO> getExpensesByMonth(
            @RequestParam(value = "month", required = false) Integer month
    ) {
        return expenseService.getExpensesByMonth(Objects.requireNonNullElseGet(month,
                () -> LocalDate.now().getMonthValue()));
    }

    @GetMapping("/grouped")
    public List<Object[]> getExpensesGroupedByCategory() {
        return expenseService.getGroupedExpensesByCategory();
    }

    @GetMapping("/test")
    public List<ExpenseDTO> test() {
        return expenseService.getExpensesCurrentMonth();
    }

}
