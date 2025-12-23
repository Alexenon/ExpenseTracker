package com.example.application.entities;

import com.example.application.entities.crypto.Portfolio;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

@Data
@Entity(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

	@OneToOne
	@JoinColumn(name = "active_portfolio_id")
	private Portfolio activePortfolio;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Portfolio> portfolios;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    private Set<Role> roles = new HashSet<>();

    public enum Role {
        USER_ROLE,
        ADMIN_ROLE,
        SUPER_ADMIN_ROLE
    }

	@Column(name = "last_time_updated", nullable = false)
	private LocalDateTime lastTimeUpdated = LocalDateTime.now();

	@Column(name = "time_created_at", nullable = false, updatable = false)
	private final LocalDateTime timeCreatedAt = LocalDateTime.now();

	public boolean isNew() {
		return id != null;
	}

    @Override
    public String toString() {
        return "User{username='%s', email='%s', roles=%s, id=%d}".formatted(username, email, roles, id);
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
}