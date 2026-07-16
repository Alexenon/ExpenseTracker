package com.example.application.expense;

import com.example.application.tag.Tag;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class ExpenseDTO {

	private final Long id;
	private final String name;
	private final String description;
	private final BigDecimal amount;
	private final String category;
	private final Set<String> tags;
	private final ExpenseTimestamp timestamp;
	private final LocalDate startDate;
	private final LocalDate expireDate;
	private final Long userId;

	public ExpenseDTO(Expense expense) {
		this.id = expense.getId();
		this.name = expense.getName();
		this.amount = expense.getAmount();
		this.category = expense.getCategory().getName();
		this.tags = expense.getTags().stream().map(Tag::getName).collect(Collectors.toUnmodifiableSet());
		this.description = expense.getDescription();
		this.timestamp = expense.getTimestamp();
		this.startDate = expense.getStartDate();
		this.expireDate = expense.getExpireDate();
		this.userId = expense.getUser().getId();
	}

}
