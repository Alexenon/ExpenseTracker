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
	public Optional<Tag> findById(@NotNull Long tagId) {
		Objects.requireNonNull(tagId, "tagId");
		return tagRepository.findById(tagId);
	}

	public List<Tag> findByExpense(@NotNull Long expenseId) {
		Objects.requireNonNull(expenseId, "expenseId");
		return tagRepository.findByExpense(expenseId);
	}

	public List<Tag> findByName(@NotNull String name) {
		Objects.requireNonNull(name, "name");
		return tagRepository.findByName(name);
	}

	public List<Tag> findByUser(@NotNull Long userId) {
		Objects.requireNonNull(userId, "userId");
		return tagRepository.findByUser(userId);
	}

	public Optional<Tag> findByNameAndUser(@NotNull String name, @NotNull Long userId) {
		Objects.requireNonNull(name, "name");
		Objects.requireNonNull(userId, "userId");
		return tagRepository.findByNameAndUser(name, userId);
	}

	public Tag findByNameAndUserOrCreate(@NotNull String name, @NotNull User user) {
		Objects.requireNonNull(name, "name");
		Objects.requireNonNull(user, "user");
		return tagRepository.findByNameAndUser(name, user.getId())
				.orElseGet(() -> save(new Tag(name, user)));
	}

	public boolean isNameTaken(String tagName, Long userId) {
		return findByNameAndUser(tagName, userId).isPresent();
	}
	//</editor-fold>

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

	public void delete(@NotNull Long tagId) {
		Tag tag = findById(tagId)
				.orElseThrow(() -> new EntityNotFoundException("Cannot delete an unexistent tag: #" + tagId));
		log.info("Deleting tag :#{}", tagId);
		try {
			tagRepository.delete(tag);
		} catch (Exception e) {
			throw new InternalUnexpectedException(e);
		}
	}

}
