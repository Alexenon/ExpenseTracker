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

    IF expireDate <= dateParam THEN
        SET result = 0;
	ELSEIF startedSameMonthAndYear AND NOT expireSameMonthAndYear THEN
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

------------------------------------------------ [] --------------------------------------------------



