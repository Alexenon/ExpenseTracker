package com.example.application.integrational.expenses;

import com.example.application.Application;
import com.example.application.data.requests.expenses.CreateExpenseRequest;
import com.example.application.entities.expenses.Category;
import com.example.application.entities.expenses.Expense;
import com.example.application.entities.expenses.ExpenseTimestamp;
import com.example.application.integrational.AbstractTest;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ExpenseServiceTest extends AbstractTest {

	/*
		TODO: Add tests:
			Expire date is never null
			Find for current month all variants
			-> Check other constraints to create maybe additional testcases
	* */

	private User user;
	private Category category;

	@BeforeEach
	void setup() {
		this.user = createUser("john", "john-weak@mail.com");
		this.category = categoryRepository.findByUser(user.getId()).getFirst();
	}

	@Test
	void createExpenseSuccessfully() {
		Expense expense = createExpense("Netflix");

		Assertions.assertNotNull(expense.getId());
		Assertions.assertEquals("Netflix", expense.getName());
		Assertions.assertEquals(1000.0, expense.getAmount());
		Assertions.assertEquals(user.getId(), expense.getUser().getId());
		Assertions.assertTrue(expenseRepository.findById(expense.getId()).isPresent());
	}

	@Test
	void deleteExpenseSuccessfully() {
		Expense expense = createExpense("Buying game");
		Assertions.assertDoesNotThrow(() -> instrumentsFacadeService.deleteExpense(expense.getId()), "Expense cannot be deleted");
		Assertions.assertTrue(expenseRepository.findById(expense.getId()).isEmpty(), "Expense should be removed");
	}

	@Test
	void createInvalidExpenseWithNullExpenseName() {
		Assertions.assertThrows(DataIntegrityViolationException.class, () -> createExpense(null), "Expense name missing");
	}

	@Test
	void createInvalidExpenseWithInvalidCategory() {
		CreateExpenseRequest request = new CreateExpenseRequest();
		request.setName("Name");
		request.setAmount(1000.0);
		request.setCategory("INVALID CATEGORY");
		request.setTags(new HashSet<>());
		request.setUserId(user.getId());
		request.setStartDate(LocalDate.now());
		request.setTimestamp(ExpenseTimestamp.ONCE);

		Assertions.assertThrows(EntityNotFoundException.class,
				() -> instrumentsFacadeService.createExpense(request), "Invalid category name");
	}

	@Test
	void createInvalidExpenseWithInvalidUser() {
		CreateExpenseRequest request = new CreateExpenseRequest();
		request.setName("Name");
		request.setAmount(1000.0);
		request.setCategory(category.getName());
		request.setTags(new HashSet<>());
		request.setUserId(200L);
		request.setStartDate(LocalDate.now());
		request.setTimestamp(ExpenseTimestamp.ONCE);

		Assertions.assertThrows(EntityNotFoundException.class,
				() -> instrumentsFacadeService.createExpense(request), "Invalid category name");
	}

	//<editor-fold desc="UTILS">
	private Expense createExpense(String name) {
		return createExpense(name, Collections.emptySet());
	}

	private Expense createExpense(String name, Set<String> tags) {
		return instrumentsFacadeService.createExpense(createExpenseRequest(name, tags));
	}

	private CreateExpenseRequest createExpenseRequest(String name, Set<String> tags) {
		CreateExpenseRequest request = new CreateExpenseRequest();
		request.setName(name);
		request.setAmount(1000.0);
		request.setCategory(category.getName());
		request.setTags(tags);
		request.setUserId(user.getId());
		request.setStartDate(LocalDate.now());
		request.setTimestamp(ExpenseTimestamp.ONCE);
		return request;
	}
	//</editor-fold>


}