package com.example.application.services;

import com.example.application.data.requests.RegisterUserRequest;
import com.example.application.entities.User;
import com.example.application.repositories.UserRepository;
import com.example.application.services.crypto.PortfolioService;
import com.example.application.utils.exceptions.auth.UsernameTakenException;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

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
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(Collections.singleton(User.Role.USER_ROLE));

        return createNewUser(user);
    }

    @Transactional
    public User createNewUser(User user) {
        if (isUsernameTaken(user.getUsername()))
            throw new UsernameTakenException("There is already a user with this username");

        if (isEmailTaken(user.getEmail()))
            throw new UsernameTakenException("There is already a user with this email");

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Collections.singleton(User.Role.USER_ROLE));
        user.setEmail(user.getEmail().trim().toLowerCase());
        User savedUser = userRepository.save(user);
        portfolioService.createPortfolio(user);

        return savedUser;
    }

    public boolean isUsernameTaken(String username) {
        return userRepository.findByUsernameIgnoreCase(username).isPresent();
    }

    public boolean isEmailTaken(String email) {
        return userRepository.findByEmailIgnoreCase(email).isPresent();
    }


}
