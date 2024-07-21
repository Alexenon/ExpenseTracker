package com.example.application.views.components.complex_components;

import com.example.application.data.models.NumberType;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.theme.lumo.LumoIcon;
import lombok.Getter;
import lombok.Setter;

@Tag(Tag.DIV)
public class PriceBadge extends Div {

    @Getter
    private double value;

    @Getter
    private Icon icon;

    @Getter
    private Paragraph textField;

    public PriceBadge(double value, NumberType numberType) {
        this(value, numberType, true, true, true);
    }

    // Builder
    public PriceBadge(double value, NumberType numberType, boolean withColor, boolean withIcon, boolean withBackground) {
        this.value = value;
        this.textField = new Paragraph(numberType.parse(value));

        addClassName("price-change-badge");
        if (withIcon)
            setIcon();
        if (withColor)
            setTextColor();
        if (withBackground)
            setBackgroundColor();

        add(textField);
    }

    public void setIcon() {
        icon = getIconByValue();
        addComponentAsFirst(icon);
    }

    public void setTextColor(Color color) {
        textField.getStyle().set("color", color.getValue());
        if (icon != null)
            icon.getStyle().set("color", color.getValue());
    }

    public void setTextColor() {
        setTextColor(getTextColorByValue());
    }

    public void setBackgroundColor(Color color) {
        this.getStyle().set("background", color.getValue());
    }

    public void setBackgroundColor() {
        setBackgroundColor(getBackgroundColorByValue());
    }

    private void removeIcon() {
        icon.removeFromParent();
    }

    public void setValue(double value) {
        this.value = value;
    }

    private Color getTextColorByValue() {
        return (value == 0) ? Color.DEFAULT_TEXT_COLOR
                : ((value > 0) ? Color.GREEN_TEXT_COLOR : Color.RED_TEXT_COLOR);
    }

    private Color getBackgroundColorByValue() {
        return (value == 0) ? Color.DEFAULT_BACKGROUND_COLOR
                : ((value > 0) ? Color.GREEN_BACKGROUND_COLOR : Color.RED_BACKGROUND_COLOR);
    }

    private Icon getIconByValue() {
        return (value == 0) ? LumoIcon.MINUS.create()
                : ((value > 0) ? LumoIcon.CHEVRON_UP.create() : LumoIcon.CHEVRON_DOWN.create());
    }

    enum Color {
        // text color
        DEFAULT_TEXT_COLOR("black"),
        GREEN_TEXT_COLOR("#34b349"),
        RED_TEXT_COLOR("#f02934"),
        // background color
        DEFAULT_BACKGROUND_COLOR("white"),
        GREEN_BACKGROUND_COLOR("rgba(52, 179, 73, .1)"),
        RED_BACKGROUND_COLOR("rgba(240, 41, 52, .1)");

        @Getter
        @Setter
        private String value;

        Color(String value) {
            this.value = value;
        }

    }


}
