USE expenses;

------------------------------------------------- [DaysPassedInMonth] --------------------------------------------------

DELIMITER //

CREATE FUNCTION IF NOT EXISTS DaysPassedInMonth(dateParam DATE)
RETURNS INT
READS SQL DATA
BEGIN
    DECLARE days INT;
    DECLARE paramYear INT;
    DECLARE paramMonth INT;
    DECLARE currentYear INT;
    DECLARE currentMonth INT;

    SET paramYear = YEAR(dateParam);
    SET paramMonth = MONTH(dateParam);
    SET currentYear = YEAR(CURRENT_DATE);
    SET currentMonth = MONTH(CURRENT_DATE);

    IF paramYear = currentYear AND paramMonth = currentMonth THEN
        SET days = DAY(NOW());
    ELSEIF paramYear < currentYear OR (paramYear = currentYear AND paramMonth < currentMonth) THEN
        SET days = DAY(LAST_DAY(CONCAT(paramYear, '-', paramMonth, '-01')));
    ELSE
        SET days = 0;
    END IF;

    RETURN days;
END //

DELIMITER ;

------------------------------------------------ [MonthValidDays] --------------------------------------------------

USE expenses;

DROP FUNCTION IF EXISTS MonthValidDays;

DELIMITER //

CREATE FUNCTION MonthValidDays(dateParam DATE, startDate DATE, expireDate DATE)
RETURNS INT
READS SQL DATA
BEGIN
	DECLARE result INT;
    DECLARE daysPassed INT;
    DECLARE startedSameMonthAndYear BOOLEAN;
    DECLARE expireSameMonthAndYear BOOLEAN;

    SET daysPassed = DaysPassedInMonth(dateParam);
	SET startedSameMonthAndYear = YEAR(dateParam) = YEAR(startDate) AND MONTH(dateParam) = MONTH(startDate);
    SET expireSameMonthAndYear = YEAR(dateParam) = YEAR(expireDate) AND MONTH(dateParam) = MONTH(expireDate);

    IF expireDate <= dateParam OR expireDate <= startDate THEN
        SET result = 0;
	ELSEIF startedSameMonthAndYear AND (expireDate IS NULL OR NOT expireSameMonthAndYear) THEN
		SET result = daysPassed - DAY(startDate) + 1;
    ELSEIF expireSameMonthAndYear THEN
		IF DAY(expireDate) <= daysPassed THEN
			IF startedSameMonthAndYear THEN
				SET result = DATEDIFF(expireDate, startDate);
			ELSE
				SET result = DAY(expireDate) - 1;
			END IF;
		ELSEIF startedSameMonthAndYear THEN
			SET result = daysPassed - DAY(startDate) + 1;
        ELSE
            SET result = daysPassed;
        END IF;
    ELSE
        SET result = daysPassed;
    END IF;

    RETURN result;
END //

DELIMITER ;


SELECT
    MonthValidDays('2023-08-01', '2023-08-18', NULL) as 'Result 1', -- starts today(update start date)
	MonthValidDays('2023-08-01', '2023-08-10', '2023-08-05') as 'Result 0', -- expired before start_date
    MonthValidDays('2023-08-01', '2023-08-01', '2023-08-01') as 'Result 0', -- starts, expires same day
    MonthValidDays('2023-08-01', '2023-08-10', '2020-01-01') as 'Result 0', -- expired ago
    MonthValidDays('2023-08-01', '2022-01-01', '2023-08-10') as 'Result 9', -- expired recently, started ago
    MonthValidDays('2023-08-01', '2023-08-10', '2023-08-15') as 'Result 5', -- expired recently, started this month
    MonthValidDays('2023-08-01', '2023-08-01', '2023-08-31') as 'Result DAYS', -- starts, expires soon
    MonthValidDays('2023-08-01', '2023-02-02', '2023-08-31') as 'Result DAYS', -- starts ago, expires soon
    MonthValidDays('2023-08-01', '2023-02-02', '2024-12-31') as 'Result DAYS', -- starts ago, expires late
	MonthValidDays('2023-08-01', '2023-08-01', NULL) as 'Result DAYS', -- starts this month without expiration
    MonthValidDays('2023-08-01', '2023-08-08', NULL) as 'Result DAYS-7', -- starts this month without expiration
    MonthValidDays('2023-08-01', '2022-01-01', NULL) as 'Result DAYS' -- starts ago without expiration
;

---------------------------------------------[ FIRST_DAY ]-----------------------------------------------

DELIMITER $$

CREATE FUNCTION FIRST_DAY(input_date DATE)
RETURNS DATE
DETERMINISTIC
BEGIN
    RETURN DATE_FORMAT(input_date, '%Y-%m-01');
END$$

DELIMITER ;

-----------------------------------------[ END_DATE_FOR_MONTH ]------------------------------------------

DROP FUNCTION IF EXISTS END_DATE_FOR_MONTH;
DELIMITER $$

CREATE FUNCTION END_DATE_FOR_MONTH(
    date_to_check DATE,
    start_date DATE,
    expire_date DATE
)
RETURNS DATE
DETERMINISTIC
BEGIN
    DECLARE first_day_of_month DATE DEFAULT DATE_FORMAT(date_to_check, '%Y-%m-01');
    DECLARE result_date DATE;

    -- If start_date is in the future
    IF start_date > date_to_check THEN
        RETURN NULL;
    END IF;

    -- If start_date is in past and expire_date too
	IF start_date < date_to_check AND expire_date < first_day_of_month THEN
        RETURN NULL;
    END IF;

    -- If expire_date exists and is before or on date_to_check, return expire_date
    IF expire_date IS NOT NULL AND expire_date <= date_to_check THEN
        RETURN expire_date;
    END IF;

    -- In all other cases, return date_to_check (today)
    RETURN date_to_check;
END$$

DELIMITER ;

-----------------------------------------[ DAYS_PASSED_FOR_MONTH ]---------------------------------------

DROP FUNCTION IF EXISTS DAYS_PASSED_FOR_MONTH;
DELIMITER $$

CREATE FUNCTION DAYS_PASSED_FOR_MONTH(
    date_to_check DATE,
    start_date DATE,
    expire_date DATE
)
RETURNS INT
DETERMINISTIC
BEGIN
    DECLARE first_day_of_month DATE DEFAULT DATE_FORMAT(date_to_check, '%Y-%m-01');
    DECLARE last_day_of_month DATE DEFAULT LAST_DAY(date_to_check);
    DECLARE days_passed INT DEFAULT 0;
    DECLARE end_date DATE;

    SET end_date = END_DATE_FOR_MONTH(date_to_check, start_date, expire_date);

    -- If the end date is NULL, no days have passed
    IF end_date IS NULL THEN
        RETURN 0;
    END IF;

    -- If the start_date is before the start of the month, use the first day of the month
    IF start_date < first_day_of_month THEN
        SET start_date = first_day_of_month;
    END IF;

    -- Calculate days between start_date and the end date within this month
    RETURN DATEDIFF(end_date, start_date);
END$$

DELIMITER ;


------------------------------------------[ GetMonthlyExpenses ]-----------------------------------------

DROP PROCEDURE IF EXISTS GetMonthlyExpenses;

DELIMITER $$

CREATE PROCEDURE GetMonthlyExpenses(
	IN username VARCHAR(255),
    IN date DATE
)
BEGIN
    SELECT
        E.name,
        E.description,
        C.name AS categoryName,
        E.timestamp,
        E.amount,
        E.start_date AS startDate,
        E.expire_date AS expireDate,
        DAYS_PASSED_FOR_MONTH(date, E.start_date, E.expire_date) AS daysPassed,
        END_DATE_FOR_MONTH(date, E.start_date, E.expire_date) AS endDate,
        CASE
            WHEN E.timestamp = 'MONTHLY' OR E.timestamp = 'YEARLY' THEN 1
            WHEN E.timestamp = 'WEEKLY' THEN FLOOR(DAYS_PASSED_FOR_MONTH(date, E.start_date, E.expire_date) / 7) + 1
            ELSE DAYS_PASSED_FOR_MONTH(date, E.start_date, E.expire_date)
        END AS timesTriggered
    FROM expense E
    INNER JOIN users U ON U.id = E.user_id
    INNER JOIN category C ON C.id = E.category_id
    WHERE (U.username = username OR U.email = username)
      -- AND E.timestamp = 'ONCE' AND C.name = 'Others' -- HERE
      AND (E.start_date < END_DATE_FOR_MONTH(date, E.start_date, E.expire_date))
      AND (DAYS_PASSED_FOR_MONTH(date, E.start_date, E.expire_date) > 0)
      AND (
          (E.timestamp = 'WEEKLY' AND FLOOR(DAYS_PASSED_FOR_MONTH(date, E.start_date, E.expire_date) / 7) > 0)
          OR (E.timestamp = 'MONTHLY' AND (E.expire_date IS NULL OR E.expire_date > DAYS_PASSED_FOR_MONTH(date, E.start_date, E.expire_date)))
          OR (E.timestamp != 'WEEKLY' AND E.timestamp != 'MONTHLY')
      );
END$$

DELIMITER ;

-------------------------------------------------------------------------------------------------------
