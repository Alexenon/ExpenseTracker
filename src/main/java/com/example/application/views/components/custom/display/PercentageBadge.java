package com.example.application.views.components.custom.display;

import com.example.application.utils.common.number.DecimalFormatter;
import com.example.application.utils.common.number.PercentageFormatter;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.theme.lumo.LumoIcon;

@Tag(Tag.DIV)
public class PercentageBadge extends Div {

    private final NumericValueParagraph textField;

    private double value;
    private boolean hasColor;
    private boolean hasBackground;
    private Icon icon;

    public PercentageBadge(double value) {
        this(value, true, true, new PercentageFormatter());
    }

    public PercentageBadge(double value, DecimalFormatter formatter) {
        this(value, true, true, formatter);
    }

    public PercentageBadge(double value, boolean hasColor, boolean hasBackground) {
        this(value, true, true, new PercentageFormatter());
    }

    public PercentageBadge(double value, boolean hasColor, boolean hasBackground, DecimalFormatter formatter) {
        this.value = value;
        this.hasColor = hasColor;
        this.hasBackground = hasBackground;
        this.textField = new NumericValueParagraph(value, formatter);

        addClassName("price-change-badge");
        add(textField);
        setValue(value);
        setHasColor(hasColor);
        setHasBackground(hasBackground);
    }

    public void setValue(double value) {
        this.value = value;
        if (hasColor)
            textField.setColorClassName(value);
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

    public boolean hasColor() {
        return hasColor;
    }

    public void setHasColor(boolean hasColor) {
        this.hasColor = hasColor;
        if (hasColor)
            textField.setColorClassName(value);
        icon.setClassName(textField.getClassName());
    }

    public boolean hasBackground() {
        return hasBackground;
    }

    public void setHasBackground(boolean hasBackground) {
        this.hasBackground = hasBackground;
        setBackgroundClassName();
    }

    public NumericValueParagraph getTextField() {
        return textField;
    }

    public void setFormatter(DecimalFormatter formatter) {
        textField.setFormatter(formatter);
    }
}
