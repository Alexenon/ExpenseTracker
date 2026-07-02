package com.example.application.utils;

import java.time.format.DateTimeFormatter;

public class CommonFormatters {

	public static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss");
	public static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	public static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

}
