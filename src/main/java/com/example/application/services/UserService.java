package com.example.application.services;

import com.example.application.data.requests.RegisterUserRequest;
import com.example.application.entities.User;
import com.example.application.entities.crypto.Portfolio;
import com.example.application.repositories.UserRepository;
import com.example.application.utils.common.lang.StringUtils;
import com.example.application.utils.exceptions.InternalUnexpectedException;
import com.example.application.utils.exceptions.auth.UsernameTakenException;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

/*
    TODO: [CRITICAL]
     [?] Don't allow spaces in the username / email  ->  pattern !!!
* */

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	//<editor-fold desc="SEARCH">
	public Optional<User> findById(Long userId) {
		return userRepository.findById(Objects.requireNonNull(userId, "userId"));
	}

	public Optional<User> findByUsername(@NotNull String username) {
		return userRepository.findByUsernameIgnoreCase(Objects.requireNonNull(username, "username").trim());
	}

	public Optional<User> findByEmail(@NotNull String email) {
		return userRepository.findByEmailIgnoreCase(Objects.requireNonNull(email, "email").trim());
	}

	public Optional<User> findByUsernameOrEmail(@NotNull String usernameOrEmail) {
		Objects.requireNonNull(usernameOrEmail, "usernameOrEmail");
		Optional<User> userByUsername = userRepository.findByUsernameIgnoreCase(usernameOrEmail);
		return userByUsername.isPresent()
				? userByUsername
				: userRepository.findByEmailIgnoreCase(usernameOrEmail);
	}

	@NotNull
	@Override
	public UserDetails loadUserByUsername(String usernameOrEmail) {
		User user = findByUsername(usernameOrEmail)
				.orElseThrow(() -> new UsernameNotFoundException(usernameOrEmail + " not found."));

		return new org.springframework.security.core.userdetails.User(
				user.getUsername(),
				user.getPassword(),
				user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.name())).toList()
		);
	}
	//</editor-fold>

	@NotNull
	public User createNewUser(RegisterUserRequest request) {
		Objects.requireNonNull(request, "request");

		if (isUsernameTaken(request.getUsername()))
			throw new UsernameTakenException("There is already a user with this username");

		if (isEmailTaken(request.getEmail()))
			throw new UsernameTakenException("There is already a user with this email");

		if (!request.getPassword().equals(request.getConfirmPassword()))
			throw new IllegalArgumentException("User register passwords does not match");

		User user = new User();
		user.setUsername(request.getUsername());
		user.setEmail(request.getEmail());
		user.setPassword(request.getPassword());

		return save(user);
	}

	@Transactional
	public User addPortfolio(@NotNull Long userId, @NotNull Portfolio portfolio) {
		User user = findById(userId)
				.orElseThrow(() -> new UsernameNotFoundException("User #" + userId + " not found."));

		user.addPortfolio(portfolio);
		return save(user);
	}

	@Transactional
	public User removePortfolio(@NotNull Long userId, @NotNull Portfolio portfolio) {
		User user = findById(userId)
				.orElseThrow(() -> new UsernameNotFoundException("User #" + userId + " not found."));

		user.removePortfolio(portfolio);
		return save(user);
	}

	@Transactional
	public User setPortfolioAsActive(@NotNull Long userId, @NotNull Portfolio portfolio) {
		User user = findById(userId)
				.orElseThrow(() -> new UsernameNotFoundException("User #" + userId + " not found."));

		user.setActivePortfolio(portfolio);
		return save(user);
	}

	@NotNull
	@Transactional
	private User save(@NotNull User user) {
		try {
			validate(user);
			normalizeUserFields(user);
			User savedUser = userRepository.save(user);
			log.info("Saved successfully {}", savedUser);
			return savedUser;
		} catch (Exception e) {
			log.error("Failed to save {}", user.toFullString(), e);
			throw new InternalUnexpectedException(e);
		}
	}

	private void validate(User user) {
		Objects.requireNonNull(user, "User cannot be null");
		Assert.isTrue(StringUtils.isNotBlank(user.getUsername()), "User -> username is missing");
		Assert.isTrue(StringUtils.isNotBlank(user.getEmail()), "User -> email is missing");
		Assert.isTrue(StringUtils.isNotBlank(user.getPassword()), "User -> password is missing");
		Assert.notNull(user.getLastTimeUpdated(), "User -> lastTimeUpdated is missing");
		Assert.notNull(user.getTimeCreatedAt(), "User -> date creation is missing");
	}

	private void normalizeUserFields(User user) {
		user.setEmail(user.getEmail().trim().toLowerCase());
		user.setUsername(user.getUsername().trim().toLowerCase());
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.getRoles().add(User.Role.USER_ROLE);
		user.setLastTimeUpdated(LocalDateTime.now());
	}

	public boolean isUsernameTaken(@NotNull String username) {
		Objects.requireNonNull(username, "username");
		return userRepository.findByUsernameIgnoreCase(username).isPresent();
	}

	public boolean isEmailTaken(@NotNull String email) {
		Objects.requireNonNull(email, "email");
		return userRepository.findByEmailIgnoreCase(email).isPresent();
	}

}