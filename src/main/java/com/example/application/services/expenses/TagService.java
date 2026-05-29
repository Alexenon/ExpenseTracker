package com.example.application.services.expenses;

import com.example.application.components.EntityValidator;
import com.example.application.entities.expenses.Tag;
import com.example.application.repositories.expenses.TagRepository;
import com.example.application.services.UserService;
import com.example.application.utils.exceptions.InternalUnexpectedException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

	public Optional<Tag> findByName(@NotNull String name) {
		Objects.requireNonNull(name, "name");
		return tagRepository.findByName(name);
	}
	//</editor-fold>

	@Transactional
	public Tag save(@NotNull Tag tag) {
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

		try {
			tagRepository.delete(tag);
		} catch (Exception e) {
			throw new InternalUnexpectedException(e);
		}
	}

}
