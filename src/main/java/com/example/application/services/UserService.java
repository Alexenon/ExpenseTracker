package com.example.application.services;

import com.example.application.entities.Role;
import com.example.application.entities.User;
import com.example.application.exceptions.UserAlreadyExistException;
import com.example.application.repositories.RoleRepository;
import com.example.application.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@SuppressWarnings("ClassCanBeRecord")
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + " not found."));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).toList()
        );
    }

    public void createNewUser(User user) {
        if(userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new UserAlreadyExistException();
        }

        Role role_user = roleRepository.findByName("ROLE_USER").orElseThrow();
        user.setRoles(List.of(role_user));
        userRepository.save(user);
    }
}
