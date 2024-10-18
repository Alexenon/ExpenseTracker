package com.example.application.views.components.complex_components;

import com.example.application.data.models.NumberType;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Paragraph;

@Tag(Tag.P)
public class ProfitValueParagraph extends Paragraph {

    private double value;
    private boolean withColor;

    public ProfitValueParagraph(double value) {
        this(value, NumberType.CURRENCY);
    }

    public ProfitValueParagraph(double value, NumberType numberType) {
        this(value, numberType, false);
    }

    public ProfitValueParagraph(double value, NumberType numberType, boolean withColor) {
        this.value = value;
        this.withColor = withColor;
        this.setText(numberType.parse(value));
        setClassNameByColor();
    }

    public void setValue(double value) {
        this.value = value;
        setClassNameByColor();
    }

    public void setApplyColor(boolean withColor) {
        this.withColor = withColor;
        setClassNameByColor();
    }

    private void setClassNameByColor() {
        removeClassNames("value-increase", "value-decrease");
        if (withColor) {
            addClassName(getClassNameByValue());
        }
    }

    private String getClassNameByValue() {
        if (value == 0)
            return null;

        return (value > 0) ? "value-increase" : "value-decrease";
    }
}

