package com.example.application.expense;

import com.example.application.expense.projections.MonthlyExpensesProjection;
import com.example.application.tag.Tag;
import com.example.application.utils.EntityValidator;
import com.example.application.utils.lang.DateUtils;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
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

	public List<Expense> findByCategory(Long categoryId) {
		return expenseRepository.findByCategory(categoryId);
	}

	public List<Expense> findByTag(Long tagId) {
		return expenseRepository.findByTag(tagId);
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

	@Transactional
	public void deleteExpenseById(long expenseId) {
		expenseRepository.deleteById(expenseId);
	}

	@Transactional
	public void deleteAllExpanses() {
		expenseRepository.deleteAll();
	}

	@Transactional
	public void removeExpenseTag(Expense expense, Tag tag) {
		log.info("Removing tag='{}' from {}", tag.getName(), expense);
		expense.getTags().remove(tag);
		saveExpense(expense);
	}

}
