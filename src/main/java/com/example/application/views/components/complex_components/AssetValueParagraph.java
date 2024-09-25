package com.example.application.views.components.complex_components;

import com.example.application.data.models.NumberType;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Paragraph;

@Tag(Tag.P)
public class AssetValueParagraph extends Paragraph {

    private final boolean withColor;
    private double value;

    public AssetValueParagraph(double value, NumberType numberType) {
        this(value, numberType, false);
    }

    public AssetValueParagraph(double value, NumberType numberType, boolean withColor) {
        this.value = value;
        this.withColor = withColor;
        this.setText(numberType.parse(value));
        if (withColor)
            setTextClassName();
    }

    public void setValue(double value) {
        this.value = value;
        if (withColor) {
            setTextClassName();
        }
    }

    public void setColor(String colorName) {
        this.getStyle().set("color", colorName);
    }

    private void setTextClassName() {
        this.getClassNames().clear();
        this.addClassName(getClassNameByValue());
    }

    private String getClassNameByValue() {
        if (value == 0)
            return null;

        return (value > 0) ? "value-increase" : "value-decrease";
    }
}

