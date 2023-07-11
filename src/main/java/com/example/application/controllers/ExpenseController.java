package com.example.application.controllers;

import com.example.application.entities.Expense;
import com.example.application.utils.ExpenseConvertor;
import com.example.application.dtos.ExpenseDTO;
import com.example.application.dtos.ExpenseRequest;
import com.example.application.services.CategoryService;
import com.example.application.services.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/expense")
public class ExpenseController {

    @Autowired
    ExpenseService expenseService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    ExpenseConvertor expenseConvertor;

    @GetMapping("/all")
    public List<ExpenseDTO> getAllExpenses() {
        return expenseService.getAllExpenses();
    }

    @PostMapping("/add")
    public Expense addExpense(@RequestBody Expense expense) {
        return expenseService.saveExpense(expense);
    }

    @PostMapping("/addAll")
    public String addExpenses(@RequestBody List<ExpenseRequest> expenseList) {
        List<Expense> expenses = expenseList.stream()
                .map(expenseConvertor::convertToExpense)
                .collect(Collectors.toList());

        expenseService.saveExpenses(expenses);
        return "All expanses have been added";
    }

    @PostMapping("/save")
    public Expense saveExpense(@RequestBody ExpenseRequest expenseRequest) {
        Expense expense = expenseConvertor.convertToExpense(expenseRequest);
        return expenseService.saveExpense(expense);
    }

    @PutMapping("/update")
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
            @RequestParam(value = "year", required = false) Integer year
    ) {
        return expenseService.getExpensesByYear(Objects.requireNonNullElseGet(year,
                () -> LocalDate.now().getYear()));
    }

    @GetMapping("/grouped")
    public List<Object[]> getExpensesTotalSumGroupedByCategory(
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "month", required = false) Integer month
    ) {
        if (year == null) {
            year = LocalDate.now().getYear();
        }

        if (month == null || month < 1 || month > 12) {
            month = LocalDate.now().getMonthValue();
        }

        return expenseService.getMonthlyCategoriesTotalSum(year, month);
    }

}
