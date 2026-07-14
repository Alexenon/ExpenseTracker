package com.example.application.user;

import com.example.application.InternalUnexpectedException;
import com.example.application.portfolio.Portfolio;
import com.example.application.user.domain.RegisterUserRequest;
import com.example.application.user.domain.UpdateUserPasswordRequest;
import com.example.application.user.domain.UpdateUserRequest;
import com.example.application.user.exceptions.EmailTakenException;
import com.example.application.user.exceptions.UsernameTakenException;
import com.example.application.utils.EntityValidator;
import com.example.application.utils.lang.StringUtils;
import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityNotFoundException;
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

	public boolean isPasswordCorrect(String password, Long userId) {
		if (StringUtils.isBlank(password))
			return false;

		User user = findById(userId)
				.orElseThrow(() -> new EntityNotFoundException("User #%s not found".formatted(userId)));

		return passwordEncoder.encode(password).equals(user.getPassword());
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
	public User createNewUser(@Validated @NotNull RegisterUserRequest request) {
		log.info("Creating new user: {}", request);

		if (!request.getPassword().equals(request.getConfirmPassword()))
			throw new IllegalArgumentException("User register passwords does not match");

		if (isUsernameTaken(request.getUsername()))
			throw new UsernameTakenException("There is already a user with this username");

		if (isEmailTaken(request.getEmail()))
			throw new EmailTakenException("There is already a user with this email");

		User user = new User();
		user.setUsername(request.getUsername().trim().toLowerCase());
		user.setEmail(request.getEmail().trim().toLowerCase());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.getRoles().add(UserRole.USER_ROLE);

		return save(user);
	}

	@Nonnull
	public User updateUser(@Validated @NotNull UpdateUserRequest request) {
		log.info("Updating user: {}", request);

		validator.validate(request);
		User user = findById(request.getUserId())
				.orElseThrow(() -> new EntityNotFoundException("User not found: #" + request.getUserId()));

		if (!user.getUsername().equals(request.getUsername()) && isUsernameTaken(request.getUsername()))
			throw new UsernameTakenException("There is already a user with this username");

		if (!user.getEmail().equals(request.getEmail()) && isEmailTaken(request.getEmail()))
			throw new EmailTakenException("There is already a user with this email");

		user.setUsername(request.getUsername());
		user.setEmail(request.getEmail());
		return save(user);
	}

	public void addPortfolio(@NotNull Long userId, @NotNull Portfolio portfolio) {
		log.info("Adding portfolio '{}' to user: #{}", portfolio.getName(), userId);
		User user = findById(userId)
				.orElseThrow(() -> new UsernameNotFoundException("User #" + userId + " not found."));

		user.addPortfolio(portfolio);
		log.info("Portfolio '{}' is added for user: #{}", portfolio.getName(), userId);
	}

	public void removePortfolio(@NotNull Long userId, @NotNull Portfolio portfolio) {
		log.info("Removing portfolio '{}' from user: #{}", portfolio.getName(), userId);
		User user = findById(userId)
				.orElseThrow(() -> new UsernameNotFoundException("User #" + userId + " not found."));

		user.removePortfolio(portfolio);
		log.info("Portfolio '{}' is removed from user: #{}", portfolio.getName(), userId);
	}

	public void setPortfolioAsActive(@NotNull Long userId, @NotNull Portfolio portfolio) {
		log.info("Setting portfolio '{}' as active for user: #{}", portfolio.getName(), userId);
		User user = findById(userId)
				.orElseThrow(() -> new UsernameNotFoundException("User #" + userId + " not found."));

		user.setActivePortfolio(portfolio);
		log.info("Portfolio '{}' is marked as active for user: #{}", portfolio.getName(), userId);
	}

	/**
	 * @return true if passsword was updated with a new one, false in case it's the same
	 */
	public boolean changePassword(UpdateUserPasswordRequest request) {
		log.info("Changing password for user: #{}", request.getUserId());
		validator.validate(request);

		Long userId = request.getUserId();
		User user = findById(userId)
				.orElseThrow(() -> new UsernameNotFoundException("User #" + userId + " not found."));

		String oldEncodedPassword = user.getPassword();
		String newEncodedPassword = passwordEncoder.encode(request.getPassword());

		if (passwordEncoder.matches(request.getPassword(), oldEncodedPassword)) {
			log.info("Password change skipped for user: #{}, the new password matches the current password.", userId);
			return false;
		}

		user.setPassword(newEncodedPassword);
		save(user);
		log.info("Updated successfully password for user: #{}", request.getUserId());
		return true;
	}

	public User save(@NotNull User user) {
		log.info("Saving {}", user);
		validator.validate(user);

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