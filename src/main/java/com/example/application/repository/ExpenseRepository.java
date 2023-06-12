package com.example.application.repository;

import com.example.application.model.Expense;
import com.example.application.model.ExpenseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query("""
            select e.name, e.amount, c.name, e.description, t.name
            from Expense e
            inner join e.category c
            inner join e.timestamp t
            """)
    List<ExpenseDTO> getAll();

}
