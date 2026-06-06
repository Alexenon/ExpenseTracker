package com.example.application.services.expenses;

import com.example.application.components.EntityValidator;
import com.example.application.data.models.projections.MonthlyExpensesProjection;
import com.example.application.entities.expenses.Expense;
import com.example.application.entities.expenses.ExpenseTimestamp;
import com.example.application.repositories.expenses.ExpenseRepository;
import com.example.application.utils.common.lang.DateUtils;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExpenseService {

	private final ExpenseRepository expenseRepository;
	private final EntityValidator validator;

	//<editor-fold desc="SEARCH">
	public Optional<Expense> findById(@NotNull Long id) {
		Objects.requireNonNull(id, "id");
		return expenseRepository.findById(id);
	}

	public List<Expense> findByUser(@NotNull Long userId) {
		Objects.requireNonNull(userId, "userId");
		return expenseRepository.findByUser(userId);
	}

	public List<Expense> getExpensesByCategory(String categoryName) {
		return expenseRepository.findByCategory(categoryName);
	}

	public List<Expense> getExpensesByMonth(int month) {
		return expenseRepository.findExpensesPerMonth(month);
	}

	public List<Expense> getExpensesByYear(int year) {
		return expenseRepository.findExpensesPerYear(year);
	}

	/**
	 * @param date is converted if it's:
	 *             <ul>
	 *                  <li>CURRENT MONTH -> remains same
	 *                  <li>PREVIOUS MONTH -> into another date with its last day of month
	 *                  <li>NEXT MONTH -> into another date with its first day of month
	 *              </ul>
	 */
	@Nonnull
	@Transactional
	public List<MonthlyExpensesProjection> findMonthlyExpensesByUser(@NotNull String username, @NotNull LocalDate date) {
		Objects.requireNonNull(username, "username");
		Objects.requireNonNull(date, "date");

		if (!DateUtils.isInSameMonthAndYear(date, LocalDate.now())) {
			date = date.isBefore(LocalDate.now())
					? DateUtils.lastDayOfMonth(date)
					: DateUtils.firstDayOfMonth(date);
		}

		return expenseRepository.findMonthlyExpenses(username, date);
	}
	//</editor-fold>

	@Transactional
	public Expense saveExpense(@NotNull Expense expense) {
		Objects.requireNonNull(expense, "expense");
		validator.validate(expense);

		replaceExpireDateForOneTimeExpenses(expense);
		System.out.println("Saving " + expense);

		return expenseRepository.save(expense);
	}

	/**
	 * Updates the expireDate to be startDate + 1 day, if the expense timestamp is ONCE
	 */
	private void replaceExpireDateForOneTimeExpenses(Expense expense) {
		if (!expense.getTimestamp().equals(ExpenseTimestamp.ONCE)) {
			return;
		}

		LocalDate startDate = expense.getStartDate();
		expense.setExpireDate(startDate.plusDays(1));
	}

	public void deleteExpenseById(long expenseId) {
		expenseRepository.deleteById(expenseId);
	}

	public void deleteAllExpanses() {
		expenseRepository.deleteAll();
	}


}
