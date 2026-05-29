package com.example.application.repositories.expenses;

import com.example.application.entities.expenses.ExpenseTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseTagRepository extends JpaRepository<ExpenseTag, Long> {

	@Query(value = """
			         SELECT * FROM expense_tags ET
			         WHERE ET.expense_id = :expenseId
			""", nativeQuery = true)
	List<ExpenseTag> findByExpense(@Param("expenseId") Long expenseId);

}
