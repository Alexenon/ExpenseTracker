Use expenses;

SET @year = 2023;
SET @month = 8;

SELECT * FROM Expense
WHERE user_id = 5;

SELECT E.id, E.name, T.name, E.start_date, E.expire_date, E.days
FROM (
    SELECT E.*,
        DaysBetweenMonthly(DATE_FORMAT(CONCAT(@year, '-', @month, '-01'), '%Y-%m-%d'), E.start_date, E.expire_date) as `days`
    FROM expense E
) AS E
INNER JOIN users U ON U.id = E.user_id
INNER JOIN timestamp T ON T.id = E.timestamp_id
WHERE
    U.username LIKE CONCAT('%', 'alex', '%')
    AND NOT (T.name = 'ONCE' AND MONTH(E.start_date) != @month)
    AND (E.expire_date IS NULL OR E.expire_date > DATE_FORMAT(CONCAT(@year, '-', @month, '-01'), '%Y-%m-%d'));
