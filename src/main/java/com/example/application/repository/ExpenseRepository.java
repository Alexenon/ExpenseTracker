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
            WHERE MONTH(E.date) = :month
            """, nativeQuery = true)
    List<ExpenseDTO> findExpensesPerMonth(@Param("month") int month);

    @Query(value = """
            SELECT E.id, E.name, E.amount, C.name AS 'Category',
                E.description, T.name AS 'Timestamp', E.date
            FROM expense E
            INNER JOIN category C ON C.id = E.category_id
            INNER JOIN timestamp T ON T.id = E.timestamp_id
            WHERE YEAR(E.date) = :year
            """, nativeQuery = true)
    List<ExpenseDTO> findExpensesPerYear(@Param("year") int year);

    @Query(value = """
            SELECT E.id, E.name, E.amount, C.name AS 'Category',
                E.description, T.name AS 'Timestamp', E.date
            FROM expense E
            INNER JOIN category C ON C.id = E.category_id
            INNER JOIN timestamp T ON T.id = E.timestamp_id
            WHERE C.name LIKE CONCAT('%', :categoryName, '%')
            """, nativeQuery = true)
    List<ExpenseDTO> findByCategory(@Param("categoryName") String categoryName);

    @Query(value = """
        SELECT C.name, SUM(
            CASE
                WHEN T.name = 'DAILY' THEN DAY(LAST_DAY(CURRENT_DATE))
                WHEN T.name = 'WEEKLY' THEN WEEK(CURRENT_DATE)
                WHEN T.name = 'MONTHLY' THEN 1
                ELSE 1
            END
        ) as 'total times'
        FROM expense E
        INNER JOIN category C ON C.id = E.category_id
        INNER JOIN timestamp T ON T.id = E.timestamp_id
        GROUP BY C.name
        """, nativeQuery = true)
    List<Object[]> find();

    @Query(value = """
            SELECT C.name, SUM(
                CASE
                    WHEN T.name = 'DAILY' THEN E.amount * DAY(LAST_DAY(CURRENT_DATE))
                    WHEN T.name = 'WEEKLY' THEN E.amount * WEEK(CURRENT_DATE)
                    WHEN T.name = 'MONTHLY' THEN E.amount
                    ELSE E.amount
                END
            ) as 'total spent'
            FROM expense E
            INNER JOIN category C ON C.id = E.category_id
            INNER JOIN timestamp T ON T.id = E.timestamp_id
            WHERE MONTH(E.date) = :month
            GROUP BY C.name
            """, nativeQuery = true)
    List<Object[]> findGroupedCategoriesWithTotalSumsByMonth(@Param("month") int month);

}
