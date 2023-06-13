package com.example.application.repository;

import com.example.application.model.Expense;
import com.example.application.model.ExpenseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query(value = """
            SELECT E.id, E.name, E.amount, C.name as 'Category',
                E.description, T.name as 'Timestamp', E.date
            FROM expense E
            INNER JOIN category C ON C.id = E.category_id
            INNER JOIN timestamp T ON T.id = E.timestamp_id
            """, nativeQuery = true)
    List<ExpenseDTO> getAll();

    @Query(value = """
            SELECT E.id, E.name, E.amount, C.name AS 'Category',
                E.description, T.name AS 'Timestamp', E.date
            FROM expense E
            INNER JOIN category C ON C.id = E.category_id
            INNER JOIN timestamp T ON T.id = E.timestamp_id
            WHERE E.date >= DATE_FORMAT(CURDATE(), '%Y-%m-01')
                AND E.date <= LAST_DAY(CURDATE());
            """)
    List<ExpenseDTO> getExpensesCurrentMonth();

    @Query(value = """
        SELECT E.id, E.name, E.amount, C.name AS 'Category',
            E.description, T.name AS 'Timestamp', E.date
        FROM expense E
        INNER JOIN category C ON C.id = E.category_id
        INNER JOIN timestamp T ON T.id = E.timestamp_id
        WHERE YEAR(E.date) = YEAR(CURDATE());
        """)
    List<ExpenseDTO> getExpensesCurrentYear();

    @Query(value = """
        SELECT E.id, E.name, E.amount, C.name AS 'Category',
            E.description, T.name AS 'Timestamp', E.date
        FROM expense E
        INNER JOIN category C ON C.id = E.category_id
        INNER JOIN timestamp T ON T.id = E.timestamp_id
        WHERE MONTH(E.date) = :month
        """)
    List<ExpenseDTO> getExpensesMonth(@Param("month") int month);


}
