package com.example.application.services.expenses;

import com.example.application.components.EntityValidator;
import com.example.application.entities.expenses.ExpenseTag;
import com.example.application.repositories.expenses.ExpenseTagRepository;
import com.example.application.utils.exceptions.InternalUnexpectedException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExpenseTagService {

	private final ExpenseTagRepository expenseTagRepository;
	private final EntityValidator validator;

	public Optional<ExpenseTag> findById(Long id) {
		Objects.requireNonNull(id, "expenseTagId");
		return expenseTagRepository.findById(id);
	}

	public List<ExpenseTag> findByExpense(Long expenseId) {
		Objects.requireNonNull(expenseId, "expenseId");
		return expenseTagRepository.findByExpense(expenseId);
	}

	@Transactional
	public ExpenseTag save(@NotNull ExpenseTag tag) {
		validator.validate(tag);
		try {
			return expenseTagRepository.save(tag);
		} catch (Exception e) {
			throw new InternalUnexpectedException(e);
		}
	}

	@Transactional
	public void delete(@NotNull Long tagId) {
		ExpenseTag tag = findById(tagId)
				.orElseThrow(() -> new EntityNotFoundException("Cannot delete an unexistent expense tag: #" + tagId));

		try {
			expenseTagRepository.delete(tag);
		} catch (Exception e) {
			throw new InternalUnexpectedException(e);
		}
	}


}
