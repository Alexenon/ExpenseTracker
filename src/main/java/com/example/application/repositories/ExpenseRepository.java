package com.example.application.repositories;

import com.example.application.dtos.ExpenseDTO;
import com.example.application.entities.Expense;
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
            SELECT E.id, E.name, E.amount, C.name as 'Category',
                E.description, T.name as 'Timestamp', E.date
            FROM expense E
                INNER JOIN users U ON U.id = E.user_id
                INNER JOIN category C ON C.id = E.category_id
                INNER JOIN timestamp T ON T.id = E.timestamp_id
            WHERE U.username LIKE CONCAT('%', :userEmailOrUsername, '%')
                OR U.email LIKE CONCAT('%', :userEmailOrUsername, '%')
            """, nativeQuery = true)
    List<ExpenseDTO> getAll(@Param("userEmailOrUsername") String userEmailOrUsername);

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
                INNER JOIN users U ON U.id = E.user_id
                INNER JOIN category C ON C.id = E.category_id
                INNER JOIN timestamp T ON T.id = E.timestamp_id
            WHERE MONTH(E.date) = :month
                AND (U.username LIKE CONCAT('%', :userEmailOrUsername, '%')
                OR U.email LIKE CONCAT('%', :userEmailOrUsername, '%'))
            """, nativeQuery = true)
    List<ExpenseDTO> findExpensesPerMonth(
            @Param("userEmailOrUsername") String userEmailOrUsername,
            @Param("month") int month
    );

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
                    WHEN T.name = 'DAILY' THEN E.amount * DAY(LAST_DAY(month_date))
                    WHEN T.name = 'WEEKLY' THEN E.amount * FLOOR(DAY(LAST_DAY(month_date)) / 7)
                    WHEN T.name = 'MONTHLY' THEN E.amount
                    ELSE E.amount
                END
            ) as totalSpent
            FROM (
                SELECT E.*, DATE_FORMAT(CONCAT(?2, '-', ?3, '-01'), '%Y-%m-%d') AS month_date
                FROM expense E
                WHERE E.date <= LAST_DAY(CONCAT(?2, '-', ?3, '-01'))
            ) AS E
                INNER JOIN users U ON U.id = E.user_id
                INNER JOIN category C ON C.id = E.category_id
                INNER JOIN timestamp T ON T.id = E.timestamp_id
            WHERE (E.expiry_date IS NULL OR E.expiry_date > month_date)
                AND (U.username LIKE CONCAT('%', ?1, '%') OR U.email LIKE CONCAT('%', ?1, '%'))
            GROUP BY C.name
            """, nativeQuery = true)
    List<Object[]> findMonthlyCategoriesTotalSum(String userEmailOrUsername, int year, int month);


}