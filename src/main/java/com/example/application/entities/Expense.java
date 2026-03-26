package com.example.application.entities;

import com.example.application.utils.common.formatters.CommonFormatters;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.StringJoiner;

@Data
@Entity(name = "expenses")
@EqualsAndHashCode(of = {"id", "name", "category", "user"})
public class Expense {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name", nullable = false)
	@Size(min = 4, max = 20, message = "Name should be between 4 and 20 characters")
	private String name;

	@Column(name = "amount", nullable = false)
	private double amount;

	@Column(name = "description")
	@Size(max = 250, message = "Description should not exceed 250 characters")
	private String description;

	@Column(name = "start_date", nullable = false)
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate startDate = LocalDate.now();

	@Column(name = "expire_date")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate expireDate;

	@Column(name = "timestamp", nullable = false)
	@Enumerated(EnumType.STRING)
	private ExpenseTimestamp timestamp = ExpenseTimestamp.ONCE;

	@ManyToOne
	@JoinColumn(name = "category_id", nullable = false)
	private Category category;

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
				.add("startDate=" + (startDate == null ? "N/A" : CommonFormatters.DATE.format(startDate)))
				.add("expireDate=" + (expireDate == null ? "N/A" : CommonFormatters.DATE.format(expireDate)))
				.add("timestamp=" + timestamp)
				.add("category=" + category)
				.add("user=" + user)
				.toString();
	}

}

