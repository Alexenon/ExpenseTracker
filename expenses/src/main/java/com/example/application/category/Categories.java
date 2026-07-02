package com.example.application.category;

import java.util.Arrays;
import java.util.List;

public enum Categories {
    SERVICES("Services", "BITCOIN"),
    FOOD_AND_DRINKS("Food & Drinks", "CURRENCY_BTC"),
    TRANSPORTATION("Transportation", "FINANCE"),
    CLOTHING("Clothing", "FILE_SIGN"),
    HEALTH("Health", "INVOICE_CLOCK_OUTLINE"),
    ENTERTAINMENT("Entertainment", "PIGGY_BANK_OUTLINE"),
    OTHERS("Others", "SHIELD_LOCK");

    private final String displayName;
    private final String iconName;

    Categories(String displayName, String iconName) {
        this.displayName = displayName;
		this.iconName = iconName;
    }

    public String getDisplayName() {
        return displayName;
    }

	public String getIconName() {
		return iconName;
	}

	public static List<String> getAllCategoryNames() {
		return Arrays.stream(values())
				.map(Categories::getDisplayName)
				.toList();
	}
	
}
