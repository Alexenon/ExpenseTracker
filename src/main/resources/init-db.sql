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

CREATE FUNCTION IF NOT EXISTS DaysBetweenMonthly(dateParam DATE, expireDate DATE)
RETURNS INT
READS SQL DATA
BEGIN
    DECLARE daysPassed INT;
    DECLARE result INT;

    DECLARE areSameMonthAndYear BOOLEAN;

    SET areSameMonthAndYear = (YEAR(dateParam) = YEAR(expireDate) AND MONTH(dateParam) = MONTH(expireDate));
    SET daysPassed = DaysPassedInMonth(dateParam);

    IF areSameMonthAndYear THEN
        IF DAY(expireDate) >= daysPassed THEN
            SET result = daysPassed;
        ELSE
            SET result = DAY(expireDate);
        END IF;
    ELSEIF expireDate <= dateParam THEN
        SET result = 0;
    ELSE
        SET result = daysPassed;
    END IF;

    RETURN result;
END //

DELIMITER ;

------------------------------------------------ [DaysBetweenMonthly] --------------------------------------------------

DELIMITER //

DROP function DaysBetweenMonthly;

CREATE FUNCTION IF NOT EXISTS DaysBetweenMonthly(dateParam DATE, expireDate DATE)
RETURNS INT
READS SQL DATA
BEGIN
    DECLARE daysPassed INT;
    DECLARE result INT;
    DECLARE areSameMonthAndYear BOOLEAN;

    SET areSameMonthAndYear = (YEAR(dateParam) = YEAR(expireDate) AND MONTH(dateParam) = MONTH(expireDate));
    SET daysPassed = DaysPassedInMonth(dateParam);

    IF expireDate <= dateParam THEN
        SET result = 0;
    ELSEIF areSameMonthAndYear THEN
        IF DAY(expireDate) >= daysPassed THEN
            SET result = daysPassed;
        ELSE
            SET result = DATEDIFF(expireDate, dateParam);
        END IF;
    ELSE
        SET result = daysPassed;
    END IF;

    RETURN result;
END //

DELIMITER ;

------------------------------------------------ [] --------------------------------------------------


