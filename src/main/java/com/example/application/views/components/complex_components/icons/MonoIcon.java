package com.example.application.views.components.complex_components.icons;

import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Svg;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.icon.AbstractIcon;

import java.util.Objects;

@Tag("mono-icon")
@JavaScript("//code.iconify.design/1/1.0.6/iconify.min.js")
public class MonoIcon extends AbstractIcon<MonoIcon> implements HasComponents {

    public MonoIcon(PictogramIcon icon) {
        Svg svg = new Svg();
        svg.addClassName("iconify");
        svg.addClassName("mdi-flip-v");

        String iconName = String.format("mdi-%s", icon.name().toLowerCase().replace('_', '-'));
        svg.getElement().setAttribute("data-icon", iconName);

        add(svg);
    }

    @Override
    public String getColor() {
        return this.getStyle().get("color");
    }

    @Override
    public void setColor(String color) {
        if (color == null) {
            this.getStyle().remove("color");
        } else {
            this.getStyle().set("color", color);
        }
    }

    @Override
    public void setSize(String size) {
        if (size == null) {
            this.getStyle().remove("font-size");
        } else {
            this.getStyle().set("font-size", size);
        }
    }

    public void setSize(float size, Unit unit) {
        Objects.requireNonNull(unit);

        if (size < 0) {
            throw new IllegalArgumentException("Size cannot be negative");
        } else {
            this.setSize(size + unit.toString());
        }
    }

}
