package com.example.application.controller;

import com.example.application.model.*;
import com.example.application.service.CategoryService;
import com.example.application.service.ExpenseService;
import com.example.application.service.TimestampService;
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

    @Autowired
    TimestampService timestampService;

    @Autowired
    CategoryService categoryService;

    @GetMapping("/all")
    public List<ExpenseDTO> getAllExpenses() {
        return expenseService.getAllExpenses();
    }

    @PostMapping("/add")
    public Expense addExpense(@RequestBody Expense expense) {
        return expenseService.saveExpense(expense);
    }

    @PostMapping("/save")
    public Expense saveExpense(@RequestBody ExpenseRequest expenseRequest) {
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

        return expenseService.saveExpense(expense);
    }

    @PutMapping
    public Expense updateExpense(@RequestBody Expense expense) {
        expenseService.updateExpense(expense);
        return expense;
    }

    @DeleteMapping("/delete")
    public String deleteExpense(@RequestBody Expense expense) {
        expenseService.deleteExpense(expense);
        return "The expense was deleted succesfully";
    }

    @DeleteMapping("/delete/{expenseId}")
    public String deleteExpenseById(@PathVariable Integer expenseId) {
        expenseService.deleteExpenseById(expenseId);
        return "Expense with id=" + expenseId + " had been deleted";
    }

    @DeleteMapping("/delete/all")
    public String deleteExpenseById() {
        expenseService.deleteAllExpanses();
        return "All expanses have been deleted";
    }

    @GetMapping("/{categoryName}")
    public List<ExpenseDTO> getExpensesByCategory(@PathVariable String categoryName) {
        return expenseService.getExpeneseByCategory(categoryName);
    }

    /**
     * Get expenses by month by this link "https://localhost:8080/api/expense/get?month=6"
     *
     * @param month The month parameter specifying the month to retrieve expenses for. If not provided,
     *              the current month will be used.
     * @return A list of ExpenseDTO objects representing the expenses for the specified month.
     */
    @GetMapping("/get")
    public List<ExpenseDTO> getExpensesByMonth(
            @RequestParam(value = "month", required = false) Integer month
    ) {
        return expenseService.getExpensesByMonth(Objects.requireNonNullElseGet(month,
                () -> LocalDate.now().getMonthValue()));
    }

    @GetMapping("/getByYear")
    public List<ExpenseDTO> getExpensesByYear(
            @RequestParam(value = "yearParam", required = false) Integer year
    ) {
        return expenseService.getExpensesByYear(Objects.requireNonNullElseGet(year,
                () -> LocalDate.now().getYear()));
    }

    @GetMapping("/grouped")
    public List<Object[]> getExpensesGroupedByCategory() {
        return expenseService.getGroupedExpensesByCategory();
    }

    @PostMapping
    public String addExpenses(@RequestBody List<Expense> expenseList) {
        expenseService.saveExpenses(expenseList);
        return "All expanses have been added";
    }

}
