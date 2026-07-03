Use expenses;

SET @year = 2023;
SET @month = 8;

-- See all expenses by month with details
SELECT E.id, E.name, C.name, T.name, E.amount, days, SUM(
	CASE
		WHEN T.name = 'DAILY' THEN FLOOR(E.amount * days)
		WHEN T.name = 'WEEKLY' THEN FLOOR(E.amount * FLOOR((days / 7)))
		WHEN T.name = 'MONTHLY' THEN FLOOR(E.amount)
		ELSE FLOOR(E.amount)
	END
) as totalSpent
FROM (
    SELECT E.*,
        MonthValidDays(DATE_FORMAT(CONCAT(@year, '-', @month, '-01'), '%Y-%m-%d'), E.start_date, E.expire_date) as `days`
    FROM expense E
) AS E
INNER JOIN users U ON U.id = E.user_id
INNER JOIN timestamp T ON T.id = E.timestamp_id
INNER JOIN category C ON C.id = E.category_id
WHERE
    U.username LIKE CONCAT('%', 'alex', '%')
    AND NOT (T.name = 'ONCE' AND MONTH(E.start_date) != @month)
    AND (E.expire_date IS NULL OR E.expire_date > DATE_FORMAT(CONCAT(@year, '-', @month, '-01'), '%Y-%m-%d'))
GROUP BY E.name;