package com.example.application.views.components.complex_components;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.theme.lumo.LumoIcon;
import lombok.Getter;

@Tag(Tag.DIV)
public class PriceChangeBadge extends Div {

    @Getter
    private Icon directionIcon;
    @Getter
    private Paragraph percentage;

    public PriceChangeBadge(double priceChangeValue) {
        String formattedPercentage = String.format("%.2f%%", Math.abs(priceChangeValue));

        addClassName("price-change-badge");
        directionIcon = LumoIcon.MINUS.create();
        percentage = new Paragraph(formattedPercentage);
        update(priceChangeValue);
        add(directionIcon, percentage);
    }

    public void update(double priceChangeValue) {
        if (priceChangeValue > 0) {
            directionIcon = LumoIcon.CHEVRON_UP.create();
            directionIcon.setClassName("price-text-green");
            addClassName("price-badge-green");
        } else if (priceChangeValue < 0) {
            directionIcon = LumoIcon.CHEVRON_DOWN.create();
            directionIcon.setClassName("price-text-red");
            addClassName(("price-badge-red"));
        } else {
            directionIcon = LumoIcon.MINUS.create();
            directionIcon.removeClassNames("price-text-green", "price-text-red");
            removeClassNames("price-badge-green", "price-badge-red");
        }
    }


}
