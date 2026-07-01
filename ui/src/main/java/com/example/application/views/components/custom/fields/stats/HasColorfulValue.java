package com.example.application.views.components.custom.fields.stats;

import com.vaadin.flow.component.HasStyle;

import java.math.BigDecimal;

public interface HasColorfulValue extends HasStyle {

    default void setColorClassName(String color) {
        getStyle().setColor(color);
    }

    default void setColorClassName(BigDecimal value) {
        removeClassNames("value-increase", "value-decrease");
        String className = getClassNameByValue(value);
        if (className != null) {
            addClassName(className);
        }
    }

    private String getClassNameByValue(BigDecimal value) {
        return value.signum() == 0 ? null
                : (value.signum() > 0) ? "value-increase" : "value-decrease";
    }

}
