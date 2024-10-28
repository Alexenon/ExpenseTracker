package com.example.application.views.components.complex_components;

import com.example.application.data.models.NumberType;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Paragraph;

@Tag(Tag.P)
public class ProfitValueParagraph extends Paragraph {

    private double value;
    private boolean hasColor;

    public ProfitValueParagraph() {
        this(0.0, NumberType.CURRENCY);
    }

    public ProfitValueParagraph(double value) {
        this(value, NumberType.CURRENCY);
    }

    public ProfitValueParagraph(double value, NumberType numberType) {
        this(value, numberType, false);
    }

    public ProfitValueParagraph(double value, NumberType numberType, boolean hasColor) {
        this.value = value;
        this.hasColor = hasColor;
        this.setText(numberType.parse(value));
        setClassNameByColor();
    }

    public void setValue(double value) {
        this.value = value;
        setClassNameByColor();
    }

    public void setHasColor(boolean withColor) {
        this.hasColor = withColor;
        setClassNameByColor();
    }

    private void setClassNameByColor() {
        removeClassNames("value-increase", "value-decrease");
        String className = getClassNameByValue();
        if (hasColor && !className.isEmpty()) {
            addClassName(className);
        }
    }

    private String getClassNameByValue() {
        if (value == 0)
            return "";

        return (value > 0) ? "value-increase" : "value-decrease";
    }
}

