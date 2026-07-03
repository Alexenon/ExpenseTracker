package com.example.application.expense;

import com.example.application.category.Category;
import com.example.application.tag.Tag;
import com.example.application.user.User;
import com.example.application.utils.CommonFormatters;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

/*
	TODO: Add field that says that the expense is either 'Must Have' or 'Wants'
		so user can filter out what expenses he could avoid next time
* */

@Data
@Entity(name = "expenses")
public class Expense {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name", nullable = false)
	@Size(min = 4, max = 50, message = "Expense name should be between 4 and 20 characters")
	private String name;

	@Column(name = "description")
	@Size(max = 250, message = "Description should not exceed 250 characters")
	private String description;

	@Column(name = "amount", nullable = false)
	@DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
	private double amount;

	@ManyToOne
	@JoinColumn(name = "category_id", nullable = false)
	private Category category;

	@ManyToMany
	@JoinTable(
			name = "expense_tags",
			joinColumns = @JoinColumn(name = "expense_id"),
			inverseJoinColumns = @JoinColumn(name = "tag_id"),
			uniqueConstraints = @UniqueConstraint(columnNames = {"expense_id", "tag_id"})
	)
	private Set<Tag> tags = new HashSet<>();

	@Column(name = "timestamp", nullable = false)
	@Enumerated(EnumType.STRING)
	private ExpenseTimestamp timestamp = ExpenseTimestamp.ONCE;

	@Column(name = "start_date", nullable = false)
	private LocalDate startDate = LocalDate.now();

	@Column(name = "expire_date")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate expireDate;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Override
	public String toString() {
		return new StringJoiner(", ", Expense.class.getSimpleName() + "[", "]")
				.add("id=" + id)
				.add("name='" + name + "'")
				.add("amount=" + amount)
				.add("description='" + description + "'")
				.add("category='" + category.getName() + "'")
				.add("tags=" + tags)
				.add("timestamp=" + timestamp)
				.add("startDate=" + (startDate == null ? "N/A" : CommonFormatters.DATE.format(startDate)))
				.add("expireDate=" + (expireDate == null ? "N/A" : CommonFormatters.DATE.format(expireDate)))
				.add("user=" + user)
				.toString();
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Expense expense)) return false;

		return id != null && id.equals(expense.id);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}

}
