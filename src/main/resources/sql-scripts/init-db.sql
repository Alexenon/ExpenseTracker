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

------------------------------------------------ [DaysBetweenMonthly] --------------------------------------------------

USE expenses;

DROP FUNCTION IF EXISTS DaysBetweenMonthly;

DELIMITER //

CREATE FUNCTION DaysBetweenMonthly(dateParam DATE, startDate DATE, expireDate DATE)
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
    DaysBetweenMonthly('2023-08-01', '2023-08-18', NULL) as 'Result 1', -- starts today(update start date)
	DaysBetweenMonthly('2023-08-01', '2023-08-10', '2023-08-05') as 'Result 0', -- expired before start_date
    DaysBetweenMonthly('2023-08-01', '2023-08-01', '2023-08-01') as 'Result 0', -- starts, expires same day
    DaysBetweenMonthly('2023-08-01', '2023-08-10', '2020-01-01') as 'Result 0', -- expired ago
    DaysBetweenMonthly('2023-08-01', '2022-01-01', '2023-08-10') as 'Result 9', -- expired recently, started ago
    DaysBetweenMonthly('2023-08-01', '2023-08-10', '2023-08-15') as 'Result 5', -- expired recently, started this month
    DaysBetweenMonthly('2023-08-01', '2023-08-01', '2023-08-31') as 'Result DAYS', -- starts, expires soon
    DaysBetweenMonthly('2023-08-01', '2023-02-02', '2023-08-31') as 'Result DAYS', -- starts ago, expires soon
    DaysBetweenMonthly('2023-08-01', '2023-02-02', '2024-12-31') as 'Result DAYS', -- starts ago, expires late
	DaysBetweenMonthly('2023-08-01', '2023-08-01', NULL) as 'Result DAYS', -- starts this month without expiration
    DaysBetweenMonthly('2023-08-01', '2023-08-08', NULL) as 'Result DAYS-7', -- starts this month without expiration
    DaysBetweenMonthly('2023-08-01', '2022-01-01', NULL) as 'Result DAYS' -- starts ago without expiration
;

------------------------------------------------ [] --------------------------------------------------



