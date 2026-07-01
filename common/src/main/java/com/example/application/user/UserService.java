package com.example.application.user;

import com.example.application.EntityValidator;
import com.example.application.InternalUnexpectedException;
import com.example.application.portfolio.Portfolio;
import com.example.application.user.domain.RegisterUserRequest;
import com.example.application.user.exceptions.UsernameTakenException;
import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityNotFoundException;
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
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final EntityValidator validator;

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

	public boolean isUsernameTaken(@NotNull String username) {
		return userRepository.findByUsernameIgnoreCase(username).isPresent();
	}

	public boolean isEmailTaken(@NotNull String email) {
		return userRepository.findByEmailIgnoreCase(email).isPresent();
	}

	@Nonnull
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

	@Nonnull
	@Transactional
	public User createNewUser(@Validated @NotNull RegisterUserRequest request) {
		log.info("Creating new user: {}", request);

		if (!request.getPassword().equals(request.getConfirmPassword()))
			throw new IllegalArgumentException("User register passwords does not match");

		User user = new User();
		user.setUsername(request.getUsername().trim().toLowerCase());
		user.setEmail(request.getEmail().trim().toLowerCase());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.getRoles().add(UserRole.USER_ROLE);

		return save(user);
	}

	@Transactional
	public void addPortfolio(@NotNull Long userId, @NotNull Portfolio portfolio) {
		log.info("Adding portfolio '{}' to user: #{}", portfolio.getName(), userId);
		User user = findById(userId)
				.orElseThrow(() -> new UsernameNotFoundException("User #" + userId + " not found."));

		user.addPortfolio(portfolio);
		log.info("Portfolio '{}' is added for user: #{}", portfolio.getName(), userId);
	}

	@Transactional
	public void removePortfolio(@NotNull Long userId, @NotNull Portfolio portfolio) {
		log.info("Removing portfolio '{}' from user: #{}", portfolio.getName(), userId);
		User user = findById(userId)
				.orElseThrow(() -> new UsernameNotFoundException("User #" + userId + " not found."));

		user.removePortfolio(portfolio);
		log.info("Portfolio '{}' is removed from user: #{}", portfolio.getName(), userId);
	}

	@Transactional
	public void setPortfolioAsActive(@NotNull Long userId, @NotNull Portfolio portfolio) {
		log.info("Setting portfolio '{}' as active for user: #{}", portfolio.getName(), userId);
		User user = findById(userId)
				.orElseThrow(() -> new UsernameNotFoundException("User #" + userId + " not found."));

		user.setActivePortfolio(portfolio);
		log.info("Portfolio '{}' is marked as active for user: #{}", portfolio.getName(), userId);
	}

	@NotNull
	@Transactional
	public User save(@NotNull User user) {
		log.info("Saving {}", user);
		validator.validate(user);

		if (isUsernameTaken(user.getUsername()))
			throw new UsernameTakenException("There is already a user with this username");

		if (isEmailTaken(user.getEmail()))
			throw new UsernameTakenException("There is already a user with this email");

		try {
			user.setLastTimeUpdated(LocalDateTime.now());
			User savedUser = userRepository.save(user);
			log.info("Saved successfully {}", savedUser);
			return savedUser;
		} catch (Exception e) {
			log.error("Failed to save {}", user.toFullString(), e);
			throw new InternalUnexpectedException(e);
		}
	}

	@Transactional
	public void delete(@NotNull Long userId) {
		log.info("Deleting user :#{}", userId);
		User user = findById(userId)
				.orElseThrow(() -> new EntityNotFoundException("Cannot delete an unexistent user: #" + userId));

		try {
			userRepository.delete(user);
			log.info("Deleted successfully user: #{}", userId);
		} catch (Exception e) {
			log.error("Failed to delete user: #{}", userId, e);
			throw new InternalUnexpectedException(e);
		}
	}

	@Transactional
	public void deleteAll(@NotNull List<User> users) {
		int numberOfTransactions = users.size();
		try {
			log.info("Deleting {} users", numberOfTransactions);
			userRepository.deleteAll(users);
			log.info("Deleted successfully {} users", numberOfTransactions);
		} catch (Exception e) {
			log.error("Failed to delete {} users", numberOfTransactions, e);
			throw new InternalUnexpectedException(e);
		}
	}

}