package com.example.application.entities.expenses;

import java.util.Arrays;
import java.util.List;

public enum ExpenseTimestamp {
	ONCE,
	DAILY,
	WEEKLY,
	MONTHLY,
	YEARLY;

	public static List<String> getTimestampNames() {
		return Arrays.stream(values()).map(ExpenseTimestamp::name).toList();
	}
}
