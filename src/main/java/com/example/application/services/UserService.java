package com.example.application.services;

import com.example.application.data.models.crypto.Wallet;
import com.example.application.data.requests.RegisterUserRequest;
import com.example.application.entities.User;
import com.example.application.repositories.UserRepository;
import com.example.application.services.crypto.WalletService;
import com.example.application.utils.exceptions.UserExistException;
import jakarta.transaction.Transactional;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final WalletService walletService;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       WalletService walletService,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.walletService = walletService;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> findByUsernameIgnoreCase(String username) {
        return userRepository.findByUsernameIgnoreCase(username);
    }

    public Optional<User> findByEmailIgnoreCase(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }

    public Optional<User> findByUsernameOrEmailIgnoreCase(String usernameOrEmail) {
        Optional<User> userByUsername = userRepository.findByUsernameIgnoreCase(usernameOrEmail);
        if (userByUsername.isPresent()) {
            return userByUsername;
        } else {
            return userRepository.findByEmailIgnoreCase(usernameOrEmail);
        }
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User user = findByUsernameOrEmailIgnoreCase(usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException(usernameOrEmail + " not found."));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.name())).toList()
        );
    }

    public User createNewUser(RegisterUserRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(Collections.singleton(User.Role.USER_ROLE));

        return createNewUser(user);
    }

    @Transactional
    public User createNewUser(User user) {
        if (findByUsernameIgnoreCase(user.getUsername()).isPresent()) {
            throw new UserExistException("There is already a user with this username");
        }
        if (findByEmailIgnoreCase(user.getEmail()).isPresent()) {
            throw new UserExistException("There is already a user with this email");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Collections.singleton(User.Role.USER_ROLE));
        user.setEmail(user.getEmail().trim().toLowerCase());
        User savedUser = userRepository.save(user);

        // Create and attach a new wallet to this user
        Wallet wallet = new Wallet();
        wallet.setUser(savedUser);
        walletService.saveWallet(wallet);

        return savedUser;
    }

}
