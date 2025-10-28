package com.example.application.services;

import com.example.application.data.requests.RegisterUserRequest;
import com.example.application.entities.User;
import com.example.application.entities.crypto.Portfolio;
import com.example.application.repositories.UserRepository;
import com.example.application.services.crypto.PortfolioService;
import com.example.application.utils.common.lang.StringUtils;
import com.example.application.utils.exceptions.InternalUnexpectedException;
import com.example.application.utils.exceptions.auth.UsernameTakenException;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/*
    TODO:
     [?] Don't allow spaces in the username / email
* */


@Slf4j
@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PortfolioService portfolioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @NotNull
    public Optional<User> findByUsername(@NotNull String username) {
        return userRepository.findByUsernameIgnoreCase(Objects.requireNonNull(username, "username"));
    }

    @NotNull
    public Optional<User> findByEmail(@NotNull String email) {
        return userRepository.findByEmailIgnoreCase(Objects.requireNonNull(email, "email"));
    }

    @NotNull
    public Optional<User> findByUsernameOrEmail(@NotNull String usernameOrEmail) {
        Objects.requireNonNull(usernameOrEmail, "usernameOrEmail");
        Optional<User> userByUsername = userRepository.findByUsernameIgnoreCase(usernameOrEmail);
        return userByUsername.isPresent()
                ? userByUsername
                : userRepository.findByEmailIgnoreCase(usernameOrEmail);
    }

    @NotNull
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User user = findByUsernameOrEmail(usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException(usernameOrEmail + " not found."));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.name())).toList()
        );
    }

    public User createNewUser(RegisterUserRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword()))
            throw new IllegalArgumentException("User register passwords does not match");

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        return createNewUser(user);
    }

    public User createNewUser(@NotNull User user) {
        User newUser = save(user);
        attachDefaultPortfolio(newUser);
        return newUser;
    }

    @NotNull
    @Transactional
    public User save(@NotNull User user) {
        try {
            validate(user);
            updateFields(user);
            User savedUser = userRepository.save(user);
            log.info("Saved successfully {}", savedUser);
            return savedUser;
        } catch (Exception e) {
            log.error("Failed to save {}, cause: {}", user, e.getMessage());
            throw new InternalUnexpectedException(e);
        }
    }

    private void validate(User user) {
        Objects.requireNonNull(user, "User cannot be null");
        Assert.isTrue(StringUtils.isNotBlank(user.getUsername()), "username is missing");
        Assert.isTrue(StringUtils.isNotBlank(user.getEmail()), "email is missing");
        Assert.isTrue(StringUtils.isNotBlank(user.getPassword()), "password is missing");

        if (isUsernameTaken(user.getUsername()))
            throw new UsernameTakenException("There is already a user with this username");

        if (isEmailTaken(user.getEmail()))
            throw new UsernameTakenException("There is already a user with this email");
    }

    private void updateFields(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEmail(user.getEmail().trim().toLowerCase());
        user.setRoles(Set.of(User.Role.USER_ROLE));
    }

    public boolean isUsernameTaken(String username) {
        return userRepository.findByUsernameIgnoreCase(username).isPresent();
    }

    public boolean isEmailTaken(String email) {
        return userRepository.findByEmailIgnoreCase(email).isPresent();
    }

    /**
     * Creates and attach a new portfolio to the provided user.
     */
    private Portfolio attachDefaultPortfolio(@NotNull User user) {
        return portfolioService.createNewPortfolio("Main", user);
    }

}