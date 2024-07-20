package com.example.application.views.components.complex_components;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.theme.lumo.LumoIcon;
import lombok.Getter;
import lombok.Setter;

// type: price, percentage
// background: on/off

@Tag(Tag.DIV)
public class PriceBadge extends Div {

    @Getter
    private Icon directionIcon;

    @Getter
    private Paragraph textField;

    @Getter
    private boolean withBackground;

    public PriceBadge(double value, boolean priceType) {
        String formattedPrice = String.format("$%.2f", value);
        String formattedPercentage = String.format("%.2f%%", Math.abs(value));

        withBackground = false;
        directionIcon = getIcon(value);
        textField = priceType
                ? new Paragraph(formattedPrice)
                : new Paragraph(formattedPercentage);


        addClassName("price-change-badge");
        setTextColor(getTextColorByValue(value));
        setBackgroundColor(getBackgroundColorByValue(value));
        add(directionIcon, textField);
    }

    public void removeTextColor() {
        setTextColor(Color.DEFAULT_TEXT_COLOR);
    }

    public void removeBackground() {
        this.withBackground = false;
        setBackgroundColor(Color.DEFAULT_BACKGROUND_COLOR);
    }

    private void removeIcon() {
        directionIcon.removeFromParent();
    }

    public void setTextColor(Color color) {
        textField.getStyle().set("color", color.getValue());
        directionIcon.getStyle().set("color", color.getValue());
    }

    public void setBackgroundColor(Color color) {
        this.getStyle().set("background", color.getValue());
    }

    private Color getTextColorByValue(double value) {
        return (value == 0) ? Color.DEFAULT_TEXT_COLOR
                : ((value > 0) ? Color.GREEN_TEXT_COLOR : Color.RED_TEXT_COLOR);
    }

    private Color getBackgroundColorByValue(double value) {
        return (value == 0) ? Color.DEFAULT_BACKGROUND_COLOR
                : ((value > 0) ? Color.GREEN_BACKGROUND_COLOR : Color.RED_BACKGROUND_COLOR);
    }

    private Icon getIcon(double value) {
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
