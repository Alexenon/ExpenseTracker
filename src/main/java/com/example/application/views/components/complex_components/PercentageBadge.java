package com.example.application.views.components.complex_components;

import com.example.application.utils.common.NumberType;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.theme.lumo.LumoIcon;

/*
* TODO: Add Builder for easier creation
* */

@Tag(Tag.DIV)
public class PercentageBadge extends Div {

    private final ProfitValueParagraph textField;

    private double value;
    private boolean hasColor;
    private boolean hasBackground;
    private Icon icon;

    public PercentageBadge(double value) {
        this(value, true, true, NumberType.PERCENT);
    }

    public PercentageBadge(double value, NumberType numberType) {
        this(value, true, true, numberType);
    }

    public PercentageBadge(double value, boolean hasColor, boolean hasBackground) {
        this(value, true, true, NumberType.PERCENT);
    }

    public PercentageBadge(double value, boolean hasColor, boolean hasBackground, NumberType numberType) {
        this.value = value;
        this.hasColor = hasColor;
        this.hasBackground = hasBackground;
        this.textField = new ProfitValueParagraph(value, numberType);

        addClassName("price-change-badge");
        add(textField);
        setValue(value);
        setHasColor(hasColor);
        setHasBackground(hasBackground);
    }

    public void setValue(double value) {
        this.value = value;
        textField.setValue(value);
        setIcon();
        setBackgroundClassName();
    }

    public void setIcon() {
        icon = getIconByValue();
        icon.removeFromParent();
        addComponentAsFirst(icon);
    }

    public void removeIcon() {
        icon.removeFromParent();
    }

    private void setBackgroundClassName() {
        removeClassNames("value-increase-bg", "value-decrease-bg");
        String className = getBackgroundClassByValue();
        if (hasBackground && !className.isEmpty()) {
            addClassName(className);
        }
    }

    private String getBackgroundClassByValue() {
        if (value == 0)
            return "";

        return (value > 0) ? "value-increase-bg" : "value-decrease-bg";
    }

    private Icon getIconByValue() {
        if (value < 0) {
            Icon icon = LumoIcon.CHEVRON_DOWN.create();
            icon.getStyle().set("margin-bottom", "5px");
            return icon;
        }

        if (value > 0)
            return LumoIcon.CHEVRON_UP.create();

        return LumoIcon.MINUS.create();
    }

    public boolean isHasColor() {
        return hasColor;
    }

    public void setHasColor(boolean hasColor) {
        this.hasColor = hasColor;
        textField.setHasColor(hasColor);
        icon.setClassName(textField.getClassName());
    }

    public boolean isHasBackground() {
        return hasBackground;
    }

    public void setHasBackground(boolean hasBackground) {
        this.hasBackground = hasBackground;
        setBackgroundClassName();
    }

    public ProfitValueParagraph getTextField() {
        return textField;
    }

    public void setFormatter(NumberType numberType) {
        textField.setFormatter(numberType);
    }
}
