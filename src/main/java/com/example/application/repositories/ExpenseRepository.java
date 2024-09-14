package com.example.application.repositories;

import com.example.application.data.dtos.ExpenseDTO;
import com.example.application.data.dtos.projections.MonthlyExpensesProjection;
import com.example.application.entities.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/*
 * TODO: ADD VIEW TO EXCLUDE ADDING ALIASES EVERYWHERE
 * */

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query(value = """
            SELECT E.id, E.name, E.amount, C.name as 'Category',
                E.description, E.timestamp, E.start_date
            FROM expense E
            INNER JOIN category C ON C.id = E.category_id
            """, nativeQuery = true)
    List<ExpenseDTO> getAll();

    @Query(value = """
            SELECT E.id, E.name, E.amount, C.name as 'Category',
                E.description, E.timestamp, E.start_date as 'startDate', E.expire_date as 'expireDate'
            FROM expense E
                INNER JOIN users U ON U.id = E.user_id
                INNER JOIN category C ON C.id = E.category_id
            WHERE U.username = :userEmailOrUsername OR U.email = :userEmailOrUsername
            """, nativeQuery = true)
    List<ExpenseDTO> getAll(@Param("userEmailOrUsername") String userEmailOrUsername);

    @Query(value = """
            SELECT E.id, E.name, E.amount, C.name AS 'Category',
                E.description, E.timestamp, E.start_date
            FROM expense E
            INNER JOIN category C ON C.id = E.category_id
            WHERE MONTH(E.start_date) = :month
            """, nativeQuery = true)
    List<ExpenseDTO> findExpensesPerMonth(@Param("month") int month);

    @Query(value = """
            SELECT E.id, E.name, E.amount, C.name AS 'Category',
                E.description, E.timestamp, E.start_date
            FROM expense E
            INNER JOIN category C ON C.id = E.category_id
            WHERE YEAR(E.start_date) = :year
            """, nativeQuery = true)
    List<ExpenseDTO> findExpensesPerYear(@Param("year") int year);

    @Query(value = """
            SELECT E.id, E.name, E.amount, C.name AS 'Category',
                E.description, E.timestamp, E.start_date
            FROM expense E
            INNER JOIN category C ON C.id = E.category_id
            WHERE C.name LIKE CONCAT('%', :categoryName, '%')
            """, nativeQuery = true)
    List<ExpenseDTO> findByCategory(@Param("categoryName") String categoryName);

    @Query(value = """
            SELECT C.name, SUM(
                CASE
                    WHEN E.timestamp = 'DAILY' THEN E.amount * DAYOFYEAR(LAST_DAY(year_date))
                    WHEN E.timestamp = 'WEEKLY' THEN E.amount * FLOOR(DAYOFYEAR(LAST_DAY(year_date)) / 7)
                    WHEN E.timestamp = 'MONTHLY' THEN E.amount * 12
                    WHEN E.timestamp = 'YEARLY' THEN E.amount
                    ELSE E.amount
                END
            ) as totalSpent
            FROM (
                SELECT E.*, DATE_FORMAT(CONCAT(?2, '-01-01'), '%Y-%m-%d') AS year_date
                FROM expense E
                WHERE YEAR(E.start_date) = ?2
            ) AS E
                INNER JOIN users U ON U.id = E.user_id
                INNER JOIN category C ON C.id = E.category_id
            WHERE (E.expire_date IS NULL OR E.expire_date > year_date)
                AND (U.username = ?1 OR U.email = ?1)
            GROUP BY C.name
            """, nativeQuery = true)
    List<Object[]> findYearlyCategoriesTotalSum(String userEmailOrUsername, int year);

    @Procedure(procedureName = "GetMonthlyExpenses")
    List<MonthlyExpensesProjection> findMonthlyExpenses(@Param("username") String username,
                                                        @Param("date") LocalDate currentDate);



}

