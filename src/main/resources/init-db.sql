USE expenses;

DELIMITER //

CREATE FUNCTION IF NOT EXISTS DaysInMonth(dateParam DATE)
RETURNS INT
READS SQL DATA
BEGIN
    DECLARE days INT;
    DECLARE paramMonth INT;
    DECLARE currentMonth INT;

    SET paramMonth = MONTH(dateParam);
    SET currentMonth = MONTH(CURRENT_DATE);

    IF paramMonth = currentMonth THEN
        SET days = DAY(dateParam);
    ELSEIF paramMonth < currentMonth THEN
        SET days = DAY(LAST_DAY(dateParam));
    ELSE
        SET days = 0;
    END IF;

    RETURN days;
END //

DELIMITER ;


