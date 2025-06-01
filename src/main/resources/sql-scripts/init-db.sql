USE income_mate_db;

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

DROP FUNCTION IF EXISTS FIRST_DAY;
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

-- ==============================================================
-- @returns INT or NULL
--     - returns NULL if:
--         - startdate is in the future
--         - both start_date and end_date are in the past (expired)
--     - Otherwise returns days from startdate until end of month
-- ==============================================================
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

-- ================================================================
-- @returns INT
--     - Returns 0 if:
--         - startdate is in the future
--         - both startdate and enddate are in the past (expired)
--     - Otherwise returns the number of days passed that passed
--       in the provided month in range from start_date to end_date
-- ================================================================
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


----------------------------------------------[ GetMonthlyExpenses ]----------------------------------------------------

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
        MONTHLY_TIMES_EXPENSE_TRIGGERED(date, E.start_date, E.expire_date, E.timestamp) as timesTriggered
    FROM expenses E
    INNER JOIN users U ON U.id = E.user_id
    INNER JOIN categories C ON C.id = E.category_id
    WHERE
		(U.username = username OR U.email = username)
		AND MONTHLY_TIMES_EXPENSE_TRIGGERED(date, E.start_date, E.expire_date, E.timestamp) > 0;
END$$
DELIMITER ;

------------------------------------------[ MONTHLY_TIMES_EXPENSE_TRIGGERED ]-------------------------------------------

DROP FUNCTION IF EXISTS MONTHLY_TIMES_EXPENSE_TRIGGERED;
DELIMITER $$

-- ================================================================
-- @returns INT
--     - Returns 0 if:
--         - startdate is in the future
--         - both startdate and enddate are in the past (expired)
--     - Otherwise returns the number of days passed that passed
--       in the provided month in range from start_date to end_date
-- ================================================================
CREATE FUNCTION MONTHLY_TIMES_EXPENSE_TRIGGERED(
    date_to_check DATE,
    start_date DATE,
    expire_date DATE,
	expense_timestamp VARCHAR(255)
)
RETURNS INT
DETERMINISTIC
BEGIN
    DECLARE first_day_of_month DATE DEFAULT DATE_FORMAT(date_to_check, '%Y-%m-01');
    DECLARE last_day_of_month DATE DEFAULT LAST_DAY(date_to_check);
    DECLARE days_passed INT DEFAULT 0;
    DECLARE end_date DATE;
    DECLARE result INT DEFAULT 0;

    SET end_date = END_DATE_FOR_MONTH(date_to_check, start_date, expire_date);

	-- If the end date is NULL, is expired or in future
	IF end_date IS NULL THEN
		RETURN 0;
	END IF;

	-- If today is the trigger date
	IF start_date = CURDATE() THEN
		RETURN 1;
	END IF;

    IF expense_timestamp = 'DAILY' THEN
        SET result = DATEDIFF(end_date, GREATEST(start_date, first_day_of_month));
    ELSEIF expense_timestamp = 'WEEKLY' THEN
        SET result = WEEKS_PASSED(FIRST_WEEKDAY_OF_MONTH(date_to_check), end_date);
    ELSE
        SET result = 1;  -- For Once, Monthly, Yearly
    END IF;

    RETURN result;
END$$
DELIMITER ;


------------------------------------------[ MONTHLY_TIMES_EXPENSE_TRIGGERED ]-------------------------------------------


DROP FUNCTION IF EXISTS WEEKS_PASSED;
DELIMITER //

-- ================================================================
-- @returns INT
--     - Number of specific weekdays passed from start_date to end_date
--     - The weekday is taken from the end_date (e.g., Monday if end_date is Monday)
-- ================================================================
CREATE FUNCTION WEEKS_PASSED(start_date DATE, end_date DATE)
RETURNS INT
DETERMINISTIC
BEGIN
  DECLARE target_weekday INT;
  DECLARE first_target_date DATE;

  -- If end_date < start_date, no weeks passed
  IF end_date < start_date THEN
    RETURN 0;
  END IF;

  SET target_weekday = WEEKDAY(end_date);  -- 0=Monday, ..., 6=Sunday

  -- Find first date >= start_date with same weekday as end_date
  SET first_target_date = start_date + INTERVAL ((7 + target_weekday - WEEKDAY(start_date)) % 7) DAY;

  -- If that weekday hasn't occurred yet before end_date
  IF first_target_date > end_date THEN
    RETURN 0;
  END IF;

  RETURN FLOOR(DATEDIFF(end_date, first_target_date) / 7) + 1;
END //

DELIMITER ;


-------------------------------------------------[ FIRST_WEEKDAY_OF_MONTH ]---------------------------------------------


DROP FUNCTION IF EXISTS FIRST_WEEKDAY_OF_MONTH;
DELIMITER //

-- ======================================================================
-- @returns DATE
--     - The first occurrence of the weekday from reference_date
--       within the same month as the reference_date
--     - Example: reference_date = '2024-06-20' (Thursday),
--       returns '2024-06-06' (first Thursday of June)
-- ======================================================================
CREATE FUNCTION FIRST_WEEKDAY_OF_MONTH(reference_date DATE)
RETURNS DATE
DETERMINISTIC
BEGIN
  DECLARE start_of_month DATE;
  DECLARE target_dow INT;
  DECLARE start_dow INT;
  DECLARE days_to_add INT;

  -- First day of the month of the reference_date
  SET start_of_month = DATE_FORMAT(reference_date, '%Y-%m-01');

  -- Normalize both to 0=Sunday, ..., 6=Saturday
  SET target_dow = (DAYOFWEEK(reference_date) + 6) % 7;
  SET start_dow = (DAYOFWEEK(start_of_month) + 6) % 7;

  SET days_to_add = (7 + target_dow - start_dow) % 7;

  RETURN DATE_ADD(start_of_month, INTERVAL days_to_add DAY);
END //
DELIMITER ;


-------------------------------------------------[ ??? ]---------------------------------------------