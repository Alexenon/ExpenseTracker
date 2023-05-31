package com.example.application.repositories;

import com.example.application.entities.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

//    @Query("SELECT e FROM Expense e " +
//            "WHERE lower(e.name) LIKE lower(concat('%', :value, '%')) " +
//            "OR lower(e.amount) LIKE lower(concat('%', :value, '%')) " +
//            "OR lower(e.description) LIKE lower(concat('%', :value, '%')) " +
//            "OR lower(e.expenseInterval) LIKE lower(concat('%', :value, '%')) " +
//            "OR lower(e.date) LIKE lower(concat('%', :value, '%'))")
//    List<Expense> searchIgnoreCase(@Param("value") String value);


}
