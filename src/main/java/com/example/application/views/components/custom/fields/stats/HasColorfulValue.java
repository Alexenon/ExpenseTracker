package com.example.application.views.components.custom.fields.stats;

import com.vaadin.flow.component.HasStyle;

public interface HasColorfulValue extends HasStyle {

    default void setColorClassName(String color) {
        getStyle().setColor(color);
    }

    default void setColorClassName(double value) {
        removeClassNames("value-increase", "value-decrease");
        String className = getClassNameByValue(value);
        if (className != null) {
            addClassName(className);
        }
    }

    private String getClassNameByValue(double value) {
        return value == 0 ? null
                : (value > 0) ? "value-increase" : "value-decrease";
    }

}
