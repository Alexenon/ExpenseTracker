package com.example.application.tag;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum DefaultTags {
	MUST_HAVES("Must haves"),
	WANTS("Wants"),
	COFFEE("Coffee"),
	HOUSE_BILLS("House Bills");

	private final String displayName;

	DefaultTags(String displayName) {
		this.displayName = displayName;
	}

	public static List<String> getAllTagNames() {
		return Arrays.stream(values())
				.map(DefaultTags::getDisplayName)
				.toList();
	}
}