package com.example.application.repositories;

import com.example.application.data.dtos.ExpenseDTO;
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
                E.description, T.name as 'Timestamp', E.start_date
            FROM expense E
            INNER JOIN category C ON C.id = E.category_id
            INNER JOIN timestamp T ON T.id = E.timestamp_id
            """, nativeQuery = true)
    List<ExpenseDTO> getAll();

    // TODO: ADD VIEW TO EXCLUDE ADDING ALIASES EVERYWHERE
    @Query(value = """
            SELECT E.id, E.name, E.amount, C.name as 'Category',
                E.description, T.name as 'Timestamp', E.start_date as 'startDate', E.expire_date as 'expireDate'
            FROM expense E
                INNER JOIN users U ON U.id = E.user_id
                INNER JOIN category C ON C.id = E.category_id
                INNER JOIN timestamp T ON T.id = E.timestamp_id
            WHERE U.username = :userEmailOrUsername OR U.email = :userEmailOrUsername
            """, nativeQuery = true)
    List<ExpenseDTO> getAll(@Param("userEmailOrUsername") String userEmailOrUsername);

    @Query(value = """
            SELECT E.id, E.name, E.amount, C.name AS 'Category',
                E.description, T.name AS 'Timestamp', E.start_date
            FROM expense E
            INNER JOIN category C ON C.id = E.category_id
            INNER JOIN timestamp T ON T.id = E.timestamp_id
            WHERE MONTH(E.start_date) = :month
            """, nativeQuery = true)
    List<ExpenseDTO> findExpensesPerMonth(@Param("month") int month);

    @Query(value = """
            SELECT E.id, E.name, E.amount, C.name AS 'Category',
                E.description, T.name AS 'Timestamp', E.start_date
            FROM expense E
            INNER JOIN category C ON C.id = E.category_id
            INNER JOIN timestamp T ON T.id = E.timestamp_id
            WHERE YEAR(E.start_date) = :year
            """, nativeQuery = true)
    List<ExpenseDTO> findExpensesPerYear(@Param("year") int year);

    @Query(value = """
            SELECT E.id, E.name, E.amount, C.name AS 'Category',
                E.description, T.name AS 'Timestamp', E.start_date
            FROM expense E
            INNER JOIN category C ON C.id = E.category_id
            INNER JOIN timestamp T ON T.id = E.timestamp_id
            WHERE C.name LIKE CONCAT('%', :categoryName, '%')
            """, nativeQuery = true)
    List<ExpenseDTO> findByCategory(@Param("categoryName") String categoryName);

    @Query(value = """
            SELECT C.name, SUM(
            	CASE
            		WHEN T.name = 'DAILY' THEN FLOOR(E.amount * days)
            		WHEN T.name = 'WEEKLY' THEN FLOOR(E.amount * (days / 7))
            		WHEN T.name = 'MONTHLY' THEN FLOOR(E.amount)
            		ELSE FLOOR(E.amount)
            	END
            ) as totalSpentPerMonth
            FROM (
                SELECT E.*,
                    MonthValidDays(DATE_FORMAT(CONCAT(?2, '-', ?3, '-01'), '%Y-%m-%d'), E.start_date, E.expire_date) as `days`
                FROM expense E
            ) AS E
            INNER JOIN users U ON U.id = E.user_id
            INNER JOIN timestamp T ON T.id = E.timestamp_id
            INNER JOIN category C ON C.id = E.category_id
            WHERE
                (U.username = ?1 OR U.email = ?1)
                AND NOT (T.name = 'ONCE' AND MONTH(E.start_date) != ?3)
                AND (E.expire_date IS NULL OR E.expire_date > DATE_FORMAT(CONCAT(?2, '-', ?3, '-01'), '%Y-%m-%d'))
            GROUP BY C.name
            """, nativeQuery = true)
    List<Object[]> findMonthlyCategoriesTotalSum(String userEmailOrUsername, int year, int month);

    @Query(value = """
        SELECT C.name, SUM(
            CASE
                WHEN T.name = 'DAILY' THEN E.amount * DAYOFYEAR(LAST_DAY(year_date))
                WHEN T.name = 'WEEKLY' THEN E.amount * FLOOR(DAYOFYEAR(LAST_DAY(year_date)) / 7)
                WHEN T.name = 'MONTHLY' THEN E.amount * 12
                WHEN T.name = 'YEARLY' THEN E.amount
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
            INNER JOIN timestamp T ON T.id = E.timestamp_id
        WHERE (E.expire_date IS NULL OR E.expire_date > year_date)
            AND (U.username = ?1 OR U.email = ?1)
        GROUP BY C.name
        """, nativeQuery = true)
    List<Object[]> findYearlyCategoriesTotalSum(String userEmailOrUsername, int year);

    @Query(value = """
            SELECT C.name, SUM(
                CASE
                    WHEN T.name = 'DAILY' THEN E.amount * DAY(LAST_DAY(month_date))
                    WHEN T.name = 'WEEKLY' THEN E.amount * FLOOR(DAY(LAST_DAY(month_date)) / 7)
                    WHEN T.name = 'MONTHLY' THEN E.amount
                    ELSE E.amount
                END
            ) as totalSpent
            FROM expense E
                INNER JOIN users U ON U.id = E.user_id
                INNER JOIN category C ON C.id = E.category_id
                INNER JOIN timestamp T ON T.id = E.timestamp_id
            WHERE (E.expire_date IS NULL OR E.expire_date > month_date)
                AND (U.username = ?1 OR U.email = ?1)
            GROUP BY C.name
            """, nativeQuery = true)
    List<Object[]> findTotalSumByCategories(String userEmailOrUsername, int year, int month);

}