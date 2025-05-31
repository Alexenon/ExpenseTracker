package com.example.application.data.enums;

public enum Categories {
    SERVICES("Services"),
    FOOD_AND_DRINKS("Food & Drinks"),
    TRANSPORTATION("Transportation"),
    CLOTHING("Clothing"),
    HEALTH("Health"),
    ENTERTAINMENT("Entertainment"),
    OTHERS("Others");

    private final String displayName;

    Categories(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
