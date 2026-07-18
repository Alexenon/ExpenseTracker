package com.example.application.category;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum DefaultCategories {
    SERVICES("Services", "BITCOIN"),
    FOOD_AND_DRINKS("Food & Drinks", "CURRENCY_BTC"),
    TRANSPORTATION("Transportation", "FINANCE"),
    CLOTHING("Clothing", "FILE_SIGN"),
    HEALTH("Health", "INVOICE_CLOCK_OUTLINE"),
    ENTERTAINMENT("Entertainment", "PIGGY_BANK_OUTLINE"),
    OTHERS("Others", "SHIELD_LOCK");

    private final String displayName;
    private final String iconName;

    DefaultCategories(String displayName, String iconName) {
        this.displayName = displayName;
		this.iconName = iconName;
    }

	public static List<String> getAllCategoryNames() {
		return Arrays.stream(values())
				.map(DefaultCategories::getDisplayName)
				.toList();
	}
	
}
