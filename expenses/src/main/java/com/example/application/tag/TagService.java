package com.example.application.tag;

import com.example.application.InternalUnexpectedException;
import com.example.application.user.User;
import com.example.application.user.UserService;
import com.example.application.utils.EntityValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TagService {

	private final TagRepository tagRepository;
	private final UserService userService;
	private final EntityValidator validator;

	//<editor-fold desc="SEARCH">
	@Transactional(readOnly = true)
	public Optional<Tag> findById(@NotNull Long tagId) {
		Objects.requireNonNull(tagId, "tagId");
		return tagRepository.findById(tagId);
	}

	@Transactional(readOnly = true)
	public List<Tag> findByExpense(@NotNull Long expenseId) {
		Objects.requireNonNull(expenseId, "expenseId");
		return tagRepository.findByExpense(expenseId);
	}

	@Transactional(readOnly = true)
	public List<Tag> findByName(@NotNull String name) {
		Objects.requireNonNull(name, "name");
		return tagRepository.findByName(name);
	}

	@Transactional(readOnly = true)
	public List<Tag> findByUser(@NotNull Long userId) {
		Objects.requireNonNull(userId, "userId");
		return tagRepository.findByUser(userId);
	}

	@Transactional(readOnly = true)
	public Optional<Tag> findByNameAndUser(@NotNull String name, @NotNull Long userId) {
		Objects.requireNonNull(name, "name");
		Objects.requireNonNull(userId, "userId");
		return tagRepository.findByNameAndUser(name, userId);
	}

	@Transactional(readOnly = true)
	public Tag findByNameAndUserOrCreate(@NotNull String name, @NotNull User user) {
		Objects.requireNonNull(name, "name");
		Objects.requireNonNull(user, "user");
		return tagRepository.findByNameAndUser(name, user.getId())
				.orElseGet(() -> save(new Tag(name, user)));
	}

	@Transactional(readOnly = true)
	public boolean isNameTaken(String tagName, Long userId) {
		return findByNameAndUser(tagName, userId).isPresent();
	}
	//</editor-fold>

	@Transactional
	public Tag save(@NotNull Tag tag) {
		log.info("Saving: {}", tag.toFullString());
		validator.validate(tag);
		tag.setLastTimeUpdated(LocalDateTime.now());
		try {
			return tagRepository.save(tag);
		} catch (Exception e) {
			throw new InternalUnexpectedException(e);
		}
	}

	@Transactional
	public void delete(@NotNull Long tagId) {
		Tag tag = findById(tagId)
				.orElseThrow(() -> new EntityNotFoundException("Cannot delete an unexistent tag: #" + tagId));
		log.info("Deleting: {}", tag.toFullString());

		try {
			tagRepository.delete(tag);
		} catch (Exception e) {
			throw new InternalUnexpectedException(e);
		}
	}

}
