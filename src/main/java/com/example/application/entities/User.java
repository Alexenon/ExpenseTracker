package com.example.application.entities;

import com.example.application.entities.crypto.Portfolio;
import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Override
    public String toString() {
        return "User{username='%s', email='%s', roles=%s, id=%d}".formatted(username, email, roles, id);
    }
}