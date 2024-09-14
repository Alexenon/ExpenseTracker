package com.example.application.repositories;

import com.example.application.data.dtos.ExpenseDTO;
import com.example.application.data.dtos.projections.MonthlyExpensesGroupedByName;
import com.example.application.data.dtos.projections.MonthlyExpensesProjection;
import com.example.application.data.dtos.projections.MonthlyTotalSpentGroupedByCategory;
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

    /**
     * <h3> NOTE: Monthly expenses however are calculated starting from first day of selected month,
     * till current day, and not till the end of current month.
     *
     * <p>
     * So if it's second day of month, the result will display just the amount of money user managed to spend
     * till this day. Other expenses will be displayed when the expenses date is triggered.
     *
     * @return sql table with 2 fields -> categoryName, totalSpentPerMonth
     */
    @Query(value = """
            SELECT C.name as categoryName, SUM(
            	CASE
            		WHEN E.timestamp = 'DAILY' THEN FLOOR(E.amount * days)
            		WHEN E.timestamp = 'WEEKLY' THEN FLOOR(E.amount * FLOOR(days / 7))
            		WHEN E.timestamp = 'MONTHLY' THEN FLOOR(E.amount)
            		ELSE FLOOR(E.amount)
            	END
            ) as totalSpentPerMonth
            FROM (
                SELECT E.*,
                    MonthValidDays(DATE_FORMAT(CONCAT(?2, '-', ?3, '-01'), '%Y-%m-%d'), E.start_date, E.expire_date) as `days`
                FROM expense E
            ) AS E
            INNER JOIN users U ON U.id = E.user_id
            INNER JOIN category C ON C.id = E.category_id
            WHERE
                (U.username = ?1 OR U.email = ?1)
                AND NOT (E.timestamp = 'ONCE' AND MONTH(E.start_date) != ?3)
                AND (E.expire_date IS NULL OR E.expire_date > DATE_FORMAT(CONCAT(?2, '-', ?3, '-01'), '%Y-%m-%d'))
            GROUP BY C.name
            """, nativeQuery = true)
    List<MonthlyTotalSpentGroupedByCategory> totalSpentPerMonthGroupedByCategory(String userEmailOrUsername, int year, int month);

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

    @Query(value = """
            SELECT C.name, SUM(
                CASE
                    WHEN E.timestamp = 'DAILY' THEN E.amount * DAY(LAST_DAY(month_date))
                    WHEN E.timestamp = 'WEEKLY' THEN E.amount * FLOOR(DAY(LAST_DAY(month_date)) / 7)
                    WHEN E.timestamp = 'MONTHLY' THEN E.amount
                    ELSE E.amount
                END
            ) as totalSpent
            FROM expense E
                INNER JOIN users U ON U.id = E.user_id
                INNER JOIN category C ON C.id = E.category_id
            WHERE (E.expire_date IS NULL OR E.expire_date > month_date)
                AND (U.username = ?1 OR U.email = ?1)
            GROUP BY C.name
            """, nativeQuery = true)
    List<Object[]> findTotalSumByCategories(String userEmailOrUsername, int year, int month);


    @Query(value = """
            WITH RECURSIVE DateSeries AS (
                SELECT E.name AS 'expenseName', C.name AS 'categoryName', E.amount,
                       MonthValidDays(CAST(CONCAT(:year, '-', :month, '-01') AS DATE), E.start_date, E.expire_date) AS `days`,
                       E.timestamp, 1 AS occurrence
                FROM expense E
                INNER JOIN users U ON U.id = E.user_id
                INNER JOIN category C ON C.id = E.category_id
                WHERE (U.username = :userEmailOrUsername OR U.email = :userEmailOrUsername)
                AND NOT (E.timestamp = 'ONCE' AND MONTH(E.start_date) != :month)
                AND (E.expire_date IS NULL OR E.expire_date > CAST(CONCAT(:year, '-', :month, '-01') AS DATE))
                
                UNION ALL
                
                SELECT expenseName, categoryName, amount, days, timestamp, occurrence + 1
                FROM DateSeries
                WHERE timestamp = 'DAILY' AND occurrence < days
                
                UNION ALL
                
                SELECT expenseName, categoryName, amount, days, timestamp, occurrence + 1
                FROM DateSeries
                WHERE timestamp = 'WEEKLY' AND occurrence < FLOOR(days / 7)
            )
            SELECT expenseName, categoryName,
                   CASE
                       WHEN timestamp = 'DAILY' THEN FLOOR(amount)
                       WHEN timestamp = 'WEEKLY' THEN FLOOR(amount)
                       ELSE FLOOR(amount)
                   END AS totalAmount, occurrence
            FROM DateSeries
            WHERE timestamp IN ('DAILY', 'WEEKLY', 'MONTHLY')
            ORDER BY expenseName, occurrence
            """, nativeQuery = true)
    List<MonthlyExpensesGroupedByName> totalSpentPerMonthGroupedByExpenseName(String userEmailOrUsername, Integer year, Integer month);

    @Procedure(procedureName = "GetMonthlyExpenses")
    List<MonthlyExpensesProjection> findMonthlyExpenses(@Param("username") String username,
                                                        @Param("date") LocalDate currentDate);

    @Procedure(procedureName = "GetMonthlyExpensesByCategory")
    List<MonthlyExpensesProjection> findMonthlyExpenses(@Param("username") String username,
                                                        @Param("date") LocalDate date,
                                                        @Param("category") String categoryName);


}

