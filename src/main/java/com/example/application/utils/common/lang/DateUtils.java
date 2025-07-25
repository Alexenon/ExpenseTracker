package com.example.application.utils.common.lang;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

public class DateUtils {

    public static boolean isInSameMonthAndYear(LocalDate dateToCheck, LocalDate currentDate) {
        return dateToCheck.getYear() == currentDate.getYear() && dateToCheck.getMonth() == currentDate.getMonth();
    }

    public static LocalDate firstDayOfMonth(LocalDate date) {
        return date.with(TemporalAdjusters.firstDayOfMonth());
    }

    public static LocalDate lastDayOfMonth(LocalDate date) {
        return date.with(TemporalAdjusters.lastDayOfMonth());
    }

    public static LocalDate lastDayOfNextMonth(LocalDate date) {
        LocalDate firstDayOfNextMonth = date.with(TemporalAdjusters.firstDayOfNextMonth());
        return lastDayOfMonth(firstDayOfNextMonth);
    }

	public static long daysBetween(LocalDate buyDate, LocalDate sellDate) {
		return ChronoUnit.DAYS.between(buyDate, sellDate);
	}

}
