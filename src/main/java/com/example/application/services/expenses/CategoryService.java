package com.example.application.services.expenses;

import com.example.application.components.EntityValidator;
import com.example.application.entities.expenses.Category;
import com.example.application.repositories.expenses.CategoryRepository;
import com.example.application.utils.exceptions.InternalUnexpectedException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

	private final CategoryRepository categoryRepository;
	private final EntityValidator validator;

	public Optional<Category> findById(@NotNull Long id) {
		Objects.requireNonNull(id, "id");
		return categoryRepository.findById(id);
	}

	public Optional<Category> findByName(@NotNull String name) {
		Objects.requireNonNull(name, "name");
		return categoryRepository.findByName(name);
	}

	public Optional<Category> findByNameAndUser(@NotNull String name, @NotNull Long userId) {
		Objects.requireNonNull(name, "name");
		Objects.requireNonNull(userId, "userId");
		return categoryRepository.findByNameAndUser(name, userId);
	}

	public List<Category> findByUser(@NotNull Long userId) {
		Objects.requireNonNull(userId, "userId");
		return categoryRepository.findByUser(userId);
	}

	public boolean isNameTaken(String name, Long userId) {
		return categoryRepository.findByNameAndUser(name, userId).isPresent();
	}

	public boolean isIconTaken(String iconName, Long userId) {
		return categoryRepository.findByIconAndUser(iconName, userId).isPresent();
	}

	@Transactional
	public Category save(@NotNull Category category) {
		validator.validate(category);

		try {
			Category entity = categoryRepository.save(category);
			log.info("Saved successfully {}", entity);
			return entity;
		} catch (Exception e) {
			log.error("Failed to save {}", category, e);
			throw new InternalUnexpectedException(e);
		}
	}

	@Transactional
	public void delete(@NotNull Long categoryId) {
		log.info("Deleting category: #{}", categoryId);
		Category category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new EntityNotFoundException("Cannot find category by id: #" + categoryId));

		try {
			categoryRepository.delete(category);
			log.info("Deleted successfully category: #{}", categoryId);
		} catch (Exception e) {
			log.error("Failed to delete category: #{}", category, e);
			throw new InternalUnexpectedException(e);
		}
	}

}