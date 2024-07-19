package com.example.application.views.components.complex_components;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.theme.lumo.LumoIcon;
import lombok.Getter;

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

    @Getter
    private PriceBadge.Color color;

    public PriceBadge(double value, boolean priceType) {
        String formattedPrice = String.format("$%.2f", value);
        String formattedPercentage = String.format("%.2f%%", Math.abs(value));

        textField = priceType
                ? new Paragraph(formattedPrice)
                : new Paragraph(formattedPercentage);

        PriceBadge.Color colorToUse;
        if (value == 0) {
            colorToUse = Color.DEFAULT;
        } else if (value > 0) {
            colorToUse = Color.GREEN;
        } else {
            colorToUse = Color.RED;
        }

        this(colorToUse, false);
    }

    private PriceBadge(PriceBadge.Color color, boolean withBackground) {
        this.color = color;
        this.withBackground = withBackground;
        initialize();
    }

    private void initialize() {
        addClassName("price-change-badge");
        addColor();
        if (withBackground)
            addBackground();

        add(directionIcon, textField);
    }

    private void addColor() {
        switch (color) {
            case GREEN -> {
                directionIcon = LumoIcon.CHEVRON_UP.create();
                addClassName("price-badge-green");
            }
            case RED -> {
                directionIcon = LumoIcon.CHEVRON_DOWN.create();
                addClassName("price-badge-red");
            }
            case DEFAULT -> {
                directionIcon = LumoIcon.MINUS.create();
                removeClassNames("price-badge-green", "price-badge-red");
            }
        }
    }

    public void addBackground() {
        this.withBackground = true;
        switch (color) {
            case GREEN -> addClassName("price-badge-green");
            case RED -> addClassName("price-badge-red");
            case DEFAULT -> removeClassNames("price-badge-green", "price-badge-red");
        }
    }

    public void removeBackground() {
        this.withBackground = false;
        removeClassNames("price-badge-green", "price-badge-red");
    }

    enum Color {
        DEFAULT, GREEN, RED
    }


}
