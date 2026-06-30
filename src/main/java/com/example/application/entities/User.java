package com.example.application.entities;

import com.example.application.entities.crypto.Portfolio;
import com.example.application.utils.exceptions.InternalUnexpectedException;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.*;

@EqualsAndHashCode(of = {"id", "username", "email"})
@Data
@Entity(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@NotBlank
	@Column(name = "username", unique = true, nullable = false)
	@Size(min = 4, max = 255, message = "Username should be between 4 and 20 characters")
	private String username;

	@NotBlank
	@Column(name = "password", nullable = false)
	@Size(min = 4, max = 128, message = "Password should be between 4 and 20 characters")
	private String password;

	@NotBlank
	@Email
	@Column(name = "email", unique = true, nullable = false)
	@Size(min = 4, max = 320, message = "Email should be between 4 and 320 characters")
	private String email;

	@Nullable
	@OneToOne(
			fetch = FetchType.LAZY,
			cascade = {CascadeType.PERSIST, CascadeType.MERGE}
	)
	@JoinColumn(name = "active_portfolio_id", unique = true)
	private Portfolio activePortfolio;

	@OneToMany(
			mappedBy = "user",
			cascade = CascadeType.ALL
	)
	private List<Portfolio> portfolios = new ArrayList<>();

	@Column(name = "role", nullable = false)
	@Enumerated(EnumType.STRING)
	@ElementCollection(targetClass = UserRole.class, fetch = FetchType.EAGER)
	@CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
	private Set<UserRole> roles = new HashSet<>();

	@Column(name = "last_time_updated", nullable = false)
	private LocalDateTime lastTimeUpdated = LocalDateTime.now();

	@Column(name = "time_created_at", nullable = false, updatable = false)
	private final LocalDateTime timeCreatedAt = LocalDateTime.now();

	//<editor-fold desc="UTILS">
	public boolean isNew() {
		return id == null;
	}

	public void addPortfolio(@NotNull Portfolio portfolio) {
		Objects.requireNonNull(portfolio, "portfolio");

		if (portfolio.getUser() != null && portfolio.getUser() != this)
			throw new IllegalStateException("Portfolio already belongs to another user");

		portfolio.setUser(this);
		portfolios.add(portfolio);

		if (activePortfolio == null) {
			setActivePortfolio(portfolio);
		}
	}

	public void removePortfolio(@NotNull Portfolio portfolio) {
		Objects.requireNonNull(portfolio, "portfolio");

		if (!portfolios.contains(portfolio))
			throw new InternalUnexpectedException("Portfolio does not belong to user");

		if (portfolios.size() == 1)
			throw new IllegalArgumentException("Cannot remove last portfolio");

		portfolio.setUser(null);
		portfolios.remove(portfolio);

		if (portfolio.equals(activePortfolio)) {
			setActivePortfolio(portfolios.getFirst());
		}
	}

	@Transactional
	public void resetActivePortfolio() {
		this.activePortfolio = null;
	}

	@Transactional
	public void setActivePortfolio(@NotNull Portfolio portfolio) {
		Objects.requireNonNull(portfolio, "portfolio");

		if (!portfolios.isEmpty() && !portfolios.contains(portfolio))
			throw new InternalUnexpectedException("Cannot set portfolio as active, because user doesn't have such portfolio");

		this.activePortfolio = portfolio;
	}

	@Override
	public String toString() {
		return "User{username='%s', email='%s', id=%d}".formatted(username, email, id);
	}

	public String toFullString() {
		return new StringJoiner(", ", User.class.getSimpleName() + "[", "]")
				.add("id=" + id)
				.add("username='" + username + "'")
				.add("password='" + password + "'")
				.add("email='" + email + "'")
				.add("activePortfolio=" + activePortfolio)
				.add("portfolios=" + portfolios)
				.add("roles=" + roles)
				.add("timeCreatedAt=" + timeCreatedAt)
				.add("lastTimeUpdated=" + lastTimeUpdated)
				.toString();
	}
	//</editor-fold>
}