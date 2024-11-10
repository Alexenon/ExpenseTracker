package com.example.application.views.components.complex_components;

import com.example.application.utils.common.number.DecimalFormatter;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Paragraph;

@Tag(Tag.P)
public class ProfitValueParagraph extends Paragraph {

    // TODO: Rename this to
    //  -> NumberDisplayParagraph
    //  -> NumericDisplayParagraph
    //  -> NumericValueDisplay

    private double value;
    private boolean hasColor;
    private DecimalFormatter formatter;

    public ProfitValueParagraph() {
        this(0.0, new DecimalFormatter());
    }

    public ProfitValueParagraph(double value) {
        this(value, new DecimalFormatter());
    }

    public ProfitValueParagraph(double value, boolean hasColor) {
        this(value, new DecimalFormatter(), hasColor);
    }

    public ProfitValueParagraph(double value, DecimalFormatter formatter) {
        this(value, formatter, false);
    }

    public ProfitValueParagraph(double value, DecimalFormatter formatter, boolean hasColor) {
        this.value = value;
        this.hasColor = hasColor;
        this.formatter = formatter;
        setClassNameByColor();
        setText(formatter.format(value));
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

    public void setFormatter(DecimalFormatter formatter) {
        this.formatter = formatter;
        setText(this.formatter.format(value));
    }

}

