package com.example.application.services;

import com.example.application.dtos.RegistrationUserDTO;
import com.example.application.entities.User;
import com.example.application.exceptions.UserAlreadyExistException;
import com.example.application.repositories.RoleRepository;
import com.example.application.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
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
                user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).toList()
        );
    }

    public User createNewUser(RegistrationUserDTO registrationUserDto) {
        if (findByUsernameIgnoreCase(registrationUserDto.getUsername()).isPresent()) {
            throw new UserAlreadyExistException("There is already a user with this username");
        }

        User user = new User();
        user.setUsername(registrationUserDto.getUsername());
        user.setEmail(registrationUserDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationUserDto.getPassword()));
        user.setRoles(List.of(roleRepository.findByName("ROLE_USER").orElseThrow()));
        return userRepository.save(user);
    }

    public User createNewUser(User user) {
        if (findByUsernameIgnoreCase(user.getUsername()).isPresent()) {
            throw new UserAlreadyExistException("There is already a user with this username");
        }
        if (findByEmailIgnoreCase(user.getEmail()).isPresent()) {
            throw new UserAlreadyExistException("There is already a user with this email");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(List.of(roleRepository.findByName("ROLE_USER").orElseThrow()));
        user.setEmail(user.getEmail().toLowerCase());

        return userRepository.save(user);
    }
}