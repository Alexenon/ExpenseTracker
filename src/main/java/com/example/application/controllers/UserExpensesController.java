package com.example.application.controllers;

import com.example.application.data.dtos.ExpenseDTO;
import com.example.application.data.requests.ExpenseRequest;
import com.example.application.entities.Expense;
import com.example.application.services.ExpenseService;
import com.example.application.utils.ExpenseConvertor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/*
  TODO: Use logged user to retrieve data, because this controller is used only by logged user
   - Add verification that user is logged in + add this endpoint as secured
   - Add a separate controller for admin that can retrieve any data
   - Remove endpoints that user cannot access to
*/

/**
 * This controller contains just endpoints that a simple user can access
 */
@RestController
@RequestMapping("/api/expense")
@RequiredArgsConstructor
public class UserExpensesController {

    private final ExpenseService expenseService;
    private final ExpenseConvertor expenseConvertor;

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

    @PostMapping("/post")
    public String test(@RequestBody String s) {
        System.out.println("We got to this method");
        System.out.println("s = " + s);
        return s;
    }

    @PutMapping("/update")
    public Expense updateExpense(@RequestBody Expense expense) {
        expenseService.updateExpense(expense);
        return expense;
    }

    @DeleteMapping("/delete")
    public String deleteExpense(@RequestBody Expense expense) {
        expenseService.deleteExpense(expense);
        return "The expense was deleted successfully";
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
        return expenseService.getExpensesByCategory(categoryName);
    }

}
